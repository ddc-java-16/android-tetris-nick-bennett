package edu.cnm.deepdive.tetris.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.adapter.NextQueueAdapter;
import edu.cnm.deepdive.tetris.databinding.FragmentGameBinding;
import edu.cnm.deepdive.tetris.model.Dealer;
import edu.cnm.deepdive.tetris.model.Field;
import edu.cnm.deepdive.tetris.viewmodel.PlayingFieldViewModel;

public class GameFragment extends Fragment {

  private FragmentGameBinding binding;
  private PlayingFieldViewModel viewModel;
  private int score;

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
    setupViewModel();
  }

  private void setupUI(LayoutInflater inflater, ViewGroup container) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    binding.moveLeft.setOnClickListener((v) -> viewModel.moveLeft());
    binding.moveRight.setOnClickListener((v) -> viewModel.moveRight());
    binding.rotateLeft.setOnClickListener((v) -> viewModel.rotateLeft());
    binding.rotateRight.setOnClickListener((v) -> viewModel.rotateRight());
    binding.drop.setOnClickListener((v) -> viewModel.drop());
    binding.run.setOnClickListener((v) -> viewModel.run());
    binding.stop.setOnClickListener((v) -> viewModel.stop());
    binding.showScores.setOnClickListener((v) -> Navigation.findNavController(binding.getRoot())
        .navigate(GameFragmentDirections.navigateToScores(score)));
    // TODO: 10/17/23 Initialize any fields.
  }

  private void setupViewModel() {
    viewModel = new ViewModelProvider(requireActivity())
        .get(PlayingFieldViewModel.class);
    viewModel
        .getPlayingField()
        .observe(getViewLifecycleOwner(), this::handlePlayingField);
    viewModel
        .getDealer()
        .observe(getViewLifecycleOwner(), this::handleDealer);
  }

  private void handlePlayingField(Field playingField) {
    score = playingField.getScore();
    binding.playingField.post(() -> binding.playingField.setPlayingField(playingField));
    binding.level.setText(String.valueOf(playingField.getLevel()));
    binding.rowsRemoved.setText(String.valueOf(playingField.getLevelRowsRemoved()));
    binding.score.setText(String.valueOf(playingField.getScore()));
    binding.finalScore.setText(getString(R.string.final_score_format, playingField.getScore()));
    binding.gameOverLayout.setVisibility(playingField.isGameOver() ? View.VISIBLE : View.GONE);
  }

  private void handleDealer(Dealer dealer) {
    //noinspection DataFlowIssue
    NextQueueAdapter adapter = new NextQueueAdapter(getContext(), dealer.getQueue());
    binding.nextQueue.setAdapter(adapter);
  }

}