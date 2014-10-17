package com.example.Banter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BanterActivity extends Activity implements BanterMenuFragment.transactToRoomFragment {

    int currentFragment = -1;
    BanterMenuFragment banterMenuFragment;
    BanterRoomFragment banterRoomFragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();

        banterMenuFragment = new BanterMenuFragment();
        banterRoomFragment = new BanterRoomFragment();
        transact(banterMenuFragment);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add:
                break;
            case R.id.search:
                break;
            case R.id.options:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
    /* Transaction between fragments */
    public void transact(Fragment to) {
        currentFragment++;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, to);
        fragmentTransaction.addToBackStack("MenuFragment");
        fragmentTransaction.commit();
    }

    public void transactToRoomFragment() {
        transact(banterRoomFragment);
    }
    @Override
    public void onBackPressed() {
        currentFragment--;
        if(currentFragment < 0){
            finish();
        }
        super.onBackPressed();
    }
}
