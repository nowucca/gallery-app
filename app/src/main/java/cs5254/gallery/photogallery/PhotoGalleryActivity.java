package cs5254.gallery.photogallery;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PhotoGalleryActivity extends MultipleFragmentActivity {

    private static final String TAG = "PhotoGalleryActivity";

    private static final int REQUEST_ERROR = 0;
    private static final String TAG_FRAGMENT_GALLERY = "tag_frag_gallery";
    private static final String TAG_FRAGMENT_MAP = "tag_frag_maps";

    private static final String EXTRA_SELECTED_NAVIGATION_ID = "selected_navigation_item";

    private BottomNavigationView bottomNavigationView;

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
        bottomNavigationView.setSelectedItemId(R.id.navigation_gallery);
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
        savedInstanceState.putInt(EXTRA_SELECTED_NAVIGATION_ID,
                bottomNavigationView.getSelectedItemId());

        Log.i(TAG, "onSaveInstanceState called");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bottomNavigationView.setSelectedItemId(savedInstanceState.getInt(EXTRA_SELECTED_NAVIGATION_ID,
                R.id.navigation_gallery));
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
