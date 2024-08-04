import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
@Preview
fun App() {
    var inputIn24Hours by remember { mutableStateOf("") }
    var inputIn90Days by remember { mutableStateOf("") }
    var textIn24Hours by remember { mutableStateOf("Hello, World!") }
    var textIn90Days by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column {
            Button(onClick = {
                textIn24Hours = "LastTwentyFourHours"
                textIn90Days = "LastNinetyDays"

                CoroutineScope(Dispatchers.IO).launch {
                    val stringBuilder = StringBuilder()

                    val issueLinksIn24Hours = HTMLConverter().extractIssueLinks(inputIn24Hours, CrashPeriodRange.LastTwentyFourHours)
                    val issueLinksIn90Days = HTMLConverter().extractIssueLinks(inputIn90Days, CrashPeriodRange.LastNinetyDays)

                    HTMLConverter().mergeIssueLinks(issueLinksIn24Hours, issueLinksIn90Days).forEach {
                        stringBuilder.append( "\n" +
                                "issuId:${it.issueId}\n" +
                                "url:${it.url}\n" +
                                "title:${it.title}\n" +
                                "subTitle:${it.subTitle}\n" +
                                "minVersion:${it.minVersion}\n" +
                                "latestVersion:${it.latestVersion}\n" +
                                "eventCountIn24:${it.eventCountIn24}\n" +
                                "userCountIn24:${it.userCountIn24}\n" +
                                "eventCountIn90Days:${it.eventCountIn90Days}\n" +
                                "userCountIn90Days:${it.userCountIn90Days}\n"
                        )
                        withContext(Dispatchers.Main) {
                            textIn24Hours = stringBuilder.toString()
                        }
                    }

//                    HTMLConverter().extractIssueLinks(inputIn24Hours, CrashPeriodRange.LastTwentyFourHours)
//                        .forEach {
////                        println(
////                            "LastTwentyFourHours" +
////                                    "issuId:${it.issueId}\n" +
////                                    "url:${it.url}\n" +
////                                    "title:${it.title}\n" +
////                                    "subTitle:${it.subTitle}\n" +
////                                    "minVersion:${it.minVersion}\n" +
////                                    "latestVersion:${it.latestVersion}\n" +
////                                    "eventCount:${it.eventCountIn24}\n" +
////                                    "userCount:${it.userCountIn24}"
////                        )
////                        println()
//                        stringBuilder.append( "\n" +
//                                "issuId:${it.issueId}\n" +
//                                "url:${it.url}\n" +
//                                "title:${it.title}\n" +
//                                "subTitle:${it.subTitle}\n" +
//                                "minVersion:${it.minVersion}\n" +
//                                "latestVersion:${it.latestVersion}\n" +
//                                "eventCount:${it.eventCountIn24}\n" +
//                                "userCount:${it.userCountIn24}\n"
//                        )
//
//                        withContext(Dispatchers.Main) {
//                            textIn24Hours = stringBuilder.toString()
//                        }
//                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val stringBuilder = StringBuilder()

                    HTMLConverter().extractIssueLinks(inputIn90Days, CrashPeriodRange.LastNinetyDays)
                        .forEach {
//                        println(
//                            "LastNinetyDays" +
//                                    "issuId:${it.issueId}\n" +
//                                    "url:${it.url}\n" +
//                                    "title:${it.title}\n" +
//                                    "subTitle:${it.subTitle}\n" +
//                                    "minVersion:${it.minVersion}\n" +
//                                    "latestVersion:${it.latestVersion}\n" +
//                                    "eventCount:${it.eventCountIn24}\n" +
//                                    "userCount:${it.userCountIn24}\n"
//                        )
//                        println()
                        stringBuilder.append( "\n" +
                                "issuId:${it.issueId}\n" +
                                "url:${it.url}\n" +
                                "title:${it.title}\n" +
                                "subTitle:${it.subTitle}\n" +
                                "minVersion:${it.minVersion}\n" +
                                "latestVersion:${it.latestVersion}\n" +
                                "eventCount:${it.eventCountIn90Days}\n" +
                                "userCount:${it.userCountIn90Days}\n"
                        )

                        withContext(Dispatchers.Main) {
                            textIn90Days = stringBuilder.toString()
                        }
                    }
                }

            }) {
                Text("click")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = textIn24Hours,
                    modifier = Modifier
                        .weight(1f),
                    maxLines = 3,
                    onValueChange = {}

                )
                TextField(
                    value = textIn90Days,
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    onValueChange = {}
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = inputIn24Hours,
                    onValueChange = { changedValue ->
                        inputIn24Hours = changedValue
                    },
                    modifier = Modifier
                        .weight(1f),
                    maxLines = 3 // 최대 3줄로 설정
                )
                TextField(
                    value = inputIn90Days,
                    onValueChange = { changedValue ->
                        inputIn90Days = changedValue
                    },
                    modifier = Modifier
                        .weight(1f),
                    maxLines = 3 // 최대 3줄로 설정
                )
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
internal fun WebViewSample() {
    MaterialTheme {
        val webViewState =
            rememberWebViewState("https://google.com/")
        Column(Modifier.fillMaxSize()) {
            val text = webViewState.let {
                "${it.pageTitle ?: "pageTitleIsNull"} ${it.loadingState} ${it.lastLoadedUrl ?: "lastLoadedUrlIsNull"}"
            }
            Text(text)
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
            )
        }

    }
}