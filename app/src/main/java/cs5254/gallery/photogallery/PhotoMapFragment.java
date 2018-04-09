package cs5254.gallery.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PhotoMapFragment extends SupportMapFragment implements GalleryItemLab.OnGalleryItemsRefreshedListener {
    private static final String TAG = "PhotoMapFragment";

    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Bitmap mMapImage;

    public static PhotoMapFragment newInstance() {
        return new PhotoMapFragment();
    }

    @Override
    public void onGalleryItemsRefreshed(List<GalleryItem> items) {
        updateUI();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        getMapAsync(googleMap -> {
            mMap = googleMap;
            updateUI();
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_options, menu);

        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mMap != null) {
                    mMap.clear();
                }
                GalleryItemLab.getInstance().refreshItems(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }


    private void updateUI() {
        List<GalleryItem> galleryItems = GalleryItemLab.getInstance().getGalleryItems();

        if (mMap == null || galleryItems.isEmpty()) {
            return;
        }

        Log.i(TAG, "mItems has "+galleryItems.size()+" items");

        mMap.clear();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        int markerCount = 0;

        for (GalleryItem item : galleryItems) {

            if (item.getLat() == 0.0 && item.getLon() == 0.0) {
                continue;
            }

            Log.i(TAG, String.format("Item id=%s lat=%s long=%s title=%s", item.getId(), item.getLat(), item.getLon(), item.getCaption()));
            markerCount++;

            LatLng itemPoint = new LatLng(item.getLat(), item.getLon());

            //
            // Cannot do bitmap loading from network on the main thread.
            //
            // Bitmap itemBitmap = bitmapFromGalleryItem(item);
            // BitmapDescriptor itemBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(mMapImage);
            // itemMarker.icon(itemBitmapDescriptor);
            //
            MarkerOptions itemMarker = new MarkerOptions()
                    .position(itemPoint)
                    .title(item.getCaption());
            bounds.include(itemPoint);
            mMap.addMarker(itemMarker);
        }

        Log.i(TAG, String.format("Expecting %d markers on the map", markerCount));

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);
        mMap.animateCamera(update);
    }

    private Bitmap bitmapFromGalleryItem(GalleryItem item) {
        try {
            FlickrFetchr fetchr = new FlickrFetchr();
            byte[] bytes = fetchr.getUrlBytes(item.getUrl());
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IOException ioe) {
            Log.i(TAG, "Unable to decode bitmap", ioe);
            return null;
        }
    }

}
