package edu.cnm.deepdive.tetris.viewmodel;

import android.content.Context;
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
import edu.cnm.deepdive.tetris.model.entity.Score;
import edu.cnm.deepdive.tetris.model.entity.User;
import edu.cnm.deepdive.tetris.model.pojo.UserScore;
import edu.cnm.deepdive.tetris.service.ScoreRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class ScoreViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final ScoreRepository repository;
  private final MutableLiveData<Long> scoreId;
  private final LiveData<Score> score;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  ScoreViewModel(@ApplicationContext Context context, ScoreRepository repository) {
    this.repository = repository;
    scoreId = new MutableLiveData<>();
    score = Transformations.switchMap(scoreId, repository::read);
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public LiveData<List<UserScore>> getAllScores() {
    return repository.readAllUserScores();
  }

  public LiveData<List<Score>> getUserScores(User user) {
    return repository.readAllScoresForUser(user.getId());
  }

  public MutableLiveData<Long> getScoreId() {
    return scoreId;
  }

  public LiveData<Score> getScore() {
    return score;
  }

  public MutableLiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void save(Score score, User user) {
    score.setPlayerId(user.getId());
    throwable.postValue(null);
    repository
        .create(score)
        .subscribe(
            scoreId::postValue,
            this::postThrowable,
            pending
        );
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }
  
}
