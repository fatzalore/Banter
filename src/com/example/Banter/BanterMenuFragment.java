package com.example.Banter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuFragment extends Fragment {

    View banterMenuFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setContentView(banterMenuFragment);
        setHasOptionsMenu(true);
        banterMenuFragment = inflater.inflate(R.layout.bantermenulayout,container,false);

        return banterMenuFragment;
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.bantermenuactionbar,menu);
    }
}
