package com.example.memomap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SuggestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SuggestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuggestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuggestFragment newInstance(String param1, String param2) {
        SuggestFragment fragment = new SuggestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest, container, false);

        ImageView profileIcon = view.findViewById(R.id.profileIcon);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String avatarPath = prefs.getString("avatarPath", null);

        if (avatarPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
            if (bitmap != null) {
                profileIcon.setImageBitmap(bitmap);
            }
        } else {
            profileIcon.setImageResource(R.drawable.profile_icon); // default fallback
        }

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }
}