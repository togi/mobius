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

import static com.spotify.mobius2.internal_util.Preconditions.checkNotNull;

import com.spotify.mobius2.functions.Consumer;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Processes events and emits effects and models as a result of that.
 *
 * @param <M> model type
 * @param <E> event type
 * @param <F> effect descriptor type
 */
class EventProcessor<M, E, F> {

  private final com.spotify.mobius2.MobiusStore<M, E, F> store;
  private final Consumer<F> effectConsumer;
  private final Consumer<M> modelConsumer;

  EventProcessor(
      com.spotify.mobius2.MobiusStore<M, E, F> store,
      Consumer<F> effectConsumer,
      Consumer<M> modelConsumer) {
    this.store = checkNotNull(store);
    this.effectConsumer = checkNotNull(effectConsumer);
    this.modelConsumer = checkNotNull(modelConsumer);
  }

  private volatile boolean busyHandlingEvents = false;
  private Queue<E> eventQueue = new LinkedList<>();

  void update(E event) {

    synchronized (this) {
      eventQueue.add(event);

      if (busyHandlingEvents) {
        return;
      } else {
        busyHandlingEvents = true;
      }
    }

    while (true) {

      Iterable<E> events;

      synchronized (this) {
        if (eventQueue.isEmpty()) {
          busyHandlingEvents = false;
          return;
        }

        events = eventQueue;
        eventQueue = new LinkedList<>();
      }

      for (E queuedEvent : events) {
        Next<M, F> next = store.update(queuedEvent);

        next.ifHasModel(this::dispatchModel);
        dispatchEffects(next.effects());
      }
    }
  }

  private void dispatchModel(M model) {
    modelConsumer.accept(model);
  }

  private void dispatchEffects(Iterable<F> effects) {
    for (F effect : effects) {
      effectConsumer.accept(effect);
    }
  }

  /**
   * Factory for event processors.
   *
   * @param <M> model type
   * @param <E> event type
   * @param <F> effect descriptor type
   */
  static class Factory<M, E, F> {

    private final com.spotify.mobius2.MobiusStore<M, E, F> store;

    Factory(com.spotify.mobius2.MobiusStore<M, E, F> store) {
      this.store = checkNotNull(store);
    }

    public EventProcessor<M, E, F> create(Consumer<F> effectConsumer, Consumer<M> modelConsumer) {
      return new EventProcessor<>(store, checkNotNull(effectConsumer), checkNotNull(modelConsumer));
    }
  }
}
