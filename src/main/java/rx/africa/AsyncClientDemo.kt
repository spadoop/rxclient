package org.apache.http.concurrent

import kotlinx.coroutines.experimental.CancellableContinuation
import kotlinx.coroutines.experimental.cancelFutureOnCompletion
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
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

/* step 2
fun HttpAsyncClient.execute(request: HttpUriRequest): CompletableFutureCallback<HttpResponse> {
    val future: FutureCallback<HttpResponse> = CompletableFutureCallback(CompletableFuture<HttpResponse>())

    this.execute(request, future)

    return future as CompletableFutureCallback<HttpResponse>
}
*/

/*step 3*/
suspend fun HttpAsyncClient.execute(request: HttpUriRequest): HttpResponse {
    return suspendCancellableCoroutine { cont: CancellableContinuation<HttpResponse> ->
        val future = this.execute(request, object : FutureCallback<HttpResponse> {
            override fun completed(result: HttpResponse) {
                cont.resume(result)
            }
            override fun cancelled() {
                // Nothing
            }
            override fun failed(ex: Exception) {
                cont.resumeWithException(ex)
            }
        })

        cont.cancelFutureOnCompletion(future)
        Unit
    }
}
fun main(args: Array<String>) {
    var c:HttpAsyncClient = DefaultHttpAsyncClient()
    val request = HttpGet("http://www.apache.org/")
    val future = c.execute(request, object : FutureCallback<HttpResponse> {
        override fun completed(response: HttpResponse) {
            println(request.requestLine.toString() + "->"
                    + response.statusLine)
        }
        override fun failed(ex: Exception) {
            ex.printStackTrace()
        }
        override fun cancelled() {
        }
    })
    val response = future.get()
    println("Response: " + response.statusLine)
    println("Response1: " + EntityUtils.toString(response.entity))
    println("Shutting down")
}
