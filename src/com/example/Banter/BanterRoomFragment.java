package com.example.Banter;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterRoomFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    View banterRoomFragment;

    ListView banterRoomList;
    ImageButton cameraButton;
    ImageButton attachImageButton;
    ImageButton smileyButton;
    ImageButton submitPostButton;
    EditText newPostText;

    BanterRoomListAdapter banterRoomListAdapter;

    BanterPost currentPost; // This is the post that is currently written by user. When user press submit, the info (image etc.) will be retrieved from this obj.
    //BanterRoom currentRoom; // not implemented yet

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        getActivity().setContentView(R.layout.banter_room_layout);
        banterRoomFragment = inflater.inflate(R.layout.banter_room_layout,container,false);
        banterRoomList = (ListView) banterRoomFragment.findViewById(R.id.room_chat_list);

        currentPost = new BanterPost();

        /* get the views from layout */
        cameraButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_camera_button);
        attachImageButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_image_button);
        smileyButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_smiley_button);
        submitPostButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_submit_button);
        newPostText = (EditText) banterRoomFragment.findViewById(R.id.room_post_text);

        addCameraListener();
        //addAttachImageListener();
        //addSmileyListener();
        addSubmitPostListener();


        /* TEST DATA! */
        ArrayList<BanterPost> testData = new ArrayList<BanterPost>();
        BanterPost testPost1 = new BanterPost();
        testPost1.setName("Ali baba");
        testPost1.setLikes(9);
        testPost1.setText("Lorem ipsum .... blablablabla Banter i massevis");
        testPost1.setTimeAndDateToNow();
        BanterPost testPost2 = new BanterPost();
        testPost2.setName("Venn av ali baba");
        testPost2.setLikes(-11);
        testPost2.setTimeAndDateToNow();
        testPost2.setText("Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis");
        testData.add(testPost1);
        testData.add(testPost2);
        /* TEST DATA end*/

        banterRoomListAdapter = new BanterRoomListAdapter(getActivity().getBaseContext(), testData);
        banterRoomList.setAdapter(banterRoomListAdapter);
        /* TEST DATA end */

        return banterRoomFragment;
    }

    private void addCameraListener() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); // 100 = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
            }
        });
    }

    private void addSubmitPostListener() {
        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: implement
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Image captured
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                /* store in currentPost */
                currentPost.setImage(thumbnail);

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.banter_room_actionbar,menu);
    }
}
