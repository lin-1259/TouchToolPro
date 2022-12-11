package top.bogey.touch_tool.data.action;

import java.util.concurrent.TimeUnit;

public class TimeArea {
    private int min;
    private int max;
    private TimeUnit unit;

    public TimeArea(int time, TimeUnit unit) {
        this(time, time, unit);
    }

    public TimeArea(int min, int max, TimeUnit unit) {
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public void setTime(int min, int max, TimeUnit unit) {
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public void setTime(int time, TimeUnit unit) {
        setTime(time, time, unit);
    }

    public long getRandomTime() {
        return unit.toMillis(Math.round(Math.random() * Math.abs(max - min) + getMin()));
    }

    public int getMin() {
        return Math.min(min, max);
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return Math.max(min, max);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}
