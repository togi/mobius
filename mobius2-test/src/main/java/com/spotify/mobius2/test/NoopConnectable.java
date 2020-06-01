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
package com.spotify.mobius2.test;

import com.spotify.mobius2.Connectable;
import com.spotify.mobius2.Connection;
import com.spotify.mobius2.functions.Consumer;
import javax.annotation.Nonnull;

/** A simple connectable that can be connected, but that never emits anything to the output */
public class NoopConnectable<I, O> implements Connectable<I, O> {
  @Nonnull
  @Override
  public Connection<I> connect(Consumer<O> output) {
    return new NoopConnection<>();
  }
}
