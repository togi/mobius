/*
 * -\-\-
 * Mobius
 * --
 * Copyright (c) 2017-2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.mobius.actors;

import com.spotify.mobius.disposables.Disposable;
import com.spotify.mobius.functions.Consumer;

/**
 * Simple interface for actors (as in the actor model of concurrent computation).
 *
 * <p>
 *
 * <p>The apply method queues messages in some kind of queue, and those messages are then consumed
 * one by one. Because of this the apply method must be thread-safe and should avoid blocking the
 * caller.
 *
 * @param <T> An immutable type used for messages sent to the actor.
 */
public interface Actor<T> extends Consumer<T>, Disposable {}
