package at.mwllgr.wtkcontrol.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for time values including "24" as valid hour.
 * Needed because the heating system distinguishes between "24" and "00".
 */
public class TimeWith24 {
    static final int MIN = 0;
    static final int MAX_HOURS = 24;
    static final int MAX_MINUTES = 59;
    static final int MAX_SECONDS = 59;
    static final String TIME_REGEX = "^\\d\\d:\\d\\d:\\d\\d$";

    int hour;
    int minute;
    int second;

    public TimeWith24() {
    }

    public TimeWith24(int hour, int minute, int second) {
        if (!(this.setHour(hour) && this.setMinute(minute) && this.setSecond(second))) {
            throw new IllegalArgumentException("Time range invalid.");
        }
    }

    public int getHour() {
        return hour;
    }

    public boolean setHour(int hours) {
        if (hours >= MIN && hours <= MAX_HOURS) {
            this.hour = hours;
            return true;
        }
        return false;
    }

    public int getMinute() {
        return minute;
    }

    public boolean setMinute(int minutes) {
        if (minutes >= MIN && minutes <= MAX_MINUTES) {
            this.minute = minutes;
            return true;
        }
        return false;
    }

    public int getSecond() {
        return second;
    }

    public boolean setSecond(int seconds) {
        if (seconds >= MIN && seconds <= MAX_SECONDS) {
            this.second = seconds;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", this.getHour(), this.getMinute(), this.getSecond());
    }

    /**
     * Parses a time string.
     *
     * @param time Time string
     * @return true = success, false = error
     */
    public boolean fromString(String time) {
        Pattern pattern = Pattern.compile(TIME_REGEX);
        Matcher matcher = pattern.matcher(time);

        if (matcher.find()) {
            String[] timeValues = time.split(":");
            int hours = Integer.parseInt(timeValues[0]);
            int minutes = Integer.parseInt(timeValues[1]);
            int seconds = Integer.parseInt(timeValues[2]);
            return this.setHour(hours) && this.setMinute(minutes) && this.setSecond(seconds);
        } else {
            return false;
        }
    }
}
