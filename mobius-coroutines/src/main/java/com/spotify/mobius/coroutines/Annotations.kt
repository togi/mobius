package com.spotify.mobius.coroutines

/**
 * Marks declarations that are still experimental in the mobius coroutine API.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@Experimental(level = Experimental.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
annotation class MobiusCoroutinesPreview
