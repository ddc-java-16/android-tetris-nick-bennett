package edu.cnm.deepdive.tetris.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.adapter.NextQueueAdapter;
import edu.cnm.deepdive.tetris.databinding.FragmentGameBinding;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import edu.cnm.deepdive.tetris.model.entity.Score;
import edu.cnm.deepdive.tetris.model.entity.User;
import edu.cnm.deepdive.tetris.viewmodel.PlayingFieldViewModel;
import edu.cnm.deepdive.tetris.viewmodel.ScoreViewModel;
import edu.cnm.deepdive.tetris.viewmodel.UserViewModel;
import java.time.Instant;

public class GameFragment extends Fragment {

  private FragmentGameBinding binding;
  private PlayingFieldViewModel playingFieldViewModel;
  private ScoreViewModel scoreViewModel;
  private UserViewModel userViewModel;
  private int score;
  private int level;
  private int rowsRemoved;
  private User currentUser;
  private Boolean inProgress;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    setupUI(inflater, container);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupViewModels();
  }

  private void setupUI(LayoutInflater inflater, ViewGroup container) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    binding.moveLeft.setOnClickListener((v) -> playingFieldViewModel.moveLeft());
    binding.moveRight.setOnClickListener((v) -> playingFieldViewModel.moveRight());
    binding.rotateLeft.setOnClickListener((v) -> playingFieldViewModel.rotateLeft());
    binding.rotateRight.setOnClickListener((v) -> playingFieldViewModel.rotateRight());
    binding.drop.setOnClickListener((v) -> playingFieldViewModel.drop());
    binding.run.setOnClickListener((v) -> playingFieldViewModel.run());
    binding.stop.setOnClickListener((v) -> playingFieldViewModel.stop());
    binding.showScores.setOnClickListener((v) -> Navigation.findNavController(binding.getRoot())
        .navigate(GameFragmentDirections.navigateToScores(score)));
    // TODO: 10/17/23 Initialize any fields.
  }

  private void setupViewModels() {
    LifecycleOwner owner = getViewLifecycleOwner();
    FragmentActivity activity = requireActivity();
    setupPlayingFieldViewModel(activity, owner);
    setupScoreViewModel(activity, owner);
    setupUserViewModel(activity, owner);
  }

  private void setupUserViewModel(FragmentActivity activity, LifecycleOwner owner) {
    userViewModel = new ViewModelProvider(activity)
        .get(UserViewModel.class);
    userViewModel
        .getCurrentUser()
        .observe(owner, (user) -> this.currentUser = user);
  }

  private void setupScoreViewModel(FragmentActivity activity, LifecycleOwner owner) {
    scoreViewModel = new ViewModelProvider(activity)
        .get(ScoreViewModel.class);
    // TODO: 10/26/23 Observe scoreId or Score from view model.
  }

  private void setupPlayingFieldViewModel(FragmentActivity activity, LifecycleOwner owner) {
    playingFieldViewModel = new ViewModelProvider(activity)
        .get(PlayingFieldViewModel.class);
    playingFieldViewModel
        .getPlayingField()
        .observe(owner, this::handlePlayingField);
    playingFieldViewModel
        .getDealer()
        .observe(owner, this::handleDealer);
    playingFieldViewModel
        .getInProgress()
        .observe(owner, this::handleInProgress);
  }

  private void handlePlayingField(Field playingField) {
    score = playingField.getScore();
    level = playingField.getLevel();
    rowsRemoved = playingField.getRowsRemoved();
    binding.playingField.post(() -> binding.playingField.setPlayingField(playingField));
    binding.level.setText(String.valueOf(playingField.getLevel()));
    binding.rowsRemoved.setText(String.valueOf(playingField.getLevelRowsRemoved()));
    binding.score.setText(String.valueOf(score));
    binding.finalScore.setText(getString(R.string.final_score_format, score));
    binding.gameOverLayout.setVisibility(playingField.isGameOver() ? View.VISIBLE : View.GONE);
  }

  private void handleDealer(Dealer dealer) {
    NextQueueAdapter adapter = new NextQueueAdapter(requireContext(), dealer.getQueue());
    binding.nextQueue.setAdapter(adapter);
  }

  private void handleInProgress(Boolean inProgress) {
    if (Boolean.FALSE.equals(inProgress) && Boolean.TRUE.equals(this.inProgress)) {
      Score score = new Score();
      score.setStarted(Instant.now()); // FIXME: 10/26/23 This should come from repository.
      score.setValue(this.score);
      score.setRowsRemoved(rowsRemoved);
      scoreViewModel.save(score, currentUser);
    }
    this.inProgress = inProgress;
  }


}