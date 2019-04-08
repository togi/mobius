package com.spotify.mobius.android;

import com.spotify.mobius.Connectable;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.WorkRunnerConnectable;
import com.spotify.mobius.functions.Producer;
import com.spotify.mobius.runners.WorkRunner;
import javax.annotation.Nonnull;

class DispatchingMobiusController<M, E> implements MobiusLoop.Controller<M, E> {

  private final MobiusLoop.Controller<M, E> controller;
  private final Producer<WorkRunner> modelRunnerFactory;

  DispatchingMobiusController(
      MobiusLoop.Controller<M, E> controller, Producer<WorkRunner> modelRunner) {
    this.controller = controller;
    this.modelRunnerFactory = modelRunner;
  }

  @Override
  public boolean isRunning() {
    return controller.isRunning();
  }

  @Override
  public void connect(final Connectable<M, E> view) {
    controller.connect(new WorkRunnerConnectable<>(modelRunnerFactory, view));
  }

  @Override
  public void disconnect() {
    controller.disconnect();
  }

  @Override
  public void start() {
    controller.start();
  }

  @Override
  public void stop() {
    controller.stop();
  }

  @Override
  public void replaceModel(M model) {
    controller.replaceModel(model);
  }

  @Nonnull
  @Override
  public M getModel() {
    return controller.getModel();
  }
}
