package udgaman.com.vachak2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.model.GridViewItem;

/**
 * Created by shivamawasthi on 9/4/16.
 */

public class GalleryGridAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<GridViewItem> items;


    public GalleryGridAdapter(Context context, List<GridViewItem> items) {
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(List<GridViewItem> items){
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gallery_grid_item, null);
        }

        if(items.get(position).isValue()) {

            TextView text = (TextView) convertView.findViewById(R.id.textView);
            text.setText(items.get(position).getPath());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            Bitmap image = items.get(position).getImage();

            if (image != null) {
                imageView.setImageBitmap(image);
            } else {
                // If no image is provided, display a folder icon.
                imageView.setImageResource(R.drawable.in);
            }
        }

        return convertView;
    }

}
