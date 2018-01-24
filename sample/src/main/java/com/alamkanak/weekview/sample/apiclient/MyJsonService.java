package com.alamkanak.weekview.sample.apiclient;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;

/**
 * Created by Raquib-ul-Alam Kanak on 1/3/16.
 * Website: http://alamkanak.github.io
 */
public interface MyJsonService {

    @GET("/1kpjf")
    void listEvents(Callback<List<Event>> eventsCallback);

    @Headers({
            "TokenCode:7ffb3614ab03c60cdfe5298e4a673050",
            "DeviceId:95c7524b-7e5d-4832-9ff2-5284d9b15c9e",
            "ApplicationCode:BIN"
    })
    @GET("/api/v1/doctor/fetch-doctor-schedule")
    void listSchedule(Callback<ScheduleResponse> scheduleCallback);

}
