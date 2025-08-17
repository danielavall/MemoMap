package com.example.memomap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetEmojiFragment extends BottomSheetDialogFragment {

    public interface EmojiSelectListener {
        void onEmojiSelected(int emojiResId);
    }

    private EmojiSelectListener listener;

    public void setEmojiSelectListener(EmojiSelectListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_emoji, container, false);

        view.findViewById(R.id.ivSad).setOnClickListener(v -> sendEmoji(R.drawable.emote_sad_green));
        view.findViewById(R.id.ivCry).setOnClickListener(v -> sendEmoji(R.drawable.ic_emote_cry_blue));
        view.findViewById(R.id.ivNeutral).setOnClickListener(v -> sendEmoji(R.drawable.ic_emote_neutral_orange));
        view.findViewById(R.id.ivHappy).setOnClickListener(v -> sendEmoji(R.drawable.ic_emote_happy_purple));
        view.findViewById(R.id.ivSmile).setOnClickListener(v -> sendEmoji(R.drawable.ic_emote_smile_pink));

        return view;
    }

    private void sendEmoji(int resId) {
        if (listener != null) {
            listener.onEmojiSelected(resId);
        }
        dismiss(); // Tutup pop-up
    }
}
