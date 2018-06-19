package udgaman.com.vachak2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.activities.ImageSelectActivity;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.util.CheckNetworkConnection;
import udgaman.com.vachak2.util.UpdateProfileStatus;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment implements View.OnClickListener{

//    private OnFragmentInteractionListener mListener;


    Uri uri;
    ProgressBar progressBar;
    boolean pic;
    private static final int SELECT_PHOTO = 100;

    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle extras = getArguments();
            uri = (Uri) extras.getParcelable("image");
            pic = extras.getBoolean("Pic");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.image_fragment_imageView);
        imageView.setImageURI(uri);
        progressBar = (ProgressBar)view.findViewById(R.id.image_fragment_progressBar);
        view.findViewById(R.id.image_fragment_button_update).setOnClickListener(this);
        view.findViewById(R.id.image_fragment_button_like).setOnClickListener(this);
        view.findViewById(R.id.image_fragment_button_comment).setOnClickListener(this);

        if(new CheckNetworkConnection().isOnline(getContext().getApplicationContext()) && !pic){
            new UpdateProfilePic().execute();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
     String tag = (String) view.getTag();
        if(tag.equals("update")){
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

                    ImageSelectFragment imageSelectFragment = new ImageSelectFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("image", selectedImage);
                    imageSelectFragment.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.image_select_fragment_container, imageSelectFragment);
                    //transaction.addToBackStack(null);
                    transaction.commit();

                }
        }
    }




    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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


    protected class UpdateProfilePic extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                File path = getActivity().getDir(QuickPreference.MAIN_DIRECTORY, Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
                String phone = sharedPreferences.getString(QuickPreference.PHONE, "00000000");
                File profilePic = new File(path, phone + ".jpg");
                FileOutputStream fos = new FileOutputStream(profilePic);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String appURL = getString(R.string.url) + "updateprofile";
            StringBuffer stringBuffer = new StringBuffer();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                Bitmap resized = ThumbnailUtils.extractThumbnail(bitmap, 80, 80);
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
                byte[] byteArray1 = stream1.toByteArray();
                String thumbnail = Base64.encodeToString(byteArray1, Base64.NO_WRAP);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String picture = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
                DateTime date = new DateTime();
                UpdateProfileStatus updateProfileStatus = new UpdateProfileStatus();
                updateProfileStatus.setPicture(picture);
                updateProfileStatus.setThumbnail(thumbnail);
                updateProfileStatus.setDeviceID(sharedPreferences.getString(QuickPreference.DEVICE_ID,"0000000"));
                updateProfileStatus.setPhone(sharedPreferences.getString(QuickPreference.PHONE,"00000000"));
                updateProfileStatus.setTimeZone(date.getZone().toString());

                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter updateProfile = new StringWriter();
                objectMapper.writeValue(updateProfile, updateProfileStatus);

                URL url = null;


                url = new URL(appURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type","application/json");
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(updateProfile.toString());
                writer.flush();
                String line;

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);

                }
                writer.close();
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
