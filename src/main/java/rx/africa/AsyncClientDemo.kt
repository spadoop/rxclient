package rx.africa

import kotlinx.coroutines.experimental.future.await
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