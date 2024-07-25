import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.web.WebView
import androidx.compose.ui.web.rememberWebViewState

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppContent()
    }
}

@Composable
fun AppContent() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var crashReports by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Firebase Crashlytics Viewer", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoggedIn) {
            // 웹뷰를 사용하여 Firebase Crashlytics 페이지 로드
            val webViewState = rememberWebViewState("https://console.firebase.google.com/u/0/crashlytics")
            WebView(state = webViewState, modifier = Modifier.fillMaxHeight(0.7f))

            Button(
                onClick = {
                    // 로그인 성공 후 크래시 리포트를 가져오는 작업 (예시로 하드코딩된 데이터 사용)
                    isLoggedIn = true
                    crashReports = getRecentCrashReports()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Login to Firebase")
            }
        } else {
            // 로그인 후 최근 크래시 리포트 표시
            Text("Recent Crash Reports:", style = MaterialTheme.typography.titleMedium)

            crashReports.forEach { report ->
                Text("- $report")
            }
        }
    }
}

// 실제로는 Firebase API를 호출하여 최근 크래시 리포트를 가져와야 합니다.
fun getRecentCrashReports(): List<String> {
    // 여기서는 예시로 하드코딩된 데이터를 사용합니다.
    return listOf("Crash Report 1", "Crash Report 2", "Crash Report 3")
}
