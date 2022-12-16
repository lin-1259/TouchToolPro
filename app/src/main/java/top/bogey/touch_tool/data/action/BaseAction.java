package top.bogey.touch_tool.data.action;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinDirection;
import top.bogey.touch_tool.data.action.pin.PinType;

public class BaseAction {
    private final String id;
    private final String tag;
    private CharSequence title;
    protected transient int titleId;

    private boolean enable = true;

    private final List<Pin<?>> pins = new ArrayList<>();

    public int x;
    public int y;

    protected final Pin<?> inPin;
    protected final Pin<?> outPin;

    public BaseAction(String tag) {
        id = UUID.randomUUID().toString();
        this.tag = tag;

        inPin = new Pin<>(PinType.EXCUTE, null);
        outPin = new Pin<>(PinType.EXCUTE, PinDirection.OUT, null);
    }

    public boolean doAction(WorldState worldState, Task task) {
        return true;
    }

    public boolean checkReady(WorldState worldState, Task task) {
        return true;
    }

    protected boolean sleep(long time) {
        try {
            Thread.sleep(time);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean matchActionTag(String tag) {
        if (this.tag == null || this.tag.isEmpty()) return false;
        return this.tag.startsWith(tag);
    }

    public <T> Pin<T> addPin(Pin<T> pin) {
        if (pin == null) throw new RuntimeException("空的插槽");
        for (Pin<?> oldPin : pins) {
            if (oldPin.getId().equals(pin.getId())) throw new RuntimeException("重复的插槽");
        }
        pins.add(pin);
        return pin;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public CharSequence getTitle(Context context) {
        if (title == null || title.length() == 0) {
            if (titleId == 0) return null;
            return context.getString(titleId);
        } else return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Pin<?>> getPins() {
        return pins;
    }
}
