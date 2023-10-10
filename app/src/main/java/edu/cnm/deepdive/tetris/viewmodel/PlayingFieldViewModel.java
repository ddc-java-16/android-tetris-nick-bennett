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
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class PlayingFieldViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final Context context;
  private final Resources resources;
  private final PlayingFieldRepository playingFieldRepository;
  private final PreferencesRepository preferencesRepository;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  private int playingFieldWidth;
  private final String playingFieldWidthKey;
  private final int playingFieldWidthDefault;

  @Inject
  PlayingFieldViewModel(@ApplicationContext Context context,
      PlayingFieldRepository playingFieldRepository,
      PreferencesRepository preferencesRepository) {
    this.context = context;
    this.resources = context.getResources();
    this.playingFieldRepository = playingFieldRepository;
    this.preferencesRepository = preferencesRepository;
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    playingFieldWidthDefault = resources.getInteger(R.integer.playing_field_width_default);
    playingFieldWidth = playingFieldWidthDefault;
    playingFieldWidthKey = resources.getString(R.string.playing_field_width_key);
    preferencesRepository.registerPreferencesChangedListener((prefs, key) -> {
      if (key.equals(playingFieldWidthKey)) {
        playingFieldWidth = prefs.getInt(playingFieldWidthKey, playingFieldWidthDefault);
        // TODO: 10/10/23 Check to see if we should create a new playing field. 
      }
    });
    create();
  }

  public LiveData<Field> getPlayingField() {
    return playingFieldRepository.getPlayingField();
  }

  public LiveData<Dealer> getDealer() {
    return playingFieldRepository.getDealer();
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void create() {
    Disposable disposable = playingFieldRepository.create(25, playingFieldWidth, 5, 5) // FIXME: 10/9/23 Replace with values from preferences.
        .subscribe(
            () -> {},
            throwable::postValue
        );
    pending.add(disposable);
  }
  
  public void start() {
    // TODO: 10/6/23 Invoke start in repository. 
  }
  
  public void moveLeft() {
    // TODO: 10/6/23 Invoke moveLeft in repository. 
  }
  
  public void moveRight() {
    // TODO: 10/6/23 Invoke moveRight in repository. 
  }
  
  public void rotateLeft() {
    // TODO: 10/6/23 Invoke rotateLeft in repository. 
  }
  
  public void rotateRight() {
    // TODO: 10/6/23 invoke rotateRight in repository. 
  }
  
  public void drop() {
    // TODO: 10/6/23 Invoke
  }
  
  @Override
  public void onStop(@NotNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

}
