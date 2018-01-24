package com.alamkanak.weekview.sample;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.sample.apiclient.MyJsonService;
import com.alamkanak.weekview.sample.apiclient.Schedule;
import com.alamkanak.weekview.sample.apiclient.ScheduleResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScheduleActivity extends AppCompatActivity implements MonthLoader.MonthChangeListener, Callback<ScheduleResponse> {

    List<WeekViewEvent> events = new ArrayList<>();
    ArrayList<Schedule> listFree = new ArrayList<>();
    HashMap<Long, Schedule> mapSchedule = new HashMap<Long, Schedule>();
    private WeekView weekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.alamkanak.weekview.sample.R.layout.activity_base);
        weekView = (WeekView) findViewById(R.id.weekView);
        weekView.setMonthChangeListener(this);

        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("http://dev.elife.vietsens.vn:9191/binbackend_develop")
                .build();
        MyJsonService service = retrofit.create(MyJsonService.class);
        service.listSchedule(this);
        weekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                Schedule schedule = mapSchedule.get(event.getStartTime().getTimeInMillis());
                if (schedule != null && schedule.getType() == 1) {
                    mapSchedule.remove(event.getStartTime().getTimeInMillis());
                    events.remove(event);
                    weekView.notifyDatasetChanged();
                }
            }
        });
        weekView.setEmptyViewClickListener(new WeekView.EmptyViewClickListener() {
            @Override
            public void onEmptyViewClicked(Calendar time) {
                int minute = time.get(Calendar.MINUTE);
                if (minute >= 0 && minute < 15) time.set(Calendar.MINUTE, 0);
                if (minute >= 15 && minute < 30) time.set(Calendar.MINUTE, 15);
                if (minute >= 30 && minute < 45) time.set(Calendar.MINUTE, 30);
                if (minute >= 45 && minute < 60) time.set(Calendar.MINUTE, 45);
                Schedule schedule = new Schedule();
                schedule.setStart(new Date(time.getTimeInMillis()));
                schedule.setEnd(new Date(time.getTimeInMillis() + 60 * 15 * 1000 ));
                schedule.setType(1);
                mapSchedule.put(time.getTimeInMillis(), schedule);
                events.add(schedule.toEvent());
                weekView.notifyDatasetChanged();
            }
        });
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return events;
    }

    @Override
    public void success(ScheduleResponse scheduleResponse, Response response) {
        events = new ArrayList<>();
        listFree = new ArrayList<>();
        if (scheduleResponse != null && scheduleResponse.getScheduleList() != null)
            for (Schedule schedule : scheduleResponse.getScheduleList()) {
                if (schedule.getType() == 1) {
                    listFree.add(schedule);
                }
            }
        new Axy().execute(scheduleResponse);
    }

    @Override
    public void failure(RetrofitError error) {

    }

    @SuppressLint("StaticFieldLeak")
    class Axy extends AsyncTask<ScheduleResponse, Void, ArrayList<WeekViewEvent>> {

        @Override
        protected ArrayList<WeekViewEvent> doInBackground(ScheduleResponse... scheduleResponses) {
            ScheduleResponse scheduleResponse = scheduleResponses[0];
            if (scheduleResponse != null && scheduleResponse.getScheduleList() != null) {
                mapSchedule = new HashMap<Long, Schedule>();
                for (Schedule schedule : scheduleResponse.getScheduleList()) {
                    Date start = (Date) schedule.getStart().clone();
                    Date end = (Date) schedule.getEnd().clone();
                    while (start.getTime() < end.getTime()) {
                        if (mapSchedule.get(start.getTime()) != null && mapSchedule.get(start.getTime()).getType() == 2) {
                            start.setTime(start.getTime() + 60 * 15 * 1000);
                            continue;
                        }
                        Schedule schedule1 = new Schedule();
                        Date start1 = (Date) start.clone();
                        schedule1.setStart(start1);
                        start.setTime(start.getTime() + 60 * 15 * 1000);
                        Date end1 = (Date) start.clone();
                        schedule1.setEnd(end1);
                        schedule1.setType(schedule.getType());
                        mapSchedule.put(start1.getTime(), schedule1);
//                        events.add(weekViewEvent);
                    }
                }
            }
            ArrayList<WeekViewEvent> events = new ArrayList<>();
            for (Schedule schedule: mapSchedule.values()) {
                events.add(schedule.toEvent());
            }
//            Calendar today = Calendar.getInstance();
//            today.set(Calendar.MINUTE, 0);
//            today.set(Calendar.HOUR, 0);
//            today.set(Calendar.MILLISECOND, 0);
//            today.set(Calendar.SECOND, 0);
//            Calendar maxDay = (Calendar) today.clone();
//            maxDay.add(Calendar.DATE, 30);
//            while (today.getTimeInMillis() < maxDay.getTimeInMillis()) {
//                WeekViewEvent weekViewEvent = new WeekViewEvent();
//                weekViewEvent.setName("");
//                Calendar start = (Calendar) today.clone();
//                weekViewEvent.setStartTime(start);
//                today.add(Calendar.MINUTE, 15);
//                Calendar end = (Calendar) today.clone();
//                weekViewEvent.setEndTime(end);
//                weekViewEvent.setColor(Color.parseColor("#2789e4"));
//                for (Schedule schedule :
//                        listFree) {
//                    if (schedule.dateIsInSchedule(start.getTimeInMillis(), end.getTimeInMillis())) {
//                        events.add(weekViewEvent);
//                        break;
//                    }
//                }
//            }
            return events;
        }

        @Override
        protected void onPostExecute(final ArrayList<WeekViewEvent> weekViewEvents) {
            super.onPostExecute(weekViewEvents);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    events = weekViewEvents;
                    weekView.notifyDatasetChanged();
                }
            });
        }
    }
}
