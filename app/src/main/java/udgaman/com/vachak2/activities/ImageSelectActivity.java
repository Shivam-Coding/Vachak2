package udgaman.com.vachak2.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;

import udgaman.com.vachak2.fragments.ImageFragment;
import udgaman.com.vachak2.fragments.ImageSelectFragment;
import udgaman.com.vachak2.R;

public class ImageSelectActivity extends AppCompatActivity {

    CropImageView mCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        Uri uri = getIntent().getData();
        boolean pic = getIntent().getBooleanExtra("Pic",false);
        if (findViewById(R.id.image_select_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }


            if(pic){
                ImageFragment imageFragment = new ImageFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("image", uri);
                bundle.putBoolean("Pic",true);
                imageFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.image_select_fragment_container, imageFragment).commit();
            }else {
                ImageSelectFragment imageSelectFragment = new ImageSelectFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("image", uri);
                imageSelectFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.image_select_fragment_container, imageSelectFragment).commit();
            }
        }


    }






}
