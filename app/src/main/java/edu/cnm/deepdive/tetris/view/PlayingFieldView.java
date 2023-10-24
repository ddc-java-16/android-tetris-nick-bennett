package edu.cnm.deepdive.tetris.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.model.Block;
import edu.cnm.deepdive.tetris.model.Block.ShapeType;
import edu.cnm.deepdive.tetris.model.Field;

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
    private final int bufferHeight;
    private final int visibleHeight;
    private final int width;
    private final int currentBlockOpacity;
    private final int ghostBlockOpacity;

    public Adapter(@NonNull Context context) {
      super(context, R.layout.item_playing_field);
      inflater = LayoutInflater.from(context);
      bufferHeight = playingField.getBufferHeight();
      visibleHeight = playingField.getHeight() - bufferHeight;
      width = playingField.getWidth();
      Resources res = context.getResources();
      currentBlockOpacity = res.getInteger(R.integer.current_block_opacity);
      ghostBlockOpacity = res.getInteger(R.integer.ghost_block_opacity);
    }

    @Override
    public int getCount() {
      return visibleHeight * width;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      int col = position % width;
      int row = position / width + bufferHeight;
      Block currentBlock = playingField.getCurrentBlock();
      Block ghostBlock = playingField.getGhostBlock();
      ShapeType shapeType;
      int alpha = currentBlockOpacity;
      if (currentBlock != null && isInBlock(currentBlock, row, col)) {
        shapeType = currentBlock.getType();
      } else if (ghostBlock != null && isInBlock(ghostBlock, row, col)) {
        shapeType = ghostBlock.getType();
        alpha = ghostBlockOpacity;
      } else {
        shapeType = playingField.get(row, col);
      }
      ImageView view = (ImageView) ((convertView != null)
          ? convertView
          : inflater.inflate(R.layout.item_playing_field, parent, false));
      view.getDrawable().setLevel(
          (shapeType == null) ? ShapeType.values().length : shapeType.ordinal());
      view.getDrawable().setAlpha(alpha);
      return view;
    }

    private boolean isInBlock(Block block, int row, int col) {
      int blockTop = block.getTopRow();
      int blockLeft = block.getLeftColumn();
      int blockHeight = block.getLastOccupiedRow() + 1;
      int blockWidth = block.getWidth();
      return row >= blockTop && row < blockTop + blockHeight
          && col >= blockLeft && col < blockLeft + blockWidth
          && block.isOccupied(row - blockTop, col - blockLeft);
    }
  }

}
