package com.spotify.mobius.coroutines

import com.spotify.mobius.Connectable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Create a [Connectable] using a suspending function.
 *
 * Every time the resulting connectable is connected, it will create a CoroutineScope with the
 * given context, and using the suspending handler function. The handler function will be
 * launched in that scope whenever the connection receives a value.
 *
 * The handler function will be passed the upstream dispatching function that the
 *
 * @param dispatcher the dispatcher to use, will use the default one unless you specify another one.
 * @param handlerFn a suspending block that will be launched when values are received.
 * @param I the input type for the resulting Connectable
 * @param O the output type for the resulting Connectable
 */
@MobiusCoroutinesPreview
fun <I, O> coroutineConnectable(
        context: CoroutineContext = EmptyCoroutineContext,
        handlerFn: suspend CoroutineScope.(input: I, dispatch: (O) -> Unit) -> Unit
): Connectable<I, O> = CoroutineConnectable(GlobalScope, context, handlerFn)

/**
 * Create a [Connectable] using a Flow transforming function.
 *
 * Every time the resulting connection is connected, a new input flow will be created with all
 * values sent to the connection. That input flow will be passed to the transforming function to
 * create an output flow, and that output flow will be collected in a new CouroutineScope that is
 * tied to the lifecycle of the connection.
 *
 * @param context the context to use when collecting the input flow
 * @param transformerFn the flow transforming function
 * @param I the input type of the Connectable
 * @param O the output type of the Connectable

 */
@MobiusCoroutinesPreview
@FlowPreview
fun <I, O> flowConnectable(
        context: CoroutineContext = EmptyCoroutineContext,
        transformerFn: (Flow<I>) -> Flow<O>): Connectable<I, O> = FlowConnectable(context, transformerFn)
