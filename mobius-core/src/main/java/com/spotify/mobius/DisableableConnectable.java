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

import static com.spotify.mobius.internal_util.Preconditions.checkNotNull;

import com.spotify.mobius.functions.Consumer;
import javax.annotation.Nonnull;

/**
 * A {@link Connectable} wrapper that ensures that an inner {@link Connection} doesn't emit or
 * receive any values after being disabled.
 *
 * <p>This only acts as a safeguard during shutdown, you still need to make sure that the
 * Connectable disposes of resources correctly.
 */
final class DisableableConnectable<I, O> implements Connectable<I, O> {

  private final Connectable<I, O> actual;

  DisableableConnectable(Connectable<I, O> actual) {
    this.actual = checkNotNull(actual);
  }

  @Nonnull
  @Override
  public DisableableConnection<I> connect(Consumer<O> output) {
    final DisableableConsumer<O> disableableOutput =
        new DisableableConsumer<>(checkNotNull(output));

    final Connection<I> input = checkNotNull(actual.connect(disableableOutput));

    return new DisableableConnection<I>(input) {
      @Override
      public void disable() {
        disableableOutput.disable();
        super.disable();
      }
    };
  }
}
