package udgaman.com.vachak2.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;

import udgaman.com.vachak2.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageSelectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageSelectFragment extends Fragment implements View.OnClickListener{

    CropImageView mCropView;
    Uri uri;
    private static final int SELECT_PHOTO = 100;

    public ImageSelectFragment() {
        // Required empty public constructor
    }

    public static ImageSelectFragment newInstance(Bundle bundle) {
        ImageSelectFragment fragment = new ImageSelectFragment();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle extras = getArguments();
                uri = (Uri) extras.getParcelable("image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_select, container, false);
                mCropView = (CropImageView)view.findViewById(R.id.cropImageView);
        mCropView.setCropMode(CropImageView.CropMode.SQUARE);
        mCropView.startLoad(uri, mLoadCallback);
        view.findViewById(R.id.buttonPickImage).setOnClickListener(this);
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.buttonDone);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCropView.startCrop(createSaveUri(), mCropCallback, mSaveCallback);
            }
        });
        return view;
    }



    public Uri createSaveUri() {
        return Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
    }


    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
        }

        @Override
        public void onError() {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("image", outputUri);
            bundle.putBoolean("Pic",false);
            imageFragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.image_select_fragment_container, imageFragment);
            //transaction.addToBackStack(null);
            transaction.commit();

        }

        @Override
        public void onError() {
        }
    };

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();
        if(tag.equals("gallery")){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mCropView.startLoad(selectedImage, mLoadCallback);
                }
        }
    }




//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
