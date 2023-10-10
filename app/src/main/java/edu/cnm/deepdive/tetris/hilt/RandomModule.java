package edu.cnm.deepdive.tetris.hilt;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import java.security.SecureRandom;
import java.util.Random;

@Module
@InstallIn(SingletonComponent.class)
public final class RandomModule {

  @Provides
  public Random provideSecureRandom() {
    return new SecureRandom();
  }

}
