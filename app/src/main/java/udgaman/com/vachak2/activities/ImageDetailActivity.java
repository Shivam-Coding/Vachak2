package udgaman.com.vachak2.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import udgaman.com.vachak2.R;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        String title = getIntent().getStringExtra("title");
//        Bitmap bitmap = getIntent().getParcelableExtra("image");

        TextView titleTextView = (TextView) findViewById(R.id.image_detail_title);
        titleTextView.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.image_detail_image);
//        imageView.setImageBitmap(bitmap);

        imageView.setImageDrawable(Drawable.createFromPath(title.toString()));
    }
}
