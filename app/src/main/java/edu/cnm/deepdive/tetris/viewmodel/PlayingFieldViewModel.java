package edu.cnm.deepdive.tetris.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import edu.cnm.deepdive.tetris.service.PlayingFieldRepository;
import edu.cnm.deepdive.tetris.service.PreferencesRepository;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
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
    Disposable disposable = playingFieldRepository.create(25, width, 5, 5) // FIXME: 10/9/23 Replace with values from preferences.
        .subscribe(
            () -> {},
            throwable::postValue
        );
    pending.add(disposable);
  }

  public void moveLeft() {
    move(playingFieldRepository.moveLeft());
  }

  public void moveRight() {
    move(playingFieldRepository.moveRight());
  }

  public void rotateLeft() {
    move(playingFieldRepository.rotateLeft());
  }

  public void rotateRight() {
    move(playingFieldRepository.rotateRight());
  }

  public void drop() {
    // TODO: 10/6/23 Invoke
  }

  @Override
  public void onStop(@NotNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

  private void move(Single<Boolean> task) {
    Disposable disposable = task.subscribe(
        moveSuccess::postValue,
        throwable::postValue
    );
    pending.add(disposable);
  }
}
