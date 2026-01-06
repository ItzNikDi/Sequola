package nd.nikdi.sequola.viewmodels

data class EditorState(
    val text: String = "",
    val cursor: Int = 0,
    val completions: List<String> = emptyList()
)
