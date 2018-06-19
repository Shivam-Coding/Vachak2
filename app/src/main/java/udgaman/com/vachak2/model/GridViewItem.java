package udgaman.com.vachak2.model;

import android.graphics.Bitmap;

/**
 * Created by shivamawasthi on 9/4/16.
 */

public class GridViewItem {

    private String path;
    private boolean isDirectory;
    private Bitmap image;
    private boolean value;

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public GridViewItem(String path, boolean isDirectory, Bitmap image, boolean value) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.image = image;
        this.value = value;
    }


    public String getPath() {
        return path;
    }


    public boolean isDirectory() {
        return isDirectory;
    }


    public Bitmap getImage() {
        return image;
    }


}
