import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

var originIssueLink = mutableListOf<HTMLConverter.IssueLink>()
val newIssueLink = mutableListOf<HTMLConverter.IssueLink>()

@Composable
@Preview
fun App() {
    var inputText by remember { mutableStateOf("") }
    var jsonInputSelected by remember { mutableStateOf(false) }
    var html24HoursSelected by remember { mutableStateOf(false) }
    var html90DaysSelected by remember { mutableStateOf(false) }
    var outputText by remember { mutableStateOf("") }
    var jsonOutputText by remember { mutableStateOf("") }
    var showJsonOutput by remember { mutableStateOf(false) }
    var reportText by remember { mutableStateOf("") }

    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Checkbox(checked = jsonInputSelected, onCheckedChange = { jsonInputSelected = it })
                Text("JSON 형식 입력")
            }
            Row {
                Checkbox(checked = html24HoursSelected, onCheckedChange = { html24HoursSelected = it })
                Text("24시간 기준 HTML 입력")
            }
            Row {
                Checkbox(checked = html90DaysSelected, onCheckedChange = { html90DaysSelected = it })
                Text("90일 기준 HTML 입력")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                label = { Text("입력할 데이터") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    processInput(inputText, jsonInputSelected, html24HoursSelected, html90DaysSelected) { outputTextResult, jsonOutputResult, reportResult ->
                        outputText = outputTextResult
                        jsonOutputText = jsonOutputResult
                        reportText = reportResult
                        showJsonOutput = jsonOutputResult.isNotEmpty()
                    }
                }
            }) {
                Text("데이터 처리")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(outputText)

            TextField(
                value = reportText,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                label = { Text("report 출력") },
                readOnly = true
            )
            // JSON 출력 필드
            if (showJsonOutput) {
                TextField(
                    value = jsonOutputText,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    label = { Text("JSON 출력") },
                    readOnly = true
                )
            }
        }
    }
}

suspend fun processInput(
    input: String,
    isJson: Boolean,
    isHtml24: Boolean,
    isHtml90: Boolean,
    onOutput: (String, String, String) -> Unit
) {
    var jsonOutputText = ""
    var reportOutputText = ""

    if (isJson) {
        // JSON 문자열을 파싱하여 originIssueLink 리스트에 추가
        originIssueLink.clear()
        originIssueLink.addAll(HTMLConverter().parseJsonToIssueLinks(input))
        originIssueLink.forEach {
            it.eventCountIn24 = -1
            it.userCountIn24 = -1
            it.eventCountIn90Days = -1
            it.eventCountIn90Days = -1
        }
    } else if (isHtml24) {
        // 24시간 기준 HTML 문자열 처리
        val issueLinks = HTMLConverter().extractIssueLinks(input, CrashPeriodRange.LastTwentyFourHours)
        updateIssueLinks(issueLinks, originIssueLink, newIssueLink, true)

        // 보고 건 형식으로 출력
        reportOutputText = generateReportText(newIssueLink, "신규 보고 건 (24시간 기준):")
    } else if (isHtml90) {
        // 90일 기준 HTML 문자열 처리
        val issueLinks = HTMLConverter().extractIssueLinks(input, CrashPeriodRange.LastNinetyDays)
        updateIssueLinks(issueLinks, originIssueLink, newIssueLink, false, true)

        // 모든 원본 리스트와 신규 리스트의 90일 기준 사용자 수와 이벤트 수가 모두 -1인지 체크
        val allValid = originIssueLink.all { it.userCountIn90Days != -1 && it.eventCountIn90Days != -1 } &&
                newIssueLink.all { it.userCountIn90Days != -1 && it.eventCountIn90Days != -1 }

        if (allValid) {
            // JSON 형식으로 결과 출력
            val gson = Gson()
            jsonOutputText = gson.toJson(originIssueLink + newIssueLink)
        }

        // 기존 보고 건 형식으로 출력
        reportOutputText = generateReportText(originIssueLink, "기존 보고 건 (90일 기준):")
    }

    onOutput("처리 완료! 원본 리스트: ${originIssueLink.size}, 신규 리스트: ${newIssueLink.size}", jsonOutputText, reportOutputText)
}

fun generateReportText(issueLinks: List<HTMLConverter.IssueLink>, title: String): String {
    return buildString {
        append(title)
        append("\n")
        issueLinks.forEachIndexed { index, issueLink ->
            append("${index + 1}. ${issueLink.title} (v2.4.9 수정 예정)\n")
            append("현상 : ${issueLink.description}\n")
            append("사용자 : ${issueLink.userCountIn24}, 이벤트 : ${issueLink.eventCountIn24}\n")
            append("90일 기준 사용자 : ${issueLink.userCountIn90Days}, 이벤트 : ${issueLink.eventCountIn90Days}\n\n")
        }
    }.trimEnd()
}

fun updateIssueLinks(
    issueLinks: List<HTMLConverter.IssueLink>,
    originList: MutableList<HTMLConverter.IssueLink>,
    newList: MutableList<HTMLConverter.IssueLink>,
    is24Hours: Boolean,
    skipAddingNew: Boolean = false // 새로운 객체 추가 여부 제어
) {
    issueLinks.forEach { newLink ->
        val existingLink = originList.find { it.issueId == newLink.issueId }
        if (existingLink != null) {
            // 기존 이슈 업데이트
            if (is24Hours) {
                existingLink.eventCountIn24 = newLink.eventCountIn24
                existingLink.userCountIn24 = newLink.userCountIn24
            } else {
                existingLink.eventCountIn90Days = newLink.eventCountIn90Days
                existingLink.userCountIn90Days = newLink.userCountIn90Days
            }
        } else if (!skipAddingNew) {
            // 새로운 이슈 추가 (90일 기준일 경우에는 추가하지 않음)
            newList.add(newLink)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}