package nd.nikdi.sequola.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nd.nikdi.sequola.clients.SqlServiceClient
import nd.nikdi.sequola.viewmodels.EditorState

@Composable
fun SQLEditor(client: SqlServiceClient) {
    var state by remember { mutableStateOf(EditorState()) }
    var requestId by remember { mutableStateOf(0U) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().border(2.dp, Color.Gray).padding(8.dp),
            value = state.text,
            onValueChange = { newText ->
                val cursor = newText.length
                requestId += 1U

                state = state.copy(text = newText, cursor = cursor)

                scope.launch {
                    val items = client.requestCompletion(requestId, newText, cursor)
                    state = state.copy(completions = items)
                }
            }
        )

        if (state.completions.isNotEmpty()) {
            Column(
                modifier = Modifier.border(2.dp, Color.DarkGray).padding(4.dp)
            ) {
                for (item in state.completions) {
                    Text(item, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}