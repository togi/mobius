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
package com.spotify.mobius2.rx2;

import static com.spotify.mobius2.Effects.effects;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.spotify.mobius2.Connectable;
import com.spotify.mobius2.Connection;
import com.spotify.mobius2.ConnectionLimitExceededException;
import com.spotify.mobius2.First;
import com.spotify.mobius2.Init;
import com.spotify.mobius2.Mobius;
import com.spotify.mobius2.MobiusLoop;
import com.spotify.mobius2.Next;
import com.spotify.mobius2.Update;
import com.spotify.mobius2.functions.Consumer;
import com.spotify.mobius2.test.RecordingConnection;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.junit.Before;
import org.junit.Test;

public class RxMobiusLoopTest {
  private RecordingConnection<Boolean> connection;
  private MobiusLoop.Builder<String, Integer, Boolean> builder;

  @Before
  public void setUp() throws Exception {
    connection = new RecordingConnection<>();
    builder =
        Mobius.loop(
            new Update<String, Integer, Boolean>() {
              @Nonnull
              @Override
              public Next<String, Boolean> update(String model, Integer event) {
                return Next.next(model + event.toString());
              }
            },
            new Connectable<Boolean, Integer>() {
              @Nonnull
              @Override
              public Connection<Boolean> connect(Consumer<Integer> output)
                  throws ConnectionLimitExceededException {
                return connection;
              }
            });
  }

  @Test
  public void shouldPropagateIncomingErrorsAsUnrecoverable() throws Exception {
    final RxMobiusLoop<Integer, String, Boolean> loop =
        new RxMobiusLoop<>(builder, "", Collections.emptySet());

    PublishSubject<Integer> input = PublishSubject.create();

    TestObserver<String> subscriber = input.compose(loop).test();

    Exception expected = new RuntimeException("expected");

    input.onError(expected);

    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);
    subscriber.assertError(new UnrecoverableIncomingException(expected));
    assertEquals(0, connection.valueCount());
  }

  @Test
  public void startModelAndEffects() {
    RxMobiusLoop<Integer, String, Boolean> loop =
        new RxMobiusLoop<>(builder, "StartModel", ImmutableSet.of(true, false));
    final TestObserver<String> testObserver = Observable.just(1).compose(loop).test();
    testObserver.assertValue("StartModel");
    testObserver.assertNoErrors();
    assertEquals(2, connection.valueCount());
    connection.assertValues(true, false);
  }

  @Test
  public void shouldSupportStartingALoopWithAnInit() throws Exception {
    MobiusLoop.Builder<String, Integer, Boolean> withInit =
        builder.init(
            new Init<String, Boolean>() {
              @Nonnull
              @Override
              public First<String, Boolean> init(String model) {
                return First.first(model + "-init");
              }
            });

    ObservableTransformer<Integer, String> transformer = RxMobius.loopFrom(withInit, "hi");

    final TestObserver<String> observer = Observable.just(10).compose(transformer).test();

    observer.assertValues("hi-init");
  }

  @Test
  public void shouldThrowIfStartingALoopWithInitAndStartEffects() throws Exception {
    MobiusLoop.Builder<String, Integer, Boolean> withInit =
        builder.init(
            new Init<String, Boolean>() {
              @Nonnull
              @Override
              public First<String, Boolean> init(String model) {
                return First.first(model + "-init");
              }
            });

    ObservableTransformer<Integer, String> transformer =
        RxMobius.loopFrom(withInit, "hi", effects(true));

    Observable.just(10)
        .compose(transformer)
        .test()
        .assertError(t -> t.getMessage().contains("cannot pass in start effects"));
  }
}