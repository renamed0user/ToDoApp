package com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1)
public abstract class MyDataBase extends RoomDatabase {
    public abstract TaskDao taskDao();
}