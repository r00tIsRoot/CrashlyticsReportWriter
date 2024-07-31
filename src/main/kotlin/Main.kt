import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
@Preview
fun App() {
    var input by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column {
            Button(onClick = {
                val result = ""
                HTMLConverter().extractIssueLinks(input).forEach {
                    println(it.href + " / " + it.text)
                    result + it.href + " / " + it.text + "\n"
                }
                text = result

            }) {
                Text("click")
            }
            Text(text = text)
            TextField(
                value = input,
                onValueChange = { changedValue ->
                    input = changedValue
                }
            )
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