package com.spotify.mobius.coroutines

import com.spotify.mobius.MobiusLoop
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Use the given CoroutineDispatcher to handle events in this mobius loop.
 *
 * @param dispatcher CoroutineDispatcher to use
 */
@MobiusCoroutinesPreview
fun <M, E, F> MobiusLoop.Builder<M, E, F>.eventDispatcher(dispatcher: CoroutineDispatcher) =
        eventRunner(CoroutineDispatcherWorkRunnerProducer(dispatcher))

/**
 * Use the given CoroutineDispatcher to handle effects in this mobius loop.
 *
 * @param dispatcher CoroutineDispatcher to use
 */
@MobiusCoroutinesPreview
fun <M, E, F> MobiusLoop.Builder<M, E, F>.effectDispatcher(dispatcher: CoroutineDispatcher) =
        effectRunner(CoroutineDispatcherWorkRunnerProducer(dispatcher))
