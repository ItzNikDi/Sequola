package nd.nikdi.sequola.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletionResponse(
    @SerialName("request_id") val requestId: UInt,
    val type: String,
    val items: List<String> = emptyList()
)

@Serializable
data class CompletionRequest(
    @SerialName("request_id") val requestId: UInt,
    val type: String = "completionContext",
    val text: String,
    val cursor: Int
)