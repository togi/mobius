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
package com.spotify.mobius2;

import static com.spotify.mobius2.Effects.effects;
import static com.spotify.mobius2.Next.next;
import static com.spotify.mobius2.Next.noChange;
import static org.junit.Assert.assertEquals;

import com.spotify.mobius2.internal_util.ImmutableUtil;
import com.spotify.mobius2.runners.WorkRunners;
import com.spotify.mobius2.test.ForwardingConnectable;
import com.spotify.mobius2.test.NoopConnectable;
import org.junit.Test;

public class MobiusLoopSynchronousBehaviour {

  @Test
  public void shouldNotStackOverflowIfEffectHandlerSynchronouslyDispatchesEvents() {
    Update<Integer, Integer, Integer> update =
        (model, event) -> {
          if (event > 0) {
            return next(model + 1, effects(event - 1));
          }
          return noChange();
        };

    MobiusLoop<Integer, Integer, Integer> mobiusLoop =
        MobiusLoop.create(
            update,
            0,
            ImmutableUtil.emptySet(),
            new ForwardingConnectable<>(), // All effects will be forwarded as events.
            new NoopConnectable<>(),
            WorkRunners.immediate(),
            WorkRunners.immediate());

    mobiusLoop.dispatchEvent(10000);

    @SuppressWarnings("ConstantConditions")
    int numberOfIterations = mobiusLoop.getMostRecentModel();

    assertEquals(10000, numberOfIterations);
  }
}
