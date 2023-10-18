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
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayingFieldRepository {

  private final Random rng;
  private final MutableLiveData<Field> playingField;
  private final MutableLiveData<Dealer> dealer;
  private final Scheduler scheduler;

  @Inject
  PlayingFieldRepository(@ApplicationContext Context context, Random rng) {
    this.rng = rng;
    playingField = new MutableLiveData<>();
    dealer = new MutableLiveData<>();
    scheduler = Schedulers.single();
  }

  public Completable create(int height, int width, int bufferSize, int queueSize) {
    return Single.fromSupplier(() -> new Dealer(queueSize, rng))
        .flatMap((dealer) ->
            Single.fromSupplier(() -> new Field(height, width, bufferSize, dealer))
                .doAfterSuccess((field) -> {
                  field.start();
                  this.dealer.postValue(dealer);
                })
        )
        .doAfterSuccess(playingField::postValue)
        .ignoreElement()
        .subscribeOn(scheduler);
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

  public Single<Boolean> moveDown() {
    //noinspection DataFlowIssue
    return move(() -> playingField.getValue().moveDown());
  }

  public Completable drop(long interval) {
    Field field = playingField.getValue();
    return Completable.fromObservable(
            Observable.interval(0, interval, TimeUnit.MILLISECONDS)
                .takeWhile((ignored) -> {
                  //noinspection DataFlowIssue
                  boolean moved = field.moveDown();
                  if (moved) {
                    playingField.postValue(field);
                  }
                  return moved;
                })
        )
        .subscribeOn(scheduler);
  }

  public LiveData<Field> getPlayingField() {
    return playingField;
  }

  public LiveData<Dealer> getDealer() {
    return dealer;
  }

  private Single<Boolean> move(Supplier<Boolean> supplier) {
    Field field = playingField.getValue();
    return Single.fromSupplier(supplier)
        .doAfterSuccess((success) -> {
          if (success) {
            playingField.postValue(field);
          }
        })
        .subscribeOn(scheduler);
  }
  
}
