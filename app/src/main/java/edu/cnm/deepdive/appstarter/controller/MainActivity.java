/*
 *  Copyright 2023 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.appstarter.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.appstarter.R;
import edu.cnm.deepdive.appstarter.viewmodel.LoginViewModel;
import edu.cnm.deepdive.appstarter.viewmodel.PermissionsViewModel;

/**
 * Serves as a basic container activity&mdash;that is, it presents no UI elements of its own (apart
 * from an options menu), but hosts a {@link androidx.navigation.fragment.NavHostFragment} for
 * presentation of one or more {@link androidx.fragment.app.Fragment} instances, associated with a
 * navigation graph.
 * <p>In addition to the navigation host role, this activity demonstrates the
 * handling of:</p>
 * <ul><li><p>user sign-out (initiated by selection of an options menu item), with automatic
 * navigation back to {@link LoginActivity} on completion of the sign-out;</p></li>
 * <li><p>user-initiated navigation to {@link SettingsActivity};</p></li>
 * <li><p>updates of UI {@link android.view.View} widget properties based on preference values
 * (obtained from {@link edu.cnm.deepdive.appstarter.viewmodel.PreferencesViewModel});</p></li>
 * <li><p>key events in the permissions request flow.</p></li></ul>
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements
    PermissionsExplanationFragment.OnAcknowledgeListener {

  private LoginViewModel loginViewModel;
  private PermissionsViewModel permissionsViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupViewModels();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    int itemId = item.getItemId();
    if (itemId == R.id.settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
    } else if (itemId == R.id.sign_out) {
      loginViewModel.signOut();
    } else {
      handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (!permissionsViewModel.handlePermissionsRequestResult(requestCode, permissions,
        grantResults)) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public void onAcknowledge(String[] permissions) {
    permissionsViewModel.requestPermissions(this, permissions);
  }

  private void setupViewModels() {
    loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    loginViewModel
        .getAccount()
        .observe(this, this::handleAccount);
    permissionsViewModel = new ViewModelProvider(this).get(PermissionsViewModel.class);
    NavHostFragment navHostFragment =
        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    //noinspection DataFlowIssue
    permissionsViewModel.startPermissionsCheck(this, navHostFragment.getNavController());
  }

  private void handleAccount(GoogleSignInAccount account) {
    if (account == null) {
      Intent intent = new Intent(this, LoginActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
  }

}