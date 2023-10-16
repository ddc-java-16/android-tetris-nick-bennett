package edu.cnm.deepdive.tetris.service;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.tetris.model.dao.ScoreDao;
import edu.cnm.deepdive.tetris.model.entity.Score;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScoreRepository {

  private final ScoreDao dao;

  @Inject
  public ScoreRepository(ScoreDao dao) {
    this.dao = dao;
  }

  public Single<Long> create(Score score) {
    return dao.insert(score)
        .subscribeOn(Schedulers.io());
  }

  public Single<Integer> delete(Score score) {
    return dao.delete(score)
        .subscribeOn(Schedulers.io());
  }

  public LiveData<Score> read(long id) {
    return dao.select(id);
  }

  public LiveData<List<Score>> readAllScoresForUser(long userId) {
    return dao.selectByPlayerId(userId);
  }

  // TODO: 10/16/23 Define  method to get top N scores.

}
