package edu.cnm.deepdive.tetris.hilt;

import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.tetris.model.dao.ScoreDao;
import edu.cnm.deepdive.tetris.model.dao.UserDao;
import edu.cnm.deepdive.tetris.service.TetrisDatabase;
import javax.inject.Singleton;

/**
 * Uses Dagger {@link Provides @Provides}-annotated methods to satisfy dependencies on concrete
 * implementations of {@link TetrisDatabase} and {@link UserDao}.
 */
@InstallIn(SingletonComponent.class)
@Module
public final class DatabaseModule {

  DatabaseModule() {
    // Package-private constructor to avoid automatic HTML documentation generation.
  }

  @Provides
  @Singleton
  public TetrisDatabase provideLocalDatabase(@ApplicationContext Context context) {
    return Room
        .databaseBuilder(context, TetrisDatabase.class, TetrisDatabase.NAME)
        .addCallback(new TetrisDatabase.Callback())
        .build();
  }

  @Provides
  @Singleton
  public UserDao provideUserDao(TetrisDatabase database) {
    return database.getUserDao();
  }

  @Provides
  @Singleton
  public ScoreDao provideScoreDao(TetrisDatabase database) {
    return database.getScoreDao();
  }

}
