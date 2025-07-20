package com.example.memomap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_journal_card);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<JournalCardModel> journalList = new ArrayList<>();
        journalList.add(new JournalCardModel("WED", "01", "JAN", "Tahun Baru di rumah aja, bingung mau mulai dari mana", 0, 0, R.color.orange));
        journalList.add(new JournalCardModel("THU", "02", "JAN", "Mulai ngerapihin to-do list dan niat tahun ini", 1, 2, R.color.pink));
        journalList.add(new JournalCardModel("FRI", "03", "JAN", "Beresin file kuliah semester lalu sambil nostalgia", 2, 1, R.color.pink));
        journalList.add(new JournalCardModel("SAT", "04", "JAN", "Quality time bareng keluarga seharian penuh", 5, 0, R.color.purple));
        journalList.add(new JournalCardModel("SUN", "05", "JAN", "Overthinking soal kuliah yang bakal mulai lagi", 0, 3, R.color.blue));
        journalList.add(new JournalCardModel("MON", "06", "JAN", "Hari pertama kuliah, belum siap mental", 2, 6, R.color.blue));
        journalList.add(new JournalCardModel("TUE", "07", "JAN", "Ngoding sampai malam, stuck di layout XML", 2, 0, R.color.green));
        journalList.add(new JournalCardModel("WED", "08", "JAN", "Ketemu teman baru, mulai merasa lebih nyaman", 3, 0, R.color.purple));
        journalList.add(new JournalCardModel("THU", "09", "JAN", "Presentasi perdana semester ini, lumayan grogi", 1, 4, R.color.blue));
        journalList.add(new JournalCardModel("FRI", "10", "JAN", "Ngopi sore sambil ngerjain tugas kecil-kecilan", 3, 1, R.color.pink));
        journalList.add(new JournalCardModel("SAT", "11", "JAN", "Bantu mama belanja mingguan, lumayan capek", 4, 0, R.color.green));
        journalList.add(new JournalCardModel("SUN", "12", "JAN", "Tidur seharian, badan dan otak butuh recharge", 0, 0, R.color.green));
        journalList.add(new JournalCardModel("MON", "13", "JAN", "Mulai minggu dengan kelas pagi yang berat banget", 1, 3, R.color.blue));
        journalList.add(new JournalCardModel("TUE", "14", "JAN", "Ngoding Android bareng temen, seru tapi pusing", 2, 2, R.color.orange));
        journalList.add(new JournalCardModel("WED", "15", "JAN", "Kelas full dari pagi sampe sore, lumayan lelah", 1, 1, R.color.green));
        journalList.add(new JournalCardModel("THU", "16", "JAN", "Dosen kasih motivasi soal masa depan, semangat naik!", 4, 0, R.color.purple));
        journalList.add(new JournalCardModel("FRI", "17", "JAN", "Tugas mulai numpuk, tapi masih bisa santai dikit", 2, 2, R.color.orange));
        journalList.add(new JournalCardModel("SAT", "18", "JAN", "Nugas di kafe sambil dengerin musik jazz", 3, 0, R.color.pink));
        journalList.add(new JournalCardModel("SUN", "19", "JAN", "Kayaknya mulai butuh short escape deh", 1, 1, R.color.green));
        journalList.add(new JournalCardModel("MON", "20", "JAN", "Mulai minggu dengan pikiran berat soal deadline", 0, 4, R.color.blue));

        JournalCardAdapter adapter = new JournalCardAdapter(journalList);
        recyclerView.setAdapter(adapter);

        ImageButton btnCalendar = view.findViewById(R.id.btn_calendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
