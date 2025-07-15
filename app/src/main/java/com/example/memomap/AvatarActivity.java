package com.example.memomap;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AvatarActivity extends AppCompatActivity {

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    ImageView imgSkin, imgHair, imgFace, imgClothes;
    GridLayout optionsContainer;
    Button tabSkin, tabHair, tabFace, tabClothes;
    String currentTab = "skin";

    private void resetTabStyles(Button... buttons) {
        for (Button btn : buttons) {
            btn.setSelected(false);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // default size

            // Reset height and top margin
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btn.getLayoutParams();
            params.height = dpToPx(48); // default height
            params.topMargin = dpToPx(0);
            btn.setLayoutParams(params);
        }
    }

    private void setTabBehavior(Button selectedTab, String optionKey, Button... allTabs) {
        resetTabStyles(allTabs);
        selectedTab.setSelected(true);
        selectedTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Increase height and top margin
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) selectedTab.getLayoutParams();
        params.height = dpToPx(56);
        params.topMargin = dpToPx(-4);
        selectedTab.setLayoutParams(params);

        showOptions(optionKey);
    }

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

        tabSkin.setOnClickListener(v -> setTabBehavior(tabSkin, "skin", tabSkin, tabHair, tabFace, tabClothes));
        tabHair.setOnClickListener(v -> setTabBehavior(tabHair, "hair", tabSkin, tabHair, tabFace, tabClothes));
        tabFace.setOnClickListener(v -> setTabBehavior(tabFace, "face", tabSkin, tabHair, tabFace, tabClothes));
        tabClothes.setOnClickListener(v -> setTabBehavior(tabClothes, "clothes", tabSkin, tabHair, tabFace, tabClothes));

        showOptions("skin"); // default tab
    }

    private void showOptions(String type) {
        currentTab = type;
        optionsContainer.removeAllViews();

        int[] imageIds = getImageSetForType(type);

        for (int i = 0; i < imageIds.length; i++) {
            int id = imageIds[i];

            ImageView option = new ImageView(this);
            option.setImageResource(id);
            option.setScaleType(ImageView.ScaleType.FIT_CENTER);

            int sizeInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics()
            );

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = sizeInPx;
            params.height = sizeInPx;
            params.setMargins(16, 16, 16, 16);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Let GridLayout space evenly

            option.setLayoutParams(params);

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
            case "hair": return new int[]{R.drawable.hair1, R.drawable.hair2, R.drawable.hair3, R.drawable.hair4, R.drawable.hair5, R.drawable.hair6};
            case "face": return new int[]{R.drawable.face1, R.drawable.face2, R.drawable.face3, R.drawable.face4, R.drawable.face5, R.drawable.face6};
            case "clothes": return new int[]{R.drawable.cloth2, R.drawable.cloth3, R.drawable.cloth4, R.drawable.cloth5, R.drawable.cloth6};
            default: return new int[]{};
        }
    }

}
