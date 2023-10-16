package edu.cnm.deepdive.tetris.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import edu.cnm.deepdive.tetris.model.entity.Score;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

@Dao
public interface ScoreDao {

  @Insert
  Single<Long> insert(Score score);

  @Delete
  Single<Integer> delete(Score score);

  @Query("SELECT * FROM score WHERE score_id = :id")
  LiveData<Score> select(long id);

  @Query("SELECT * FROM score WHERE player_id = :playerId ORDER BY value DESC")
  LiveData<List<Score>> selectByPlayerId(long playerId);

  // TODO: 10/16/23 Add query to retrieve top N scores w/ user display names.
}
