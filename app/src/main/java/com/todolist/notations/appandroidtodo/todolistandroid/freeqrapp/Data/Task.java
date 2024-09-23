package com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "task_name")
    public String name;

    @ColumnInfo(name = "task_desc")
    public String desc;

    @ColumnInfo(name = "task_date")
    public String date;

    @ColumnInfo(name = "task_completed")
    public Boolean completed;
}
