package at.mwllgr.wtkcontrol.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeWith24 {
    static final int MIN = 0;
    static final int MAX_HOURS = 24;
    static final int MAX_MINUTES = 59;
    static final int MAX_SECONDS = 59;
    static final String TIME_REGEX = "^\\d\\d:\\d\\d:\\d\\d$";

    int hours;
    int minutes;
    int seconds;

    public TimeWith24() { }

    public TimeWith24(int hours, int minutes, int seconds) {
        if(!(this.setHours(hours) && this.setMinutes(minutes) && this.setSeconds(seconds))) {
            throw new IllegalArgumentException("Time range invalid.");
        }
    }

    public int getHours() {
        return hours;
    }

    public boolean setHours(int hours) {
        if(hours >= MIN && hours <= MAX_HOURS) {
            this.hours = hours;
            return true;
        }
        return false;
    }

    public int getMinutes() {
        return minutes;
    }

    public boolean setMinutes(int minutes) {
        if(minutes >= MIN && minutes <= MAX_MINUTES) {
            this.minutes = minutes;
            return true;
        }
        return false;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean setSeconds(int seconds) {
        if(seconds >= MIN && seconds <= MAX_SECONDS) {
            this.seconds = seconds;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", this.getHours(), this.getMinutes(), this.getSeconds());
    }

    public boolean fromString(String time) {
        Pattern pattern = Pattern.compile(TIME_REGEX);
        Matcher matcher = pattern.matcher(time);

        if(matcher.find())
        {
            String[] timeValues = time.split(":");
            int hours = Integer.parseInt(timeValues[0]);
            int minutes = Integer.parseInt(timeValues[1]);
            int seconds = Integer.parseInt(timeValues[2]);
            return this.setHours(hours) && this.setMinutes(minutes) && this.setSeconds(seconds);
        }
        else
        {
            return false;
        }
    }
}
