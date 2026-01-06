package nd.nikdi.sequola

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sequola",
    ) {
        App()
    }
}