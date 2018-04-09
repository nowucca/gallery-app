package cs5254.gallery.photogallery;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;

public class PhotoGalleryFragment extends Fragment implements GalleryItemLab.OnGalleryItemsRefreshedListener {
    private static final String TAG = "PhotoGalleryFragment";

    private static final String DIALOG_PHOTO = "dialog_photo";

    private RecyclerView mPhotoRecyclerView;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onGalleryItemsRefreshed(List<GalleryItem> items) {
        setupAdapter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        if (!GalleryItemLab.getInstance().hasGalleryItems()) {
            GalleryItemLab.getInstance().refreshItems(this);
        }

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                (photoHolder, bitmap) -> {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    photoHolder.bindDrawable(drawable);
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                GalleryItemLab.getInstance().refreshItems(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(GalleryItemLab.getInstance().getGalleryItems()));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;
        private int mGalleryItemPosition;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = itemView.findViewById(R.id.item_image_view);
            mItemImageView.setOnClickListener(v -> {
                FragmentManager manager = getFragmentManager();
                PhotoDialogFragment dialogFragment = PhotoDialogFragment.newInstance(mGalleryItemPosition);


                dialogFragment.setCancelable(false);
                dialogFragment.setTargetFragment(PhotoGalleryFragment.this, 0);
                dialogFragment.show(manager, DIALOG_PHOTO);

                });

        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
            GalleryItem item = GalleryItemLab.getInstance().getGalleryItem(mGalleryItemPosition);
            item.setDrawable(drawable);
        }
        public void bindGalleryItemPosition(int position) {
            mGalleryItemPosition = position;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.image_loading);
            photoHolder.bindGalleryItemPosition(position);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }


}
