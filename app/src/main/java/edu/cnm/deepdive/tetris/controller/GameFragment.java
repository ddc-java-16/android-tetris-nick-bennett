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
import edu.cnm.deepdive.tetris.databinding.FragmentGameBinding;
import edu.cnm.deepdive.tetris.viewmodel.PlayingFieldViewModel;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

public class GameFragment extends Fragment {

  private FragmentGameBinding binding;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    // TODO: 10/17/23 Initialize any fields.
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull @NotNull View view,
      @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    var playingFieldViewModel = new ViewModelProvider(requireActivity())
        .get(PlayingFieldViewModel.class);
    playingFieldViewModel
        .getPlayingField()
        .observe(getViewLifecycleOwner(), (playingField) -> {
          binding.playingField.setPlayingField(playingField);
        });
  }

}