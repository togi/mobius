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

/**
 * A {@code Disableable} is an object that can be told to permanently stop performing work (eg.
 * block all incoming messages) without necessarily releasing associated resources.
 *
 * <p>It can be thought of as a less aggressive {@link Disposable} that doesn't have to wait for all
 * resources to be released, it just has to stop using them before returning from {@link
 * #disable()}.
 */
interface Disableable {

  /**
   * Disable this object and stop using all resources associated with this object.
   *
   * <p>The object will no longer perform any work after disable has been called, and any further
   * calls to disable won't have any effect.
   */
  void disable();
}
