package edu.cnm.deepdive.tetris.model.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.time.Instant;

@Entity(
    tableName = "score",
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "user_id", childColumns = "player_id", onDelete = ForeignKey.CASCADE
        )
    }
)
public class Score {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "score_id")
  private long id;

  @NonNull
  private Instant created = Instant.MIN;

  @NonNull
  private Instant started = Instant.MIN;

  private long duration;

  @ColumnInfo(index = true)
  private long value;

  @ColumnInfo(name = "rows_removed")
  private int rowsRemoved;

  @ColumnInfo(name = "player_id", index = true)
  private long playerId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public Instant getCreated() {
    return created;
  }

  public void setCreated(@NonNull Instant created) {
    this.created = created;
  }

  @NonNull
  public Instant getStarted() {
    return started;
  }

  public void setStarted(@NonNull Instant started) {
    this.started = started;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public int getRowsRemoved() {
    return rowsRemoved;
  }

  public void setRowsRemoved(int rowsRemoved) {
    this.rowsRemoved = rowsRemoved;
  }

  public long getPlayerId() {
    return playerId;
  }

  public void setPlayerId(long playerId) {
    this.playerId = playerId;
  }

}
