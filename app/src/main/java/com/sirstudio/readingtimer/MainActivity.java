package com.sirstudio.readingtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Variables
    int pagecount;
    int currentPage;
    boolean timerRunning = false;

    //Text fields
    EditText f_pageCount;
    EditText f_currentPage;

    //Buttons
    Button b_StartTimer;

    //Stats
    int pagesRead = 0;
    int totalSeconds = 0;
    TextView stat_onePage;
    TextView stat_pagesPer15;
    TextView stat_estTime;
    TextView stat_avgOver;


    //Timer
    long startTime = 0;
    int seconds;
    int minutes;
    int hours;


    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable()
    {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int t_seconds = (int) (millis / 1000);

            int t_minutes = t_seconds / 60;
            int t_hours = t_minutes / 60;
            t_seconds = t_seconds % 60;
            t_minutes = t_minutes % 60;

            minutes = t_minutes;
            seconds = t_seconds;
            hours = t_hours;

            timerHandler.postDelayed(this, 250);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize UI objects
        f_pageCount = findViewById(R.id.f_PageCount);
        f_currentPage = findViewById(R.id.f_CurPage);
        b_StartTimer = findViewById(R.id.b_StartTimer);
        stat_onePage = findViewById(R.id.stat_onePage);
        stat_pagesPer15 = findViewById(R.id.stat_pagesPer15);
        stat_estTime = findViewById(R.id.stat_estTime);
        stat_avgOver = findViewById(R.id.stat_avgOver);
    }

    public void StartTimer(View view)
    {
        if(timerRunning == false) //Check if the timer is running
        {
            if(f_pageCount.length() != 0 && f_currentPage.length() != 0) //Check if the text fields are empty or not
            {
                //Get strings from text fields and convert them to integers
                pagecount = Integer.parseInt(f_pageCount.getText().toString());
                currentPage = Integer.parseInt(f_currentPage.getText().toString());

                if(pagecount > 0) //Check if the values are more than 0
                {
                    b_StartTimer.setText(R.string.b_StopReading);

                    //Start the timer
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    timerRunning = true;

                    Toast.makeText(this, "Reading timer started! Hit this button again after reading 5 pages.", Toast.LENGTH_LONG).show();
                }
                else if(pagecount <= 0)
                {
                    f_pageCount.setError("Please enter a valid number");
                }
            }
            else
            {
                //Give error about empty text fields
                if(f_pageCount.length() == 0)
                {
                    f_pageCount.setError("This field cannot be empty!");
                }
                if(f_currentPage.length() == 0)
                {
                    f_currentPage.setError("This field cannot be empty!");
                }
            }
        }
        else //Timer is running, stop it
        {
            timerHandler.removeCallbacks(timerRunnable);
            timerRunning = false;
            b_StartTimer.setText(R.string.b_StartReading);

            //Calculate stats
            currentPage += 5;
            pagesRead += 5;
            totalSeconds += seconds + (minutes * 60) + (hours * 60);

            f_currentPage.setText(Integer.toString(currentPage));

            //Time per one page
            float avgPerPage = (float)totalSeconds / pagesRead;
            stat_onePage.setText("One page: " + Float.toString(avgPerPage) + "s");

            //Pages read per 15 minutes. 15 minutes = 900 seconds
            stat_pagesPer15.setText("Pages / 15 min: " + Float.toString(Math.round((900.00f / avgPerPage) * 10f) / 10f) + " pages");

            //Estimated time left
            double estSecondsLeft = (pagecount - currentPage) * (double)avgPerPage;
            estSecondsLeft = Math.round(estSecondsLeft);

            stat_estTime.setText("Estimated time left: " + calcTime(estSecondsLeft));

            //Average of - pages
            stat_avgOver.setText("Average of " + Integer.toString(pagesRead )+ " pages");
        }
    }

    private String calcTime(double seconds)
    {
        String result = "";
        int minutes = 0;
        int hours = 0;
        double tempSeconds = seconds;

        if (tempSeconds >= 60)
        {
            while (true)
            {
                if (tempSeconds >= 60)
                {
                    tempSeconds -= 60;
                    minutes++;
                }
                else
                {
                    break;
                }
            }
        }
        if (minutes >= 60)
        {
            while (true)
            {
                if (minutes >= 60)
                {
                    minutes -= 60;
                    hours++;
                }
                else
                {
                    break;
                }
            }
        }

        if(hours > 0)
        {
            result += hours + "h ";
        }
        if(minutes > 0)
        {
            result += minutes + "min ";
        }
        if(tempSeconds > 0)
        {
            result += Math.round(tempSeconds) + "s";
        }

        return result;
    }
}
