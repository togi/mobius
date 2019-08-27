package com.spotify.mobius.coroutines

import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.test.RecordingConsumer
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertFailsWith

@UseExperimental(ExperimentalCoroutinesApi::class)
class CoroutineConnectableTest {

    private var scope = TestCoroutineScope()

    private var output = RecordingConsumer<String>()

    private fun <I, O> CoroutineConnectable<I, O>.withConnection(output: Consumer<O> = Consumer {}, block: Connection<I>.() -> Unit) =
            connect(output).apply {
                block()
                scope.advanceUntilIdle()
            }

    private fun coroutineConnectable(context: CoroutineContext = EmptyCoroutineContext,
                                     handlerFn: suspend CoroutineScope.(String, (String) -> Unit) -> Unit) =
            CoroutineConnectable(scope, context, handlerFn)


    @Before
    fun setUp() {
        output.clearValues()
    }

    @Test
    fun contextCannotContainAJob() {
        assertFailsWith(IllegalArgumentException::class) {
            coroutineConnectable(Job()) { _, _ -> }
        }
    }

    @Test
    fun suppliedContextIsInheritedFrom() {
        val underTest = coroutineConnectable(CoroutineName("test1")) { _, _ ->
            coroutineContext[CoroutineName]?.let { output.accept(it.name) }
        }

        underTest.withConnection {
            accept("")
        }

        output.assertValues("test1")
    }

    @Test
    fun handlerAcceptsInput() {
        val underTest = coroutineConnectable { input, _ -> output.accept(input) }

        underTest.withConnection {
            accept("test2")
        }

        output.assertValues("test2")
    }

    @Test
    fun handlerCanCallOutputWhenConnected() {
        val underTest = coroutineConnectable { input, output -> output(input) }

        underTest.withConnection(output) {
            accept("test3")
        }

        output.assertValues("test3")
    }

    @Test
    fun handlerCannotCallOutputAfterDisconnected() {
        val lock = CompletableDeferred<Unit>()

        val underTest = coroutineConnectable { input, output ->
            output("$input-1")
            lock.await()
            output("$input-2")
        }

        underTest.withConnection(output) {
            accept("test4")
            dispose()
        }

        lock.complete(Unit)
        scope.advanceUntilIdle()

        output.assertValues("test4-1")
    }

    @Test
    fun usesSuppliedDispatcher() {
        val dispatcher = TestCoroutineDispatcher()

        val underTest = coroutineConnectable(dispatcher) { input, _ ->
            output.accept(input)
        }

        underTest.withConnection {
            dispatcher.pauseDispatcher()

            accept("test5")

            scope.advanceUntilIdle()
            output.assertValues()

            dispatcher.advanceUntilIdle()
            output.assertValues("test5")
        }
    }

    @Test
    fun handlerIsLaunchedInParallell() {
        val lock = CompletableDeferred<Unit>()

        val underTest = coroutineConnectable { input, _ ->
            lock.await()
            output.accept(input)

        }

        underTest.withConnection {
            accept("test6-2")
            accept("test6-3")

            output.accept("test6-1")

            lock.complete(Unit)
        }

        output.assertValues("test6-1", "test6-2", "test6-3")
    }

    @Test
    fun handlersAreCancelledOnDispose() {
        val lock = CompletableDeferred<Unit>()

        val underTest = coroutineConnectable { _, _ ->
            try {
                lock.await()
            } catch (e: CancellationException) {
                output.accept("cancelled")
            }
        }

        val connection = underTest.withConnection {
            accept("test7")
        }

        output.assertValues()
        connection.dispose()
        output.assertValues("cancelled")
    }

    @Test
    fun connectionsAreIndependent() {
        val output1 = RecordingConsumer<String>()
        val output2 = RecordingConsumer<String>()

        val underTest = coroutineConnectable { input, output -> output(input) }

        val c1 = underTest.withConnection(output1) {}
        val c2 = underTest.withConnection(output2) {}

        c1.accept("test8-1")
        c2.accept("test8-2")
        c2.accept("test8-3")
        c1.accept("test8-4")

        scope.advanceUntilIdle()

        output1.assertValues("test8-1", "test8-4")
        output2.assertValues("test8-2", "test8-3")

    }

    @Test
    fun canReusefterDisposing() {
        val underTest = coroutineConnectable { input, output -> output(input) }

        underTest.withConnection(output) {
            accept("test9-1")
            dispose()
        }

        underTest.withConnection(output) {
            accept("test9-2")
        }

        output.assertValues("test9-1", "test9-2")
    }

    @Test
    fun disposingWaitsForHandler() {
        val lock = CompletableDeferred<Unit>()

        val underTest = coroutineConnectable { input, _ ->
            try {
                lock.await()
            } catch (e: CancellationException) {
                Thread.sleep(100)
                output.accept(input)
            }
        }

        val connection = underTest.withConnection {
            accept("test10")
        }

        output.assertValues()
        connection.dispose()
        output.assertValues("test10")
    }
}
