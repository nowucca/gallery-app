package cs5254.gallery.photogallery;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GALLERY_ITEM_POSITION = "itemPosition";
    private static final String DIALOG_PHOTO = "dialog_photo";

    private int mGalleryItemPosition;
    private GalleryItem mGalleryItem;

    public PhotoDialogFragment() {
        // Required empty public constructor
    }

    private PhotoDialogFragment(GalleryItem galleryItem) {
        this();
        mGalleryItem = galleryItem;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoDialogFragment newInstance(int galleryItemPosition) {
        PhotoDialogFragment fragment = new PhotoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GALLERY_ITEM_POSITION, galleryItemPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGalleryItemPosition = getArguments().getInt(ARG_GALLERY_ITEM_POSITION, 0);
            mGalleryItem = GalleryItemLab.getInstance().getGalleryItem(mGalleryItemPosition);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_photo_dialog, null);

        TextView caption = v.findViewById(R.id.photo_dialog_caption);
        caption.setText(mGalleryItem.getCaption());

        TextView coordinates = v.findViewById(R.id.photo_dialog_coordinates);
        coordinates.setText(String.format("Lat: %s, Lon: %s", mGalleryItem.getLat(), mGalleryItem.getLon()));

        ImageView photo = v.findViewById(R.id.photo_dialog_image);
        photo.setImageDrawable(mGalleryItem.getDrawable() == null ? getResources().getDrawable(R.drawable.image_loading) :
                mGalleryItem.getDrawable());

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Photo Details")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                })
               .create();
    }

}
