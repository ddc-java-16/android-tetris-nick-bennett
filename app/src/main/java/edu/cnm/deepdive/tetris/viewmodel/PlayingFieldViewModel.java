package edu.cnm.deepdive.tetris.viewmodel;

import android.content.Context;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.tetris.model.Field;
import edu.cnm.deepdive.tetris.service.PlayingFieldRepository;
import edu.cnm.deepdive.tetris.service.PreferencesRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class PlayingFieldViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PlayingFieldRepository playingFieldRepository;
  private final PreferencesRepository preferencesRepository;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  PlayingFieldViewModel(Context context,
      PlayingFieldRepository playingFieldRepository, PreferencesRepository preferencesRepository) {
    this.playingFieldRepository = playingFieldRepository;
    this.preferencesRepository = preferencesRepository;
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public LiveData<Field> getPlayingField() {
    return playingFieldRepository.getPlayingField();
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void create() {
    // TODO: 10/6/23 Invoke create in repository, with settings from settings repository.   
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
