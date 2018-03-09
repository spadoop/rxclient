package org.apache.http.concurrent

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.future.await
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
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

/* step 2*/
//    fun HttpAsyncClient.execute(request: HttpUriRequest): CompletableFutureCallback<HttpResponse> {
//        val future: FutureCallback<HttpResponse> = CompletableFutureCallback(CompletableFuture<HttpResponse>())
//
//        this.execute(request, future)
//
//        return future as CompletableFutureCallback<HttpResponse>
//    }


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

fun main(args: Array<String>) =runBlocking<Unit>{
    var ht:HttpEntity =  StringEntity(  "", ContentType.APPLICATION_JSON)
    val job = async {
        Thread.sleep(100L)
        for(i in 0..999)getCount("http://localhost:8090/api")
    }
    println(System.currentTimeMillis())
    println("Response: " +  job.await() )
    println(System.currentTimeMillis())
}
suspend fun getCount(url: String): HttpResponse {

    var httpclient:CloseableHttpAsyncClient = HttpAsyncClients.createDefault()
    httpclient.start()
    val request = HttpGet(url)
    val r = httpclient./* calling extension */execute(request) /* kotlinx.coroutines extension await()*/
    httpclient.close()
    return r
}
