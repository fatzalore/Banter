package com.example.Banter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterRoomFragment extends Fragment {

    View banterRoomFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setContentView(R.layout.banterroomlayout);
        banterRoomFragment = inflater.inflate(R.layout.banterroomlayout,container,false);



        return banterRoomFragment;
    }
}
