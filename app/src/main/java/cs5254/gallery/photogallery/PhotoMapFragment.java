package cs5254.gallery.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

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


public class PhotoMapFragment extends SupportMapFragment {
    private static final String TAG = "PhotoMapFragment";

    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Bitmap mMapImage;
    private List<GalleryItem> mItems = new ArrayList<>();

    public static PhotoMapFragment newInstance() {
        return new PhotoMapFragment();
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

        new FetchItemsTask().execute();
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
        if (mMap == null || mItems.isEmpty()) {
            return;
        }

        Log.i(TAG, "mItems has "+mItems.size()+" items");

        mMap.clear();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for (GalleryItem item : mItems) {
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

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            updateUI();
        }

    }
}
