package edu.cnm.deepdive.tetris.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Random;
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
                .doAfterSuccess((ignored) -> this.dealer.postValue(dealer))
        )
        .doAfterSuccess((field) -> playingField.postValue(field))
        .ignoreElement()
        .subscribeOn(scheduler);
  }

  public LiveData<Field> getPlayingField() {
    return playingField;
  }

  public LiveData<Dealer> getDealer() {
    return dealer;
  }

}
