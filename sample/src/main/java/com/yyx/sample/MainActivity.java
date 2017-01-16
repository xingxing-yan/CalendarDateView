package com.yyx.sample;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.yyx.library.CalendarDateView;
import com.yyx.library.WeekDayView;

public class MainActivity extends AppCompatActivity {
    private WeekDayView mWdv;
    private CalendarDateView mCdv;
    private TextView mTvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        otherLogic();
        setListener();
    }

    private void initView() {
        mWdv = (WeekDayView) findViewById(R.id.main_wdv);
        mCdv = (CalendarDateView) findViewById(R.id.main_cdv);
        mTvDate = (TextView) findViewById(R.id.main_date);


    }

    private void otherLogic() {
        mCdv.setDateTextView(mTvDate);
    }

    private void setListener() {
        mCdv.setOnItemClickListener(new CalendarDateView.OnItemClickListener() {
            @Override
            public void onItemClick(int year, int month, int day) {
                String date = year + "-" + (month+1) + "-" + day;
                Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
