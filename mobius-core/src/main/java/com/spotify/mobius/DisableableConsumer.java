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

import com.spotify.mobius.functions.Consumer;

/** A {@link Consumer} that will block all incoming values once it's disabled. */
class DisableableConsumer<T> implements Consumer<T>, Disableable {

  private final Consumer<T> actual;
  private boolean disabled;

  DisableableConsumer(Consumer<T> actual) {
    this.actual = actual;
    this.disabled = false;
  }

  @Override
  public synchronized void accept(T input) {
    if (disabled) {
      return;
    }
    actual.accept(input);
  }

  @Override
  public synchronized void disable() {
    disabled = true;
  }
}
