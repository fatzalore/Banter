package com.example.Banter;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

public class BanterActivity extends Activity implements BanterMenuFragment.transactToRoomFragment {

    int currentFragment = -1;
    BanterMenuFragment banterMenuFragment;
    BanterRoomFragment banterRoomFragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    EditText dialogRoomName;
    EditText dialogPassword;
    EditText dialogRepeatPassword;
    EditText dialogAdminPassword;
    EditText dialogRepeatAdminPassword;
    ImageButton dialogYes;
    ImageButton dialogNo;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add:
                createNewRoomDialog();
                break;
            case R.id.search:
                toggleSearchField();
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
    /* Creates the pop up dialog to input information about new Banter Room */
    private void createNewRoomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.banter_new_room_dialog);
        dialog.setTitle("Create new banter room");
        dialogRoomName = (EditText) dialog.findViewById(R.id.new_room_dialog_room_name);
        dialogPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_password);
        dialogRepeatPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_repeat_password);
        dialogAdminPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_admin_password);
        dialogRepeatAdminPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_repeat_admin_password);
        dialogYes = (ImageButton) dialog.findViewById(R.id.new_room_dialog_yes);
        dialogNo = (ImageButton) dialog.findViewById(R.id.new_room_dialog_no);
        dialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO IMPLEMENT SAVING
            }
        });
        dialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    
    private void toggleSearchField(){
        EditText searchField = (EditText) banterMenuFragment.banterMenuFragment.findViewById(R.id.menu_search_field);
        if(searchField.getVisibility() == View.VISIBLE){
            searchField.setVisibility(View.GONE);
        } else {
            searchField.setVisibility(View.VISIBLE);
        }

    }
}
