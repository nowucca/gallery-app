package cs5254.gallery.photogallery;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PhotoGalleryActivity extends MultipleFragmentActivity {

    private static final String TAG = "PhotoGalleryActivity";

    private static final int REQUEST_ERROR = 0;
    private static final String TAG_FRAGMENT_GALLERY = "tag_frag_gallery";
    private static final String TAG_FRAGMENT_MAP = "tag_frag_maps";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected String getStartingFragmentTag() {
        return TAG_FRAGMENT_GALLERY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        registerFragment(TAG_FRAGMENT_GALLERY, PhotoGalleryFragment.newInstance());
        registerFragment(TAG_FRAGMENT_MAP, PhotoMapFragment.newInstance());

        bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_gallery:
                            switchFragment(TAG_FRAGMENT_GALLERY);
                            return true;
                        case R.id.navigation_map:
                            switchFragment(TAG_FRAGMENT_MAP);
                            return true;
                    }
                    return false;
                });

        switchFragment(TAG_FRAGMENT_GALLERY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this,
                    errorCode,
                    REQUEST_ERROR,
                    dialogInterface -> {
                        // Leave if services are unavailable.
                        finish();
                    });

            errorDialog.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
