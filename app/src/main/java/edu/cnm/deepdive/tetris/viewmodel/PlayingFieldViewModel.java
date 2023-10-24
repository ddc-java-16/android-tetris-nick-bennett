package edu.cnm.deepdive.tetris.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;
  private final String playingFieldWidthKey;
  private final int playingFieldWidthDefault;

  @Inject
  PlayingFieldViewModel(@ApplicationContext Context context,
      PlayingFieldRepository playingFieldRepository,
      PreferencesRepository preferencesRepository) {
    this.playingFieldRepository = playingFieldRepository;
    this.preferencesRepository = preferencesRepository;
    moveSuccess = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    Resources resources = context.getResources();
    playingFieldWidthDefault = resources.getInteger(R.integer.playing_field_width_default);
    playingFieldWidthKey = resources.getString(R.string.playing_field_width_key);
    create();
  }

  public LiveData<Field> getPlayingField() {
    return playingFieldRepository.getPlayingField();
  }

  public LiveData<Dealer> getDealer() {
    return playingFieldRepository.getDealer();
  }

  public LiveData<Boolean> getMoveSuccess() {
    return moveSuccess;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void create() {
    int width = preferencesRepository.get(playingFieldWidthKey, playingFieldWidthDefault);
    execute(playingFieldRepository.create(25, width, 5, 5)); // FIXME: 10/9/23 Replace with values from preferences.
  }

  public void run() {
    execute(playingFieldRepository.run());
  }

  public void stop() {
    playingFieldRepository.stop();
    // TODO: 10/23/23 Set whatever is needed to trigger an update in the UI.
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
    execute(playingFieldRepository.drop());
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
