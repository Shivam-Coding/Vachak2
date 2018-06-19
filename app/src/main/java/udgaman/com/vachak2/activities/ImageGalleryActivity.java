package udgaman.com.vachak2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.adapters.GalleryGridAdapter;
import udgaman.com.vachak2.adapters.GridViewAdapter;
import udgaman.com.vachak2.model.GridViewItem;
import udgaman.com.vachak2.model.ImageItem;
import udgaman.com.vachak2.util.BitmapHelper;

public class ImageGalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final static String TAG = "IMAGE_GALLERY_ACTIVITY";
     public GridView gridView;
    private GridViewAdapter gridAdapter;

    public GalleryGridAdapter adapter;

    public List<GridViewItem> gridItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);



            setGridAdapter(Environment.getExternalStorageDirectory().getPath() + "/");



//        gridView = (GridView) findViewById(R.id.gridView);
//        gridAdapter = new GridViewAdapter(this, R.layout.image_gallery_grid_item, getData());
//        gridView.setAdapter(gridAdapter);











    }

    private ArrayList<ImageItem> getData() {
//        final ArrayList<ImageItem> imageItems = new ArrayList<>();
//        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
//        for (int i = 0; i < imgs.length(); i++) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
//            imageItems.add(new ImageItem(bitmap, "Image#" + i));
//        }
//        return imageItems;
        return null;
    }





    /**
     * This will create our GridViewItems and set the adapter
     *
     * @param path
     *            The directory in which to search for images
     */
     public void setGridAdapter(String path) {
        // Create a new grid adapter

        List<GridViewItem> items = new ArrayList<GridViewItem>();
         File[] files = new File(path).listFiles(new ImageFileFilter());
        for(int i=1; i< files.length; i++){
         items.add(new GridViewItem(null,false,null,false));
        }


        gridItems = items;
        adapter = new GalleryGridAdapter(this, gridItems);

        // Set the grid adapter
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

        // Set the onClickListener
        gridView.setOnItemClickListener(this);

        new PrepareImageGalleryThread().execute(path);

    }



    /**
     * Go through the specified directory, and create items to display in our
     * GridView
     */
     public List<GridViewItem> createGridItems(String directoryPath) {
        List<GridViewItem> items = new ArrayList<GridViewItem>();

        // List all the items within the folder.



            File[] files = new File(directoryPath).listFiles(new ImageFileFilter());
//            Toast.makeText(this,files.toString(),Toast.LENGTH_SHORT).show();
//            Toast.makeText(this,files.length+"  hrttttr",Toast.LENGTH_SHORT).show();


            for (File file : files) {


                // Add the directories containing images or sub-directories

//                Toast.makeText(this, file.toString() + "   hi", Toast.LENGTH_SHORT).show();
                try {
                    if (file.isDirectory()
                            && file.listFiles(new ImageFileFilter()).length != 0) {

//                        Toast.makeText(this, " in if  ", Toast.LENGTH_SHORT).show();

                        items.add(new GridViewItem(file.getAbsolutePath(), true, null,true));

                    }
                    // Add the images
                    else {

                        if (!file.isDirectory()) {

//                            Toast.makeText(this, " in else  ", Toast.LENGTH_SHORT).show();

                            Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
                                    100,
                                    100);
                            items.add(new GridViewItem(file.getAbsolutePath(), false, image,true));
                        }
                    }



                }catch (NullPointerException n){
                   n.printStackTrace();
                }
            }


        return items;
    }


    /**
     * Checks the file to see if it has a compatible extension.
     */
     public boolean isImageFile(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".png"))
        // Add other formats as desired
        {
            return true;
        }
        return false;
    }


    @Override
    public void
    onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (gridItems.get(position).isDirectory()) {

            try {
                setGridAdapter(gridItems.get(position).getPath());
            }catch (Exception e){
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            }

        }
        else {
            // Display the image

            Intent intent = new Intent(getApplicationContext(), ImageDetailActivity.class);
            intent.putExtra("title", gridItems.get(position).getPath());
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                Bitmap b = BitmapFactory.decodeFile(gridItems.get(position).getPath());
////                Bitmap bitmap = BitmapFactory.decodeFile(gridItems.get(position).getPath(), options);
//                intent.putExtra("image", b);

                //Start details activity
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    /**
     * This can be used to filter files.
     */
    public class ImageFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            else if (isImageFile(file.getAbsolutePath())) {
                return true;
            }
            return false;
        }
    }




    public class PrepareImageGalleryThread extends AsyncTask<String,Void,List<GridViewItem>>{
        @Override
        protected void onPostExecute(List<GridViewItem> aVoid) {
            super.onPostExecute(aVoid);
            adapter.setItems(gridItems);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected List<GridViewItem> doInBackground(String... strings) {


            gridItems = createGridItems(strings[0]);
//            adapter.setItems(createGridItems(strings[0]));
//            adapter.notifyDataSetChanged();

            return null;
        }
    }



}
