package com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.room.Room;

import com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.Data.MyDataBase;
import com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.Data.Task;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    Button add;
    LinearLayout layout;
    MyDataBase dataBase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add=findViewById(R.id.add);
        layout=findViewById(R.id.container);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                buildDialog();
            }
        });
        synchronized (MyDataBase.class) {
            if (dataBase == null) {
                dataBase = Room.databaseBuilder(this,
                                MyDataBase.class, "task")
                        .allowMainThreadQueries()
                        .build();

                    if (!dataBase.taskDao().getAll().isEmpty()) {
                        dataBase.taskDao().getAll().forEach(task -> {
                            addCard(task.name, task.desc, task.date,true, task.completed);
                        });
                    }

            }

        }
    }



    @SuppressLint("MissingInflatedId")
    public void buildDialog(){
        Context context = this;
        AlertDialog dialog;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.my_dialog, null);
        EditText editText= view.findViewById(R.id.nameDesc);

        CheckBox checkDesc = view.findViewById(R.id.checkBoxDesc);
        checkDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editText.setVisibility(View.VISIBLE);
                }
                else {
                    editText.setVisibility(View.INVISIBLE);
                }
            }
        });
        final EditText name= view.findViewById(R.id.nameEdit);
        final EditText desc= view.findViewById(R.id.nameDesc);
        builder.setView(view);
        builder.setTitle("Enter your Task")
                .setPositiveButton("SAVE",null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name.setText("");
                        editText.setText("");
                        checkDesc.setChecked(false);
                    }
                });

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (Objects.equals(name.getText().toString(), "")) {
                Toast.makeText(context, context.getString(R.string.task_name_can_t_be_empty), Toast.LENGTH_SHORT).show();
            } else {

                String date = Calendar.getInstance().getTime().toString();
                Task newTask = new Task();
                newTask.name=name.getText().toString();
                newTask.desc=desc.getText().toString();
                newTask.date=date;
                newTask.completed=false;
                addTaskInBack(newTask);
                addCard(name.getText().toString(), desc.getText().toString(),date,false,false);
                name.setText("");
                editText.setText("");
                checkDesc.setChecked(false);
                dialog.dismiss();
            }
        });
    }


    public void addTaskInBack(Task task){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try{
                dataBase.taskDao().add(task);
                }
                catch (Exception e){
                    Log.i("db error",e.getMessage());
                }
            }
        });
    }
    public void deleteTaskInBack(String name, String desc){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Task t = dataBase.taskDao().findByName(name,desc);
                    dataBase.taskDao().delete(t);
                }
                catch (Exception e){
                    Log.i("db error",e.getMessage());
                }
            }
        });
    }


    @SuppressLint("MissingInflatedId")
    private void addCard(String name, String desc, String savedDate,Boolean saved ,Boolean compleated){
        final View view = getLayoutInflater().inflate(R.layout.task,null);
        TextView nameView = view.findViewById(R.id.task_name);
        TextView descView = view.findViewById(R.id.task_desc);
        Button delete = view.findViewById(R.id.delete);
        CheckBox checkBox= view.findViewById(R.id.task_check_box);
        Context context=this;
        TextView date= view.findViewById(R.id.task_date);

        date.setText(savedDate);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Task task = dataBase.taskDao().findByName(nameView.getText().toString(),descView.getText().toString());
                if(b){
                    view.findViewById(R.id.layout_task).setBackgroundColor(getColor(R.color.shadow));
                    nameView.setPaintFlags(nameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    task.completed=true;
                    dataBase.taskDao().updateTask(task);
                }
                else {
                    view.findViewById(R.id.layout_task).setBackgroundColor(Color.WHITE);
                    nameView.setPaintFlags(nameView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    task.completed = false;
                    dataBase.taskDao().updateTask(task);


                }
            }
        });

        nameView.setText(name);
        descView.setText(desc);

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View viewDialog = getLayoutInflater().inflate(R.layout.edit_dialog, null);
                EditText editName = viewDialog.findViewById(R.id.savedNameEdit);
                EditText editDesc = viewDialog.findViewById(R.id.savedDescEdit);
                TextView editDate = viewDialog.findViewById(R.id.savedDate);
                editDate.setText(date.getText());
                editName.setText(nameView.getText());
                editDesc.setText(descView.getText());

                builder.setView(viewDialog);
                builder.setTitle("Edit your Task")
                        .setPositiveButton("SAVE",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick (DialogInterface dialog,int which){
                                Task task = dataBase.taskDao().findByName(nameView.getText().toString(),descView.getText().toString());
                                String _date=Calendar.getInstance().getTime().toString();
                                task.name=editName.getText().toString();
                                task.desc=editDesc.getText().toString();
                                task.date=_date;
                                dataBase.taskDao().updateTask(task);
                                nameView.setText(editName.getText());
                                descView.setText(editDesc.getText());
                                date.setText(_date);
                                Toast.makeText(context, getString(R.string.task_is_edited),Toast.LENGTH_SHORT).show();}

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                alertDialog = builder.create();
                alertDialog.show();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                layout.removeView(view);
                                deleteTaskInBack(nameView.getText().toString(),descView.getText().toString());
                                Toast.makeText(context, getString(R.string.task_is_deleted),Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create().show();
            }
        });

        if(saved){
            if(compleated){
                checkBox.setChecked(true);
            }
        }
        layout.addView(view);
        if(!saved)Toast.makeText(context, getString(R.string.task_is_added),Toast.LENGTH_SHORT).show();
    }

}