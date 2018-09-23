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

import com.spotify.mobius.functions.Consumer;

/**
 * Factory for creating instances of {@link Actor}.
 *
 * <p>
 *
 * <p>Typically an actor factory is associated with a thread or thread-pool, and a created actor
 * will be using that thread or thread-pool to dispatch messages to the consumer.
 */
public interface ActorFactory {

  /**
   * Create a new actor that dispatch all incoming messages to the given consumer.
   *
   * @param <T> The message type used by the actor. Must be immutable.
   */
  <T> Actor<T> create(Consumer<T> consumer);
}
