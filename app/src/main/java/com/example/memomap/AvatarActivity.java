package com.example.memomap;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AvatarActivity extends AppCompatActivity {

    ImageView imgSkin, imgHair, imgFace, imgClothes;
    LinearLayout optionsContainer;
    Button tabSkin, tabHair, tabFace, tabClothes;
    String currentTab = "skin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar);

        imgSkin = findViewById(R.id.imgSkin);
        imgHair = findViewById(R.id.imgHair);
        imgFace = findViewById(R.id.imgFace);
        imgClothes = findViewById(R.id.imgClothes);

        optionsContainer = findViewById(R.id.optionsContainer);

        tabSkin = findViewById(R.id.tabSkin);
        tabHair = findViewById(R.id.tabHair);
        tabFace = findViewById(R.id.tabFace);
        tabClothes = findViewById(R.id.tabClothes);

        tabSkin.setOnClickListener(v -> showOptions("skin"));
        tabHair.setOnClickListener(v -> showOptions("hair"));
        tabFace.setOnClickListener(v -> showOptions("face"));
        tabClothes.setOnClickListener(v -> showOptions("clothes"));

        showOptions("skin"); // default tab
    }

    private void showOptions(String type) {
        currentTab = type;
        optionsContainer.removeAllViews();

        int[] imageIds = getImageSetForType(type);

        for (int id : imageIds) {
            ImageView option = new ImageView(this);
            option.setImageResource(id);
            option.setPadding(8, 8, 8, 8);
            option.setLayoutParams(new LinearLayout.LayoutParams(200, 200));

            option.setOnClickListener(v -> {
                switch (type) {
                    case "skin": imgSkin.setImageResource(id); break;
                    case "hair": imgHair.setImageResource(id); break;
                    case "face": imgFace.setImageResource(id); break;
                    case "clothes": imgClothes.setImageResource(id); break;
                }
            });

            optionsContainer.addView(option);
        }
    }

    private int[] getImageSetForType(String type) {
        switch (type) {
            case "skin": return new int[]{R.drawable.skin1, R.drawable.skin2, R.drawable.skin3, R.drawable.skin4, R.drawable.skin5, R.drawable.skin5};
            case "hair": return new int[]{R.drawable.hair2, R.drawable.hair3, R.drawable.hair4, R.drawable.hair5, R.drawable.hair6};
            case "face": return new int[]{R.drawable.face1, R.drawable.face2, R.drawable.face3, R.drawable.face4, R.drawable.face5, R.drawable.face6};
            case "clothes": return new int[]{R.drawable.cloth1, R.drawable.cloth2, R.drawable.cloth3, R.drawable.cloth4, R.drawable.cloth5, R.drawable.cloth6};
            default: return new int[]{};
        }
    }
}
