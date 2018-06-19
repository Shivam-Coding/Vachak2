package udgaman.com.vachak2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import udgaman.com.vachak2.DAO.DatabaseConnection;
import udgaman.com.vachak2.DAO.VachakDatabase;
import udgaman.com.vachak2.R;
import udgaman.com.vachak2.adapters.DrawerAdapter;
import udgaman.com.vachak2.adapters.FriendsAdapter;
import udgaman.com.vachak2.model.QuickPreference;
import udgaman.com.vachak2.model.User;
import udgaman.com.vachak2.util.ContentListner;

public class FriendsActivity extends AppCompatActivity {

    final static String TAG = "FRIEND";
    SQLiteDatabase sqLiteDatabase;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mPlanetTitles;
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Intent intent = getIntent();
       sqLiteDatabase = DatabaseConnection.getConnection(this);
        String[] projection = {
                VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME,
                VachakDatabase.Friends.COLUMN_NAME_LAST_NAME,
                VachakDatabase.Friends.COLUMN_NAME_PHONE,
                VachakDatabase.Friends.COLUMN_NAME_STATUS,
                VachakDatabase.Friends.COLUMN_NAME_IMAGE
        };


        String sortOrder =
                VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME + " DESC";

        Cursor c = sqLiteDatabase.query(
                VachakDatabase.Friends.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        List<User> users = new ArrayList<User>();
        while(c.moveToNext()){
            User u = new User();
            u.setFirstName(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_FIRST_NAME)));
            u.setLastName(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_LAST_NAME)));
            u.setPhone(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_PHONE)));
            u.setStatus(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_STATUS)));
            u.setThumbnail(c.getString(c.getColumnIndex(VachakDatabase.Friends.COLUMN_NAME_IMAGE)));
            users.add(u);
        }
        sqLiteDatabase.close();

        FriendsAdapter friendsAdapter = new FriendsAdapter(this, users);
        ListView listView = (ListView)findViewById(R.id.friends_listView);
        listView.setAdapter(friendsAdapter);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerAdapter(this));



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.mipmap.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
               // getActionBar().setTitle("geribfweu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
              //  getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }



                return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    Intent intent = new Intent(this,ImageSelectActivity.class);
                    intent.setData(selectedImage);
                    intent.putExtra("Pic",false);
                    startActivity(intent);


                }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDrawerList.setAdapter(new DrawerAdapter(this));
    }

    protected class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position){
            case 0:{
                File path = getDir(QuickPreference.MAIN_DIRECTORY, Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences = getSharedPreferences(QuickPreference.REGISTER_PREFERENCE, MODE_PRIVATE);
                String phone = sharedPreferences.getString(QuickPreference.PHONE,"00000000");
                File profilePic  = new File(path,phone+".jpg");
                if(profilePic.exists()){
                    Uri selectedImage = Uri.fromFile(profilePic);
                    Intent intent = new Intent(this,ImageSelectActivity.class);
                    intent.setData(selectedImage);
                    intent.putExtra("Pic",true);
                    startActivity(intent);

                }else{

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
                break;
            }
            case 1:{
                Intent intent = new Intent(this,StatusUpdate.class);
                startActivity(intent);
                break;
            }
        }
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

}
