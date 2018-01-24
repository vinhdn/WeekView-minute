package com.alamkanak.weekview.sample.apiclient;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vinh on 1/24/18.
 */

public class ScheduleResponse {
    @SerializedName("Data")
    private List<Schedule> scheduleList;

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
