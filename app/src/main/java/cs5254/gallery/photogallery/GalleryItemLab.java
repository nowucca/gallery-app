package cs5254.gallery.photogallery;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satkinson on 4/8/18.
 */

public class GalleryItemLab {

    private List<GalleryItem> mItems;

    private static GalleryItemLab INSTANCE = new GalleryItemLab();

    public static final GalleryItemLab getInstance() {
        return INSTANCE;
    }

    private GalleryItemLab() {
        mItems = new ArrayList<>();
    }

    public void refreshItems(OnGalleryItemsRefreshedListener listener) {
        new FetchItemsTask(listener).execute();
    }

    public List<GalleryItem> getGalleryItems() {
        return mItems;
    }

    public boolean hasGalleryItems() {
        return mItems != null && !mItems.isEmpty();
    }

    public interface OnGalleryItemsRefreshedListener {
        void onGalleryItemsRefreshed(List<GalleryItem> items);
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {

        OnGalleryItemsRefreshedListener mListener;

        public FetchItemsTask(OnGalleryItemsRefreshedListener listener) {
            this.mListener = listener;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            mListener.onGalleryItemsRefreshed(items);
        }

    }
}
