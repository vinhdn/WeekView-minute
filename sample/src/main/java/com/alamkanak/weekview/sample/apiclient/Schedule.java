package com.alamkanak.weekview.sample.apiclient;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.sample.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vinh on 1/24/18.
 */

public class Schedule {
    @SerializedName("time_start")
    private String timeStart;
    @SerializedName("time_end")
    private String timeEnd;
    private int type;

    @Expose
    private Date start;
    @Expose
    private Date end;

    private String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    private String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getStart() {
        if(start == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                start = sdf.parse(getTimeStart());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        if(end == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                end = sdf.parse(getTimeEnd());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public WeekViewEvent toEvent() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if (start == null || end == null) {
            try {
                start = sdf.parse(getTimeStart());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
            try {
                end = sdf.parse(getTimeEnd());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Initialize start and end time.
        Calendar now = Calendar.getInstance();
        Calendar startTime = (Calendar) now.clone();
        startTime.setTimeInMillis(start.getTime());
        Calendar endTime = (Calendar) startTime.clone();
        endTime.setTimeInMillis(end.getTime());

        // Create an week view event.
        WeekViewEvent weekViewEvent = new WeekViewEvent();
        weekViewEvent.setId(startTime.getTimeInMillis());
        weekViewEvent.setName("");
        weekViewEvent.setStartTime(startTime);
        weekViewEvent.setEndTime(endTime);
        weekViewEvent.setColor(getType() == 1 ? Color.parseColor("#2789e4") : Color.parseColor("#f57f68"));

        return weekViewEvent;
    }

    public boolean dateIsInSchedule(long startTime, long endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if (start == null || end == null) {
            try {
                start = sdf.parse(getTimeStart());
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
            try {
                end = sdf.parse(getTimeEnd());
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (startTime >= start.getTime() && endTime <= end.getTime()) {
            return true;
        }
        return false;
    }
}
