package com.example.homeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button RightButton;
    Button LeftButton;
    Chronometer chronometer;
    TextView timeSecond;
    ListView list;

    long pausedTime;
    int round = 1;

    ListView dialogList;

    ArrayList<String> list_of_items = new ArrayList<String>();
    ArrayList<String> lines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RightButton = findViewById(R.id.startNow);
        LeftButton = findViewById(R.id.restore);
        chronometer = findViewById(R.id.mainTime);
        timeSecond = findViewById(R.id.secondaryTime);
        list = findViewById(R.id.list);
    }


    public void rightButtonFunc(View view) {
        if (RightButton.getText().equals("Start")){
            //timeSecond.setText("YES");
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            RightButton.setText("Stop");
            LeftButton.setText("Kolo");

        } else if (RightButton.getText().equals("Stop")){
            //timeSecond.setText("NO");
            chronometer.stop();
            pausedTime = SystemClock.elapsedRealtime() - chronometer.getBase();     //uloží čas
            RightButton.setText("Pokračovat");
            LeftButton.setText("Obnovit");

        } else if (RightButton.getText().equals("Pokračovat")){
            //timeSecond.setText("MAYBE");
            chronometer.setBase(SystemClock.elapsedRealtime() - pausedTime);        //vezme uložený čas jako nový základ
            chronometer.start();
            RightButton.setText("Stop");
            LeftButton.setText("Kolo");
        }
    }

    public void leftButtonFunc(View view) {
        if (LeftButton.getText().equals("Obnovit")){
            //timeSecond.setText("YES");
            chronometer.setBase(SystemClock.elapsedRealtime());                     //nastavý původní čas (vynuluje)
            timeSecond.setText("00:00:00");
            RightButton.setText("Start");
            //---
            list_of_items.clear();
            list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_items));
            round = 1;

        } else if (LeftButton.getText().equals("Kolo")){
            timeSecond.setText(chronometer.getText().toString());

            String kolo = String.valueOf(round);
            String cas = chronometer.getText().toString();

            list_of_items.add(kolo+". | "+cas);

            list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_items));

            round += 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navrh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.saveItems){
            if (!list_of_items.isEmpty()){

                for (int i = 0; i<list_of_items.size(); i++) {
                    String itemFromList = list_of_items.get(i);

                    try {
                        OutputStream os = openFileOutput("data.txt", MODE_PRIVATE);      //samotné položky
                        OutputStreamWriter osw = new OutputStreamWriter(os);
                        osw.write(itemFromList+"\n");
                        osw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(this, "Uloženo", Toast.LENGTH_LONG).show();
                timeSecond.setText("");
            }else{
                Toast.makeText(this, "Nelze nic uložit", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.showItems){
            try {
                InputStream is = openFileInput("data.txt");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                String line = "";
                String nextLine;
                while ((nextLine = br.readLine()) != null) {
                    line = nextLine;
                }

                if (!line.equals("")){
                    timeSecond.setText("Poslední zaznamenané kolo "+line);
                } else {
                    timeSecond.setText("Žádné záznamy");
                }

            } catch (IOException e) {
                timeSecond.setText("Žádné záznamy");
            }
        }

        if (item.getItemId() == R.id.deleteItems){
            try {
                OutputStream os = openFileOutput("data.txt", MODE_PRIVATE);      //samotné položky
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write("");
                osw.close();
                Toast.makeText(this, "Smazáno", Toast.LENGTH_LONG).show();

                if (!timeSecond.getText().toString().equals("00:00:00")){
                    timeSecond.setText("Záznam smazán");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}