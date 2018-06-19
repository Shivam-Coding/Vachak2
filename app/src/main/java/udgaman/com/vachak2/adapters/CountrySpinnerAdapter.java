package udgaman.com.vachak2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import udgaman.com.vachak2.R;
import udgaman.com.vachak2.model.Countries;

/**
 * Created by shivamawasthi on 8/14/16.
 */

public class CountrySpinnerAdapter extends BaseAdapter {


    LayoutInflater layoutInflater ;
    Countries model;
    String[][] countries;
    Context context;

    public CountrySpinnerAdapter(Context context){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        model = new Countries();
        countries = model.getCountries();
        this.context = context;

    }



    @Override
    public int getCount() {
        return countries.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.country_spinner_item_row,viewGroup,false);
        TextView country = (TextView) view.findViewById(R.id.country);
        TextView code1 = (TextView) view.findViewById(R.id.country_code);
        ImageView flag = (ImageView) view.findViewById(R.id.country_flag);
        country.setText(countries[i][0]);
        code1.setText("(+"+countries[i][3]+")");
        String code = countries[i][1].toLowerCase();
        int resID = context.getResources().getIdentifier(code, "drawable", context.getPackageName());
        flag.setImageResource(resID);
        return view;
    }
}
