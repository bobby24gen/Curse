package com.example.curse.dataBase;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.example.curse.models.Jobs;

@Dao
public interface MainDAO {

    @Insert (onConflict = REPLACE)
    void insert (Jobs jobs);

    @Delete
    void delete (Jobs jobs);

    @Query("SELECT * FROM jobs ORDER BY id DESC")
    List<Jobs> getAll();

    @Query("UPDATE jobs SET title = :title, description = :description, cost = :cost WHERE ID = :id")
    void update (int id, String title, String description, String cost);

    @Query("UPDATE jobs SET favorite = :favor WHERE ID = :id")
    void favor (int id, boolean favor);
}
