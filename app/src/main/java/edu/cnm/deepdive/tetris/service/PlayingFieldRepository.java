package edu.cnm.deepdive.tetris.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayingFieldRepository {

  private static final int MILLISECONDS_PER_SECOND = 1000;
  private static final int DROP_ROWS_PER_TICK = 20;

  private final Random rng;
  private final MutableLiveData<Field> playingField;
  private final MutableLiveData<Dealer> dealer;
  private final MutableLiveData<Boolean> running;
  private final Scheduler moveScheduler;
  private final Scheduler tickScheduler;
  private final Scheduler dropScheduler;

  private Subject<Boolean> ticker;

  @Inject
  PlayingFieldRepository(@ApplicationContext Context context, Random rng) {
    this.rng = rng;
    playingField = new MutableLiveData<>();
    dealer = new MutableLiveData<>();
    running = new MutableLiveData<>();
    moveScheduler = Schedulers.single();
    tickScheduler = Schedulers.single();
    dropScheduler = Schedulers.single();
  }

  public Completable create(int height, int width, int bufferSize, int queueSize) {
    return Single.fromSupplier(() -> new Dealer(queueSize, rng))
        .flatMap((dealer) ->
            Single.fromSupplier(() -> new Field(height, width, bufferSize, dealer))
                .doAfterSuccess((field) -> this.dealer.postValue(dealer))
        )
        .doAfterSuccess(playingField::postValue)
        .doAfterSuccess((ignore) -> running.postValue(false))
        .ignoreElement()
        .subscribeOn(moveScheduler);
  }

  public Observable<Boolean> run() {
    clearTicker();
    Field field = playingField.getValue();
    //noinspection DataFlowIssue
    if (!field.isGameStarted()) {
      field.start();
      playingField.postValue(field);
      dealer.postValue(dealer.getValue());
    }
    ticker = BehaviorSubject.createDefault(Boolean.TRUE);
    return ticker
        .filter(Boolean.TRUE::equals)
        .doAfterNext((ignore) -> running.postValue(true))
        .flatMap((running) -> Observable.just(running)
            .delay(Math.round(field.getSecondsPerTick() * MILLISECONDS_PER_SECOND),
                TimeUnit.MILLISECONDS, tickScheduler))
        .doOnComplete(() -> running.postValue(false))
        .observeOn(moveScheduler)
        .map((ignored) -> tick());
  }

  public void stop() {
    clearTicker();
  }

  public Single<Boolean> moveLeft() {
    //noinspection DataFlowIssue
    return move(() -> playingField.getValue().moveLeft());
  }

  public Single<Boolean> moveRight() {
    //noinspection DataFlowIssue
    return move(() -> playingField.getValue().moveRight());
  }

  public Single<Boolean> rotateLeft() {
    //noinspection DataFlowIssue
    return move(() -> playingField.getValue().rotateLeft());
  }

  public Single<Boolean> rotateRight() {
    //noinspection DataFlowIssue
    return move(() -> playingField.getValue().rotateRight());
  }

  public Single<Boolean> moveDown(boolean freezeOnFailure) {
    //noinspection DataFlowIssue
    return move(() -> playingField.getValue().moveDown(freezeOnFailure));
  }

  public Completable drop(boolean hard) {
    Field field = playingField.getValue();
    //noinspection DataFlowIssue
    return Completable.fromObservable(
        Observable.interval(0, Math.round(
                    field.getSecondsPerTick() * MILLISECONDS_PER_SECOND / DROP_ROWS_PER_TICK),
                TimeUnit.MILLISECONDS, dropScheduler)
            .observeOn(moveScheduler)
            .takeWhile((ignored) -> {
              if (field.moveDown(hard)) {
                return true;
              } else {
                dealer.postValue(dealer.getValue());
                return false;
              }
            })
            .doAfterNext((ignored) -> playingField.postValue(field))
    );
  }

  public LiveData<Field> getPlayingField() {
    return playingField;
  }

  public LiveData<Dealer> getDealer() {
    return dealer;
  }

  public LiveData<Boolean> getRunning() {
    return running;
  }

  private Single<Boolean> move(Supplier<Boolean> supplier) {
    Field field = playingField.getValue();
    return Single.fromSupplier(supplier)
        .doAfterSuccess((success) -> {
          if (success) {
            playingField.postValue(field);
          }
        })
        .subscribeOn(moveScheduler);
  }

  private boolean tick() {
    Field field = playingField.getValue();
    //noinspection DataFlowIssue
    boolean moved = field.moveDown(true);
    if (!moved) {
      dealer.postValue(dealer.getValue());
    }
    playingField.postValue(field);
    boolean stillRunning = moved || !field.isGameOver();
    if (stillRunning) {
      ticker.onNext(Boolean.TRUE);
    } else {
      clearTicker();
    }
    return stillRunning;
  }

  private void clearTicker() {
    if (ticker != null && !ticker.hasComplete()) {
      ticker.onComplete();
    }
    ticker = null;
  }


}
