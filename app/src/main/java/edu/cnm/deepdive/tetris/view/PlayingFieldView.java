package edu.cnm.deepdive.tetris.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.model.Block.ShapeType;
import edu.cnm.deepdive.tetris.model.Field;
import java.util.EnumMap;
import java.util.Map;

public class PlayingFieldView extends GridView {

  private Field playingField;

  public PlayingFieldView(Context context) {
    super(context);
  }

  public PlayingFieldView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PlayingFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public PlayingFieldView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public Field getPlayingField() {
    return playingField;
  }

  public void setPlayingField(Field playingField) {
    this.playingField = playingField;
    int heightInBricks = playingField.getHeight() - playingField.getBufferHeight();
    int widthInBricks = playingField.getWidth();
    int maxBrickHeight = getHeight() / heightInBricks;
    int maxBrickWidth = getWidth() / widthInBricks;
    ViewGroup.LayoutParams params = getLayoutParams();
    int columnSize = Math.min(maxBrickHeight, maxBrickWidth);
    params.height = columnSize * heightInBricks;
    params.width = columnSize * widthInBricks;
    setLayoutParams(params);
    setNumColumns(widthInBricks);
    setAdapter(new Adapter(getContext()));
  }

  private class Adapter extends ArrayAdapter<ShapeType> {

    private final LayoutInflater inflater;

    public Adapter(@NonNull Context context) {
      super(context, R.layout.item_playing_field);
      inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
      return (playingField.getHeight() - playingField.getBufferHeight()) * playingField.getWidth();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      int col = position % playingField.getWidth();
      int row = position / playingField.getWidth() + playingField.getBufferHeight();
      ShapeType shapeType = playingField.get(row, col);
      ImageView view = (ImageView) ((convertView != null)
          ? convertView
          : inflater.inflate(R.layout.item_playing_field, parent, false));
      view.getDrawable().setLevel(0);
//      view.getDrawable().setLevel(
//          (shapeType == null) ? ShapeType.values().length : shapeType.ordinal());
      return view;
    }

  }

}
