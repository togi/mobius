package com.spotify.mobius.coroutines

import com.spotify.mobius.functions.Producer
import com.spotify.mobius.runners.WorkRunner
import kotlinx.coroutines.*

/** Allows using a CouroutineDispatcher as a WorkRunner */
internal class CoroutineDispatcherWorkRunnerProducer(private val dispatcher: CoroutineDispatcher) : Producer<WorkRunner> {
    override fun get(): WorkRunner {
        val job = Job()
        val scope = CoroutineScope(dispatcher + job)

        return object : WorkRunner {
            override fun post(runnable: Runnable) {
                scope.launch {
                    runnable.run()
                }
            }

            override fun dispose() {
                runBlocking {
                    job.cancelAndJoin()
                }
            }
        }
    }
}
