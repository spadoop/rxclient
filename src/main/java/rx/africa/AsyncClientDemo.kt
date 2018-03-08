package rx.africa

import kotlinx.coroutines.experimental.future.await
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.nio.client.HttpAsyncClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Future

class CompletableFutureCallback<T>(
        val completableFuture: CompletableFuture<T>
) : FutureCallback<T>, Future<T> by completableFuture, CompletionStage<T> by completableFuture {
    override fun failed(ex: Exception) {
        completableFuture.completeExceptionally(ex)
    }

    override fun cancelled() {
        completableFuture.cancel(false)
    }

    override fun completed(result: T) {
        completableFuture.complete(result)
    }
}

suspend fun <T> CompletableFutureCallback<T>.await(): T = this.completableFuture.await()

interface FutureCallback<T> {
    fun completed(result: T)
    fun failed(ex: Exception)
    fun cancelled()
}
//fun HttpAsyncClient.execute(request: HttpUriRequest): CompletableFuture<HttpResponse> {
//    val future = CompletableFuture<HttpResponse>()
//
//    this.execute(request, object : FutureCallback<HttpResponse> {
//        override fun completed(result: HttpResponse) {
//            future.complete(result)
//        }
//
//        override fun cancelled() {
//            future.cancel(false)
//        }
//
//        override fun failed(ex: Exception) {
//            future.completeExceptionally(ex)
//        }
//    })
//
//    return future
//}
fun HttpAsyncClient.execute(request: HttpUriRequest): CompletableFutureCallback<HttpResponse> {
    val future: FutureCallback<HttpResponse> = CompletableFutureCallback(CompletableFuture<HttpResponse>())

    this.execute(request, future)

    return future
}