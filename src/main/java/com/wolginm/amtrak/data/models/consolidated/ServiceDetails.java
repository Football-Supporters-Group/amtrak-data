package com.wolginm.amtrak.data.models.consolidated;

import java.sql.Date;

import com.wolginm.amtrak.data.models.gtfs.Calendar;

public class ServiceDetails {

    private Calendar calendar;

    public ServiceDetails(final Calendar calendar) {
        this.calendar = calendar;
    }

    public String getService_Id() {
        return this.calendar.getService_id();
    }

    /**
     * Gets the days the service is opperating.
     * @return boolean array. Mon, Tues, Wed, Thur, Fri, Sat, Sun
     */
    public boolean[] weekCalendar() {
        boolean[] week = new boolean[7];
        week[0] = this.calendar.isMonday();
        week[1] = this.calendar.isTuesday();
        week[2] = this.calendar.isWednesday();
        week[3] = this.calendar.isThursday();
        week[4] = this.calendar.isFriday();
        week[5] = this.calendar.isSaturday();
        week[6] = this.calendar.isSunday();

        return week;
    }

    public Date getStartDate() {
        return this.calendar.getStartDate();
    }

    public Date getEndDate() {
        return this.calendar.getEndDate();
    }

    public boolean isWeekday() {
        return this.calendar.isMonday()
            && this.calendar.isTuesday()
            && this.calendar.isWednesday()
            && this.calendar.isThursday()
            && this.calendar.isFriday();
    }

    public boolean isWeekend() {
        return this.calendar.isMonday()
            && this.calendar.isTuesday()
            && this.calendar.isWednesday()
            && this.calendar.isThursday()
            && this.calendar.isFriday();
    }
}
