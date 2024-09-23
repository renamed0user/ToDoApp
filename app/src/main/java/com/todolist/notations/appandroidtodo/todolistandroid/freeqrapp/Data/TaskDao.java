package com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE task_name LIKE :first AND " +
            "task_desc LIKE :last LIMIT 1")
    Task findByName(String first, String last);

    @Update()
    void updateTask(Task task);
    @Insert
    void add(Task tasks);

    @Delete
    void delete(Task task);
}
