package com.spotify.mobius.coroutines

import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Create a [Connectable] using a suspending function.
 */
internal class CoroutineConnectable<I, O>(
        private val parentScope: CoroutineScope,
        private val context: CoroutineContext,
        private val handlerFn: suspend CoroutineScope.(I, (O) -> Unit) -> Unit
) : Connectable<I, O> {

    init {
        require(context[Job] == null) {
            "The Connectable context cannot have a Job in it. Manage its lifecycle using the Connection object."
        }
    }

    @UseExperimental(ExperimentalCoroutinesApi::class)
    override fun connect(output: Consumer<O>): Connection<I> {
        val job = Job()
        val newContext = parentScope.newCoroutineContext(context + job)

        val scope = CoroutineScope(newContext)

        return object : Connection<I> {
            override fun accept(input: I) {
                scope.launch {
                    handlerFn(input, output::accept)
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
