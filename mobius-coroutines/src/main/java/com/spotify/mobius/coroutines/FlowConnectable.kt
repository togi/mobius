package com.spotify.mobius.coroutines

import com.spotify.mobius.Connectable
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Create a [Connectable] using a Flow transforming function.
 */
@FlowPreview
internal class FlowConnectable<I, O>(
        private val context: CoroutineContext = EmptyCoroutineContext,
        private val transformer: (Flow<I>) -> Flow<O>
) : Connectable<I, O> {

    init {
        require(context[Job] == null) {
            "The Connectable context cannot have a Job in it. Manage its lifecycle using the Connection object."
        }
    }

    @UseExperimental(ExperimentalCoroutinesApi::class)
    override fun connect(output: Consumer<O>): Connection<I> {
        val inputChannel = Channel<I>()
        val inputFlow = inputChannel.consumeAsFlow()

        val outputFlow = transformer(inputFlow)

        val job = Job()
        val newContext = GlobalScope.newCoroutineContext(context + job)
        val scope = CoroutineScope(newContext)

        scope.launch {
            outputFlow.collect {
                output.accept(it)
            }
        }

        return object : Connection<I> {
            override fun accept(value: I) {
                scope.launch {
                    inputChannel.send(value)
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
