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
package com.spotify.mobius;

import com.spotify.mobius.disposables.Disposable;

/** A {@link Connection} that will block all incoming values once it's disabled. */
class DisableableConnection<T> implements Connection<T>, Disableable {

  private final DisableableConsumer<T> consumer;
  private final Disposable disposable;

  DisableableConnection(Connection<T> actual) {
    this.consumer = new DisableableConsumer<>(actual);
    this.disposable = actual;
  }

  @Override
  public void accept(T value) {
    consumer.accept(value);
  }

  @Override
  public void disable() {
    consumer.disable();
  }

  @Override
  public void dispose() {
    disposable.dispose();
  }
}
