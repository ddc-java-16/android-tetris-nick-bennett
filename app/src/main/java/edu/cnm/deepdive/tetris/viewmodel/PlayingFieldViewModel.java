package edu.cnm.deepdive.tetris.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import edu.cnm.deepdive.tetris.service.PlayingFieldRepository;
import edu.cnm.deepdive.tetris.service.PreferencesRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class PlayingFieldViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PlayingFieldRepository playingFieldRepository;
  private final PreferencesRepository preferencesRepository;
  private final MutableLiveData<Boolean> moveSuccess;
  private final LiveData<Boolean> inProgress;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;
  private final String playingFieldWidthKey;
  private final int playingFieldWidthDefault;
  private final String nextQueueLengthKey;
  private final int nextQueueLengthDefault;

  @Inject
  PlayingFieldViewModel(@ApplicationContext Context context,
      PlayingFieldRepository playingFieldRepository,
      PreferencesRepository preferencesRepository) {
    this.playingFieldRepository = playingFieldRepository;
    this.preferencesRepository = preferencesRepository;
    moveSuccess = new MutableLiveData<>();
    inProgress = Transformations.map(playingFieldRepository.getPlayingField(), 
        (field) -> !field.isGameOver());
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    Resources resources = context.getResources();
    playingFieldWidthKey = resources.getString(R.string.playing_field_width_key);
    playingFieldWidthDefault = resources.getInteger(R.integer.playing_field_width_default);
    nextQueueLengthKey = resources.getString(R.string.next_queue_length_key);
    nextQueueLengthDefault = resources.getInteger(R.integer.next_queue_length_default);
    create();
  }

  public LiveData<Field> getPlayingField() {
    return playingFieldRepository.getPlayingField();
  }

  public LiveData<Dealer> getDealer() {
    return playingFieldRepository.getDealer();
  }

  public LiveData<Boolean> getRunning() {
    return Transformations.distinctUntilChanged(playingFieldRepository.getRunning());
  }

  public LiveData<Boolean> getMoveSuccess() {
    return moveSuccess;
  }

  public LiveData<Boolean> getInProgress() {
    return Transformations.distinctUntilChanged(inProgress);
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void create() {
    int width = preferencesRepository.get(playingFieldWidthKey, playingFieldWidthDefault);
    int nextQueueLength = preferencesRepository.get(nextQueueLengthKey, nextQueueLengthDefault);
    execute(playingFieldRepository.create(25, width, 5, nextQueueLength));
  }

  public void run() {
    execute(playingFieldRepository.run());
  }

  public void stop() {
    playingFieldRepository.stop();
  }

  public void moveLeft() {
    execute(playingFieldRepository.moveLeft());
  }

  public void moveRight() {
    execute(playingFieldRepository.moveRight());
  }

  public void rotateLeft() {
    execute(playingFieldRepository.rotateLeft());
  }

  public void rotateRight() {
    execute(playingFieldRepository.rotateRight());
  }

  public void drop() {
    execute(playingFieldRepository.drop(false));
  }

  @Override
  public void onPause(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onPause(owner);
    stop();
  }

  @Override
  public void onStop(@NotNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

  private void execute(Single<Boolean> task) {
    moveSuccess.postValue(null);
    throwable.postValue(null);
    task.subscribe(
        moveSuccess::postValue,
        this::postThrowable,
        pending
    );
  }

  private void execute(Completable task) {
    moveSuccess.postValue(null);
    throwable.postValue(null);
    task.subscribe(
        () -> {},
        this::postThrowable,
        pending
    );
  }

  private void execute(Observable<Boolean> task) {
    moveSuccess.postValue(null);
    throwable.postValue(null);
    task.subscribe(
        moveSuccess::postValue,
        this::postThrowable,
        () -> {},
        pending
    );
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
