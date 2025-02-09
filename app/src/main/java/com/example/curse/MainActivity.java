package com.example.curse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.curse.adapter.JobsListAdapter;
import com.example.curse.dataBase.RoomDB;
import com.example.curse.models.Jobs;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    int recyclerSpanCount = 1;
    boolean favoriteButtonSet = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    JobsListAdapter jobsListAdapter;
    RoomDB database;
    List<Jobs> jobs = new ArrayList<>();
    SearchView searchViewHome;
    Jobs selectedJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_home);
        searchViewHome = findViewById(R.id.searchView_home);

        database = RoomDB.getInstance(this);
        jobs = database.mainDAO().getAll();

        updateRecycler(jobs);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigationView_menu);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.favorite_btn) {
                    favoriteButtonSet = !favoriteButtonSet;

                    filter("");

                    drawerLayout.close();
                    return true;

                } else if (itemId == R.id.itemCount_btn) {
                    if (recyclerSpanCount == 1)
                        recyclerSpanCount = 2;
                    else
                        recyclerSpanCount = 1;
                    updateRecyclerSpanCount();

                    drawerLayout.close();
                    return true;

                } else if (itemId == R.id.itemNewJob_btn) {
                    Intent intent = new Intent(MainActivity.this, JobsTakeActivity.class);
                    startActivityForResult(intent, 101);

                    drawerLayout.close();
                    return true;
                }
                return false;
            }
        });

        searchViewHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter (newText);
                return true;
            }
        });

    }

    private void filter(String newText) {
        List<Jobs> filteredList = new ArrayList<>();
        for (Jobs singleJob : jobs) {
            if (favoriteButtonSet) {
                if ((singleJob.getTitle().toLowerCase().contains(newText.toLowerCase())
                        || singleJob.getDescription().toLowerCase().contains(newText.toLowerCase())
                        || singleJob.getCost().toLowerCase().contains(newText.toLowerCase())) && singleJob.getFavorite()) {
                    filteredList.add(singleJob);
                }
            } else {
                if (singleJob.getTitle().toLowerCase().contains(newText.toLowerCase())
                        || singleJob.getDescription().toLowerCase().contains(newText.toLowerCase())
                        || singleJob.getCost().toLowerCase().contains(newText.toLowerCase())) {
                    filteredList.add(singleJob);
                }
            }
        }
        jobsListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Jobs new_jobs = (Jobs) data.getSerializableExtra("jobs");
                database.mainDAO().insert(new_jobs);
                jobs.clear();
                jobs.addAll(database.mainDAO().getAll());
                jobsListAdapter.notifyDataSetChanged();
                filter("");
            }
        }
        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Jobs new_jobs = (Jobs) data.getSerializableExtra("jobs");
                database.mainDAO().update(new_jobs.getID(), new_jobs.getTitle(), new_jobs.getDescription(),
                        new_jobs.getCost());
                jobs.clear();
                jobs.addAll(database.mainDAO().getAll());
                jobsListAdapter.notifyDataSetChanged();
                filter("");
            }
        }
    }

    private void updateRecycler(List<Jobs> jobs) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(recyclerSpanCount, LinearLayoutManager.VERTICAL));
        jobsListAdapter = new JobsListAdapter(MainActivity.this, jobs ,jobsClickListener);
        recyclerView.setAdapter(jobsListAdapter);

    }

    private void updateRecyclerSpanCount() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(recyclerSpanCount, LinearLayoutManager.VERTICAL));
    }

    private  final JobsClickListener jobsClickListener = new JobsClickListener() {
        @Override
        public void OnClick(Jobs jobs) {
            Intent intent = new Intent(MainActivity.this, JobsTakeActivity.class);
            intent.putExtra("choice_job", jobs);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Jobs jobs, CardView cardView) {
            selectedJob = new Jobs();
            selectedJob = jobs;
            showPopupMenu(cardView);
        }
    };

    private void showPopupMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.pin_btn) {
            if (selectedJob.getFavorite()) {
                database.mainDAO().favor(selectedJob.getID(), false);
                Toast.makeText(MainActivity.this, "Delete from favorites", Toast.LENGTH_SHORT).show();
            } else {
                database.mainDAO().favor(selectedJob.getID(), true);
                Toast.makeText(MainActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
            jobs.clear();
            jobs.addAll(database.mainDAO().getAll());
            jobsListAdapter.notifyDataSetChanged();
            filter("");
            return true;
        } else if (itemId == R.id.delete_btn) {
            Toast.makeText(MainActivity.this, "The application was deleted", Toast.LENGTH_SHORT).show();
            database.mainDAO().delete(selectedJob);
            jobs.remove(selectedJob);
            jobsListAdapter.notifyDataSetChanged();
            filter("");
            return true;
        }
        return false;
    }
}