package edu.cnm.deepdive.tetris.controller;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.adapter.NextQueueAdapter;
import edu.cnm.deepdive.tetris.databinding.FragmentGameBinding;
import edu.cnm.deepdive.tetris.viewmodel.PlayingFieldViewModel;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class GameFragment extends Fragment {

  private FragmentGameBinding binding;
  private PlayingFieldViewModel viewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    binding.moveLeft.setOnClickListener((v) -> viewModel.moveLeft());
    binding.moveRight.setOnClickListener((v) -> viewModel.moveRight());
    binding.rotateLeft.setOnClickListener((v) -> viewModel.rotateLeft());
    binding.rotateRight.setOnClickListener((v) -> viewModel.rotateRight());
    binding.drop.setOnClickListener((v) -> viewModel.drop());
    binding.run.setOnClickListener((v) -> viewModel.run());
    binding.stop.setOnClickListener((v) -> viewModel.stop());
    // TODO: 10/17/23 Initialize any fields.
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull @NotNull View view,
      @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity())
        .get(PlayingFieldViewModel.class);
    viewModel
        .getPlayingField()
        .observe(getViewLifecycleOwner(), (playingField) -> {
          binding.playingField.post(() -> binding.playingField.setPlayingField(playingField));
          binding.level.setText(String.valueOf(playingField.getLevel()));
          binding.rowsRemoved.setText(String.valueOf(playingField.getRowsRemoved()));
          binding.score.setText(String.valueOf(playingField.getScore()));
        });
    viewModel
        .getDealer()
        .observe(getViewLifecycleOwner(), (dealer) -> {
          //noinspection DataFlowIssue
          NextQueueAdapter adapter = new NextQueueAdapter(getContext(), dealer.getQueue());
          binding.nextQueue.setAdapter(adapter);
        });
  }

}