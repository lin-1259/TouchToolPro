package top.bogey.touch_tool.data.action;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.AppUtils;

public class BaseAction implements Parcelable {
    private String id;
    private final String cls;
    private final CharSequence title;
    private CharSequence des;

    private final ArrayList<Pin<? extends PinObject>> pins = new ArrayList<>();

    public int x;
    public int y;

    protected transient Pin<? extends PinObject> inPin;
    protected transient Pin<? extends PinObject> outPin;
    protected transient ArrayList<Pin<? extends PinObject>> pinsTmp = new ArrayList<>();

    public BaseAction(Context context) {
        id = UUID.randomUUID().toString();
        cls = getClass().getName();
        title = null;

        inPin = new Pin<>(new PinExecute(), PinSlotType.MULTI);
        outPin = new Pin<>(new PinExecute(), PinDirection.OUT, PinSlotType.SINGLE);
    }

    public BaseAction(Context context, @StringRes int titleId) {
        id = UUID.randomUUID().toString();
        cls = getClass().getName();
        title = context.getString(titleId);

        inPin = new Pin<>(new PinExecute(), PinSlotType.MULTI);
        outPin = new Pin<>(new PinExecute(), PinDirection.OUT, PinSlotType.SINGLE);
    }

    public BaseAction(Parcel in) {
        cls = getClass().getName();
        id = in.readString();
        title = in.readString();
        des = in.readString();
        in.readTypedList(pinsTmp, Pin.CREATOR);
        x = in.readInt();
        y = in.readInt();
    }

    public BaseAction copy() {
        BaseAction copy = AppUtils.copy(this);
        copy.setId(UUID.randomUUID().toString());
        copy.getPins().forEach(pin -> {
            pin.setId(UUID.randomUUID().toString());
            pin.setActionId(copy.getId());
            pin.getLinks().clear();
        });
        copy.x = x + 1;
        copy.y = y + 1;
        return copy;
    }

    public static final Creator<BaseAction> CREATOR = new Creator<BaseAction>() {
        @Override
        public BaseAction createFromParcel(Parcel in) {
            try {
                Class<?> aClass = Class.forName(in.readString());
                Constructor<?> constructor = aClass.getConstructor(Parcel.class);
                return (BaseAction) constructor.newInstance(in);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public BaseAction[] newArray(int size) {
            return new BaseAction[size];
        }
    };

    public void doAction(WorldState worldState, TaskRunnable runnable) {
        doAction(worldState, runnable, outPin);
    }

    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        if (runnable.isInterrupt()) return;
        if (pin.getDirection() == PinDirection.IN) throw new RuntimeException("执行针脚不正确");

        for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
            BaseAction action = runnable.getTask().getActionById(entry.getValue());
            if (action == null) continue;
            Pin<? extends PinObject> pinById = action.getPinById(entry.getKey());
            if (pinById == null) continue;
            runnable.addProgress();
            action.doAction(worldState, runnable, pinById);
            return;
        }
    }

    // 获取针脚值之前先计算下针脚
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
    }

    protected void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public <T extends PinObject> Pin<T> addPin(Pin<T> pin) {
        addPin(pins.size(), pin);
        return pin;
    }

    public Pin<? extends PinObject> addPin(int index, Pin<? extends PinObject> pin) {
        if (pin == null) throw new RuntimeException("空的针脚");
        for (Pin<? extends PinObject> oldPin : pins) {
            if (oldPin.getId().equals(pin.getId())) throw new RuntimeException("重复的针脚");
        }
        pins.add(index, pin);
        pin.setActionId(id);
        return pin;
    }

    public Pin<? extends PinObject> removePin(Pin<? extends PinObject> pin) {
        if (pin == null) return null;
        for (Pin<? extends PinObject> oldPin : pins) {
            if (oldPin.getId().equals(pin.getId())) {
                pins.remove(oldPin);
                return oldPin;
            }
        }
        return null;
    }

    public Pin<? extends PinObject> getPinById(String id) {
        for (Pin<? extends PinObject> pin : pins) {
            if (pin.getId().equals(id)) return pin;
        }
        return null;
    }

    // 获取针脚的值
    protected PinObject getPinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        // 先看看自己是不是输出针脚，是的话先计算刷新下值，再返回数据
        if (pin.getDirection() == PinDirection.OUT) {
            calculatePinValue(worldState, task, pin);
            return pin.getValue();
        }

        // 再看看针脚有没有连接，有连接就是连接的值
        if (pin.getLinks().size() > 0) {
            for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                BaseAction action = task.getActionById(entry.getValue());
                if (action == null) continue;
                Pin<? extends PinObject> pinById = action.getPinById(entry.getKey());
                if (pinById == null) continue;
                return action.getPinValue(worldState, task, pinById);
            }
            throw new RuntimeException("针脚没有默认值");
        } else {
            // 否则，就是自己的默认值
            return pin.getValue();
        }
    }

    public CharSequence getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CharSequence getDes() {
        return des;
    }

    public void setDes(CharSequence des) {
        this.des = des;
    }

    public ArrayList<Pin<? extends PinObject>> getPins() {
        return pins;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(cls);
        dest.writeString(id);
        dest.writeString(title == null ? null : title.toString());
        dest.writeString(des == null ? null : des.toString());
        dest.writeTypedList(pins);
        dest.writeInt(x);
        dest.writeInt(y);
    }
}
