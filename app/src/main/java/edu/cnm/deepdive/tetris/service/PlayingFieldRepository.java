package edu.cnm.deepdive.tetris.service;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import java.util.Random;
import javax.inject.Inject;

public class PlayingFieldRepository {

  private final Random rng;

  @Inject
  PlayingFieldRepository(Context context, Random rng) {
    this.rng = rng;
  }

  public Field create(int height, int width, int bufferSize, int queueSize) {
    Dealer dealer = new Dealer(queueSize, rng);
    return new Field(height, width, bufferSize, dealer);
  }

}
