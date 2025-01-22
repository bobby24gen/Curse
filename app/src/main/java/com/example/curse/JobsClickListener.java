package com.example.curse;

import androidx.cardview.widget.CardView;

import com.example.curse.models.Jobs;

public interface JobsClickListener {

    void OnClick (Jobs jobs);
    void onLongClick (Jobs jobs, CardView cardView);

}
