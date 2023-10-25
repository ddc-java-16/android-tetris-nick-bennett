package edu.cnm.deepdive.tetris.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.model.Block.ShapeType;
import java.util.List;

public class NextQueueAdapter extends ArrayAdapter<ShapeType> {

  private final List<ShapeType> queue;
  private final LayoutInflater inflater;

  public NextQueueAdapter(@NonNull Context context, @NonNull List<ShapeType> queue) {
    super(context, R.layout.item_next_queue, queue);
    this.queue = queue;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return queue.size();
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    ShapeType shape = queue.get(position);
    int level = shape.ordinal();
    ImageView view = (ImageView) ((convertView != null)
            ? convertView
            : inflater.inflate(R.layout.item_next_queue, parent, false));
    view.getDrawable().setLevel(level);
    return view;
  }
}
