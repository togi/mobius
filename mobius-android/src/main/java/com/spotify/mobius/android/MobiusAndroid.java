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
package com.spotify.mobius.android;

import com.spotify.mobius.Mobius;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.android.runners.MainThreadWorkRunner;
import com.spotify.mobius.functions.Producer;
import com.spotify.mobius.runners.WorkRunner;
import javax.annotation.Nonnull;

public final class MobiusAndroid {
  private MobiusAndroid() {
    // prevent instantiation
  }

  public static <M, E, F> MobiusLoop.Controller<M, E> controller(
      MobiusLoop.Factory<M, E, F> loopFactory, M defaultModel) {

    return new DispatchingMobiusController<>(
        Mobius.controller(loopFactory, defaultModel),
        new Producer<WorkRunner>() {
          @Nonnull
          @Override
          public WorkRunner get() {
            return MainThreadWorkRunner.create();
          }
        });
  }
}
