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
 * An {@link Actor} that immediately calls its {@link Consumer} on the same thread.
 */
public class ImmediateActor<T> implements Actor<T> {

  private final Consumer<T> consumer;
  private boolean disposed;

  public ImmediateActor(Consumer<T> consumer) {
    this.consumer = consumer;
  }

  @Override
  public synchronized void accept(T message) {
    if (disposed) return;

    consumer.accept(message);
  }

  @Override
  public synchronized void dispose() {
    disposed = true;
  }
}
