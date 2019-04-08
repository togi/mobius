package com.spotify.mobius;

import com.spotify.mobius.functions.Consumer;
import com.spotify.mobius.functions.Producer;
import com.spotify.mobius.runners.WorkRunner;
import javax.annotation.Nonnull;

/** Wraps a Connectable and redispatches all incoming messages on a given WorkRunner */
public class WorkRunnerConnectable<I, O> implements Connectable<I, O> {

  private final Producer<WorkRunner> workRunnerFactory;
  private final Connectable<I, O> innerConnectable;

  public WorkRunnerConnectable(
      Producer<WorkRunner> workRunnerFactory, Connectable<I, O> innerConnectable) {
    this.workRunnerFactory = workRunnerFactory;
    this.innerConnectable = innerConnectable;
  }

  @Nonnull
  @Override
  public Connection<I> connect(final Consumer<O> output) throws ConnectionLimitExceededException {
    final WorkRunner inputRunner = workRunnerFactory.get();
    final Connection<I> innerConnection = innerConnectable.connect(output);

    return new Connection<I>() {
      @Override
      public void accept(final I value) {
        inputRunner.post(
            new Runnable() {
              @Override
              public void run() {
                innerConnection.accept(value);
              }
            });
      }

      @Override
      public void dispose() {
        inputRunner.dispose();
        innerConnection.dispose();
      }
    };
  }
}
