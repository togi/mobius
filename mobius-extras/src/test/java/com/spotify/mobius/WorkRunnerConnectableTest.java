package com.spotify.mobius;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.spotify.mobius.disposables.Disposable;
import com.spotify.mobius.functions.Consumer;
import com.spotify.mobius.runners.TestWorkRunner;
import com.spotify.mobius.test.RecordingConsumer;
import com.spotify.mobius.test.SimpleConnection;
import javax.annotation.Nonnull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkRunnerConnectableTest {

  private Connectable<Integer, String> intToString =
      new Connectable<Integer, String>() {
        @Nonnull
        @Override
        public Connection<Integer> connect(Consumer<String> output)
            throws ConnectionLimitExceededException {
          return new SimpleConnection<Integer>() {
            @Override
            public void accept(Integer value) {
              output.accept(value.toString());
            }
          };
        }
      };

  private WorkRunnerConnectable<Integer, String> underTest;
  private TestWorkRunner workRunner;

  @Before
  public void setUp() throws Exception {
    workRunner = new TestWorkRunner();
    underTest = new WorkRunnerConnectable<>(() -> workRunner, intToString);
  }

  @After
  public void tearDown() throws Exception {
    workRunner.dispose();
  }

  @Test
  public void messagesAreForwardedToOutput() {
    RecordingConsumer<String> output = new RecordingConsumer<>();

    Connection<Integer> input = underTest.connect(output);
    input.accept(1);
    input.accept(2);
    input.accept(3);

    output.assertValues();

    workRunner.runAll();

    output.assertValues("1", "2", "3");

    input.dispose();
  }

  @Test
  public void runnerIsDisposedWhenConnectionIsDisposed() {
    Disposable disposable = underTest.connect(value -> {});

    assertFalse(workRunner.isDisposed());
    disposable.dispose();
    assertTrue(workRunner.isDisposed());
  }
}
