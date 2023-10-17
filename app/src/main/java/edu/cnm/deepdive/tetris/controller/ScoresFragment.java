package edu.cnm.deepdive.tetris.controller;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.Navigation;
import edu.cnm.deepdive.tetris.R;
import edu.cnm.deepdive.tetris.controller.GameFragmentDirections.NavigateToScores;
import edu.cnm.deepdive.tetris.databinding.FragmentScoresBinding;
import org.jetbrains.annotations.NotNull;

public class ScoresFragment extends Fragment {

  private FragmentScoresBinding binding;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentScoresBinding.inflate(inflater, container, false);
    binding.dummyText.setText(String.valueOf(ScoresFragmentArgs.fromBundle(getArguments()).getScore()));
    // TODO: 10/17/23 Initialize field contents.
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // TODO: 10/17/23 Connect to viewmodels and add observers.
    //noinspection DataFlowIssue
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);
  }

}