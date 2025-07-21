package com.example.memomap;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
// import android.os.Handler; // TIDAK PERLU lagi
// import android.os.Looper; // TIDAK PERLU lagi
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddGoalDialogFragment extends DialogFragment {

    private EditText inputGoal;
    private RadioGroup radioGroupType;
    private FloatingActionButton sendGoalButton;

    public interface AddGoalDialogListener {
        void onGoalAdded(String goalText, String type);
    }

    private AddGoalDialogListener listener;

    public void setAddGoalDialogListener(AddGoalDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogSlideFromBottom);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setGravity(Gravity.BOTTOM);
            // Ini memastikan dialog menyesuaikan diri saat keyboard muncul (ketika user mengetuk EditText)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return inflater.inflate(R.layout.add_new_goal, container, false); // Pastikan nama layoutnya benar
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputGoal = view.findViewById(R.id.input_goal);
        radioGroupType = view.findViewById(R.id.radio_group_type);
        sendGoalButton = view.findViewById(R.id.send_goal_button);

        // --- Perubahan di SINI ---
        // Hapus atau komentari baris yang memberikan fokus dan menampilkan keyboard otomatis:
        // inputGoal.requestFocus(); // Hapus baris ini
        // if (getDialog() != null && getDialog().getWindow() != null) {
        //     getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // Hapus baris ini
        // }
        // Hapus juga Handler.postDelayed block yang sebelumnya kita tambahkan untuk fokus otomatis.
        // --- Akhir Perubahan ---

        // Simpan hint asli dari string resource
        final String originalHint = getResources().getString(R.string.input_text_here);

        // OnFocusChangeListener akan mengatur hint saat user mengetuk EditText
        inputGoal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputGoal.setHint(""); // Hapus hint saat EditText mendapatkan fokus
                    // Opsional: Tampilkan keyboard secara manual saat fokus, jika SOFT_INPUT_STATE_VISIBLE tidak diatur global
                    // InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    // if (imm != null) {
                    //    imm.showSoftInput(inputGoal, InputMethodManager.SHOW_IMPLICIT);
                    // }
                } else {
                    // Kembalikan hint jika EditText kehilangan fokus DAN teksnya kosong
                    if (inputGoal.getText().toString().isEmpty()) {
                        inputGoal.setHint(originalHint);
                    }
                }
            }
        });


        sendGoalButton.setOnClickListener(v -> {
            String goalText = inputGoal.getText().toString().trim();
            if (!goalText.isEmpty()) {
                int selectedRadioButtonId = radioGroupType.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
                String type = "Unknown";
                if (selectedRadioButton != null) {
                    type = selectedRadioButton.getText().toString();
                }

                if (listener != null) {
                    listener.onGoalAdded(goalText, type);
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please input Goal or Task!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(width, height);
        }
    }

    private void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        hideKeyboard();
    }
}