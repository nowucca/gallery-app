package cs5254.gallery.photogallery;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       

    }

    @Override
    protected void onResume() {
        super.onResume();

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
}
