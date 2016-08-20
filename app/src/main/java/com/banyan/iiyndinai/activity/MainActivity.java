package com.banyan.iiyndinai.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.banyan.iiyndinai.R;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    // CART
    RelativeLayout notificationCount1;
    TextView tv;
    int i = 0;
    String str_count, str_name;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_name = user.get(SessionManager.KEY_USER);


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", str_name); // username

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item1 = menu.findItem(R.id.actionbar_cart);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item1);
        tv = (TextView) notificationCount1.findViewById(R.id.badge_notification_2);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        MenuItemCompat.setActionView(item, R.layout.notification_update_count_layout);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item);
        tv = (TextView) notificationCount1.findViewById(R.id.badge_notification_2);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.actionbar_cart) {


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        Intent in = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new HotDealsFragment();
                title = getString(R.string.title_hotdeals);
                break;
            case 2:
                fragment = new EnquiryFragment();
                title = getString(R.string.title_enquiry);
                break;
            case 3:
                fragment = new TrackOrderFragment();
                title = getString(R.string.title_track);
                break;

            case 4:
                try {
                    session.logoutUser();
                } catch (Exception e) {

                }
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}