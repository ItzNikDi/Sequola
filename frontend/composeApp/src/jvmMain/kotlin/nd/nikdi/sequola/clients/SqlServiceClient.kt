package nd.nikdi.sequola.clients

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import nd.nikdi.sequola.models.CompletionRequest
import nd.nikdi.sequola.models.CompletionResponse

class SqlServiceClient(path: String) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val process = ProcessBuilder(path)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()

    private val writer = process.outputStream.bufferedWriter()
    private val reader = process.inputStream.bufferedReader()

    suspend fun requestCompletion(requestId: UInt, text: String, cursor: Int): List<String> =
        withContext(Dispatchers.IO) {
            val req = json.encodeToString(CompletionRequest(requestId = requestId, text = text, cursor = cursor))
            //println("req: $req") we do some debugging here... :)
            writer.write("$req\n")
            writer.flush()

            val rawRes = reader.readLine()
            //println("res: $res")
            val res = json.decodeFromString<CompletionResponse>(rawRes)
            res.items
        }
}