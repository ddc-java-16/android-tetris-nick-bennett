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
package edu.cnm.deepdive.tetris.service;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.tetris.NavigationGraphDirections;
import edu.cnm.deepdive.tetris.controller.PermissionsExplanationFragment;
import edu.cnm.deepdive.tetris.viewmodel.PermissionsViewModel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implements a {@link LiveData}-based repository of permissions granted by the user, and handles
 * the low-level details of the permissions flow. Note that while this class is declared (using
 * {@link Singleton}) as a singleton, the {@link #startPermissionsCheck(Activity, NavController)},
 * {@link #requestPermissions(Activity, String[])}, and
 * {@link #handlePermissionsRequestResult(int, String[], int[])} methods are <em>not</em>
 * thread-safe; as a rule, those should be invoked only by an instance of
 * {@link PermissionsViewModel}, which should in turn be
 * scoped to the main activity of the app.
 */
@Singleton
public class PermissionsRepository {

  private static final int PERMISSIONS_REQUEST_CODE = 128158634;

  private final MutableLiveData<Set<String>> permissions;

  @Inject
  PermissionsRepository(@SuppressWarnings("unused") @ApplicationContext Context context) {
    permissions = new MutableLiveData<>(new HashSet<>());
  }

  /**
   * Returns {@link LiveData} of all permissions declared in {@code AndroidManifest.xml} that have
   * been granted (implicitly or explicitly) to the app.
   */
  public LiveData<Set<String>> getPermissions() {
    return permissions;
  }

  /**
   * Sets the permission flow in motion. This proceeds by obtaining a {@link String String[]} of all
   * permissions declared in <code>AndroidManifest.xml</code>, invoking
   * {@link ContextCompat#checkSelfPermission(Context, String)} to determine what permissions must
   * be requested from the user, invoking
   * {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)} to learn which of
   * those permissions should be explained to the user before requesting, and taking the appropriate
   * next step.
   *
   * @param activity      {@link Activity} initiating the flow and providing the
   *                      {@link Activity#onRequestPermissionsResult(int, String[], int[])}
   *                      callback.
   * @param navController {@link NavController} used if/when navigating to the {@link
   *                      PermissionsExplanationFragment}.
   */
  public void startPermissionsCheck(Activity activity, NavController navController) {
    try {
      String[] permissionsNeeded = getManifestPermissions(activity);
      Set<String> permissionsGranted = permissions.getValue();
      List<String> permissionsToRequest = new LinkedList<>();
      List<String> permissionsToExplain = new LinkedList<>();
      queryPermissionGrants(activity,
          permissionsNeeded, permissionsGranted, permissionsToRequest, permissionsToExplain);
      permissions.postValue(permissionsGranted);
      if (!permissionsToExplain.isEmpty()) {
        navController.navigate(NavigationGraphDirections.explainPermissions(
            permissionsToExplain.toArray(new String[0]),
            permissionsToRequest.toArray(new String[0])));
      } else if (!permissionsToRequest.isEmpty()) {
        requestPermissions(activity, permissionsToRequest.toArray(new String[0]));
      }
    } catch (NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Continues the permissions flow after rationales (if any) have been displayed to the user.
   *
   * @param activity    {@link Activity} providing the
   *                    {@link Activity#onRequestPermissionsResult(int, String[], int[])} callback.
   * @param permissions Permissions to be requested of the user.
   */
  public void requestPermissions(Activity activity, String[] permissions) {
    ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_CODE);
  }

  /**
   * Handles response passed (indirectly) from
   * {@link Activity#onRequestPermissionsResult(int, String[], int[])}, adding all granted
   * permissions to a {@link LiveData LiveData&lt;Set&lt;String&gt;&gt;}.
   *
   * @param requestCode          Request code received by
   *                             {@link Activity#onRequestPermissionsResult(int, String[], int[])},
   *                             which must match a private constant in this class.
   * @param permissionsRequested {@link String String[]} containing all permissions requested of the
   *                             user by last invocation of
   *                             {@link #requestPermissions(Activity, String[])}
   * @param grantResults         {@code int[]} of grant/deny results.
   * @return {@code true} if {@code requestCode} matched the expected value and results were
   * processed; {@code false} otherwise.
   */
  public boolean handlePermissionsRequestResult(int requestCode,
      @NonNull String[] permissionsRequested,
      @NonNull int[] grantResults) {
    boolean handled;
    if (requestCode == PERMISSIONS_REQUEST_CODE) {
      Set<String> permissions = Objects.requireNonNull(this.permissions.getValue());
      for (int i = 0; i < permissionsRequested.length; i++) {
        String permission = permissionsRequested[i];
        int result = grantResults[i];
        if (result == PackageManager.PERMISSION_GRANTED) {
          permissions.add(permission);
        } else {
          permissions.remove(permission);
        }
      }
      this.permissions.postValue(permissions);
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  private String[] getManifestPermissions(Activity activity) throws NameNotFoundException {
    //noinspection deprecation
    PackageInfo info = activity
        .getPackageManager()
        .getPackageInfo(activity.getPackageName(),
            PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
    return info.requestedPermissions;
  }

  private void queryPermissionGrants(Activity activity,
      String[] permissionsNeeded, Set<String> permissionsGranted,
      List<String> permissionsToRequest, List<String> permissionsToExplain) {
    for (String permission : permissionsNeeded) {
      if (ContextCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(permission);
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
          permissionsToExplain.add(permission);
        }
      } else {
        permissionsGranted.add(permission);
      }
    }
  }

}
