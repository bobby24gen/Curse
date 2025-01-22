package com.example.curse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.curse.models.Jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobsTakeActivity extends AppCompatActivity {

    EditText editText_title, editText_description, editText_cost;
    ImageView imageView_save;
    Jobs jobs;
    boolean isOldJob = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jobs_take);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText_title = findViewById(R.id.editText_title);
        editText_description = findViewById(R.id.editText_description);
        editText_cost = findViewById(R.id.editText_count);

        imageView_save = findViewById(R.id.imageView_save);

        jobs = new Jobs();
        try {
            jobs = (Jobs) getIntent().getSerializableExtra("choice_job");
            editText_title.setText(jobs.getTitle());
            editText_description.setText(jobs.getDescription());
            editText_cost.setText(jobs.getCost());
            isOldJob = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editText_title.getText().toString();
                String description = editText_description.getText().toString();
                String cost = editText_cost.getText().toString();

                if(title.isEmpty()) {
                    Toast.makeText(JobsTakeActivity.this, "Add a title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(description.isEmpty()) {
                    Toast.makeText(JobsTakeActivity.this, "Add a description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cost.isEmpty()) {
                    Toast.makeText(JobsTakeActivity.this, "Add a payment", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
                Date date = new Date();

                if(!isOldJob){
                    jobs = new Jobs();
                }

                jobs.setTitle(title);
                jobs.setDescription(description);
                jobs.setCost(cost);
                jobs.setDate(formatter.format(date));

                Intent intent = new Intent();
                intent.putExtra("jobs", jobs);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });
    }
}