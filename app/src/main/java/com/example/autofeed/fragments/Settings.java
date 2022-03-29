package com.example.autofeed.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.autofeed.activities.PetProfile;
import com.example.autofeed.R;
import com.example.autofeed.activities.UserInfo;

public class Settings extends Fragment {
    private CardView card1, card2; // define cardview variables

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        card1 = view.findViewById(R.id.cv1);// linking the UI to the code
        card2 = view.findViewById(R.id.cv2);//

        card1.setOnClickListener(v -> startActivity(new Intent(getActivity(), PetProfile.class))); // if confirmBtn was pressed go to PetProfile activity
        card2.setOnClickListener(v -> startActivity(new Intent(getActivity(), UserInfo.class)));   // if confirmBtn was pressed go to UserInfo activity

        return view;
    }
}
