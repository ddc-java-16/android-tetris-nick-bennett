package edu.cnm.deepdive.tetris.model.pojo;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import java.time.Instant;

@DatabaseView(
    viewName = "user_score",
    value = "SELECT s.player_id,\n"
        + "       u.display_name,\n"
        + "       s.value,\n"
        + "       s.rows_removed,\n"
        + "       s.created\n"
        + "FROM user AS u\n"
        + "         JOIN score AS s ON u.user_id = s.player_id"
)
public class UserScore {

  @ColumnInfo(name = "player_id")
  private long playerId;

  @ColumnInfo(name = "display_name")
  private String displayName;

  private long value;

  @ColumnInfo(name = "rows_removed")
  private int rowsRemoved;

  private Instant created;

  public long getPlayerId() {
    return playerId;
  }

  public void setPlayerId(long playerId) {
    this.playerId = playerId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }
}
