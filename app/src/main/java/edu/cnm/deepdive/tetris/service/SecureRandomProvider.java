package edu.cnm.deepdive.tetris.service;

import java.security.SecureRandom;
import java.util.Random;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SecureRandomProvider implements RandomProvider {

  @Inject
  public SecureRandomProvider() {
  }

  @Override
  public Random provide() {
    return new SecureRandom();
  }

}
