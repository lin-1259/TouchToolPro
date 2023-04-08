package top.bogey.touch_tool.data.action;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.UUID;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.utils.GsonUtils;

public class BaseAction {
    private final String cls;
    private String id;
    private String title;
    private String des;

    private final ArrayList<Pin> pins = new ArrayList<>();

    public int x;
    public int y;
    public boolean showDetail = true;

    private transient int titleId;
    protected final transient ArrayList<Pin> pinsTmp = new ArrayList<>();

    public BaseAction() {
        this(0);
    }

    public BaseAction(@StringRes int titleId) {
        id = UUID.randomUUID().toString();
        cls = getClass().getName();

        this.titleId = titleId;
    }

    public BaseAction(@StringRes int titleId, JsonObject jsonObject) {
        cls = GsonUtils.getAsString(jsonObject, "cls", getClass().getName());
        id = GsonUtils.getAsString(jsonObject, "id", UUID.randomUUID().toString());
        title = GsonUtils.getAsString(jsonObject, "title", null);
        des = GsonUtils.getAsString(jsonObject, "des", null);

        x = GsonUtils.getAsInt(jsonObject, "x", 0);
        y = GsonUtils.getAsInt(jsonObject, "y", 0);
        showDetail = GsonUtils.getAsBoolean(jsonObject, "showDetail", true);

        this.titleId = titleId;
        pinsTmp.addAll(GsonUtils.getAsType(jsonObject, "pins", new TypeToken<ArrayList<Pin>>() {}.getType(), new ArrayList<>()));
    }

    public BaseAction copy() {
        BaseAction copy = GsonUtils.copy(this, BaseAction.class);

        copy.setId(UUID.randomUUID().toString());
        copy.titleId = titleId;
        copy.getPins().forEach(pin -> {
            pin.setId(UUID.randomUUID().toString());
            pin.setActionId(copy.getId());
            pin.cleanLinks();
        });
        copy.x = x + 1;
        copy.y = y + 1;

        return copy;
    }

    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        doNextAction(runnable, actionContext, pin);
    }

    protected void doNextAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (runnable.isInterrupt() || actionContext.isReturned()) return;
        if (!pin.getDirection().isOut()) throw new RuntimeException("执行针脚不正确");

        Pin linkedPin = pin.getLinkedPin(actionContext);
        if (linkedPin == null) return;
        if (!runnable.addProgress()) return;
        BaseAction owner = linkedPin.getOwner(actionContext);
        owner.doAction(runnable, actionContext, linkedPin);
    }

    // 获取针脚值之前先计算下针脚
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
    }

    // 获取针脚的值
    protected PinObject getPinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        // 先看看自己是不是输出针脚，是的话先计算刷新下值，再返回数据
        if (pin.getDirection().isOut()) {
            calculatePinValue(runnable, actionContext, pin);
            return pin.getValue();
        }

        // 再看看输入针脚有没有连接，有连接就是连接的值
        if (pin.getLinks().size() > 0) {
            Pin linkedPin = pin.getLinkedPin(actionContext);
            if (linkedPin == null) throw new RuntimeException("针脚没有默认值");
            BaseAction owner = linkedPin.getOwner(actionContext);
            return owner.getPinValue(runnable, actionContext, linkedPin);
        } else {
            // 否则，就是自己的默认值
            return pin.getValue();
        }
    }

    protected void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    public Pin addPin(Pin pin) {
        addPin(pins.size(), pin);
        return pin;
    }

    public Pin addPin(int index, Pin pin) {
        if (pin == null) throw new RuntimeException("空的针脚");
        for (Pin oldPin : pins) {
            if (oldPin.getId().equals(pin.getId())) throw new RuntimeException("重复的针脚");
        }
        pins.add(index, pin);
        pin.setActionId(id);
        return pin;
    }

    public Pin reAddPin(Pin defaultValue) {
        Pin pin = null;
        if (pinsTmp.size() > 0) {
            Pin tmp = pinsTmp.get(0);
            if (tmp.getPinClass().equals(defaultValue.getPinClass())) {
                pin = pinsTmp.remove(0);
            }
        }
        if (pin == null) {
            return addPin(defaultValue);
        } else {
            addPin(pins.size(), pin);
            pin.setTitleId(defaultValue.getTitleId());

            if (defaultValue.getValue() instanceof PinSpinner) {
                PinSpinner spinner = (PinSpinner) pin.getValue();
                PinSpinner defaultSpinner = (PinSpinner) defaultValue.getValue();
                spinner.setArray(defaultSpinner.getArray());
            }

            return pin;
        }
    }

    public void reAddPin(Pin defaultValue, int lastCount) {
        int count = 0;
        while (pinsTmp.size() > lastCount && count < 50) {
            reAddPin(defaultValue);
            count++;
        }
    }

    public Pin removePin(Pin pin) {
        if (pin == null) return null;
        for (Pin oldPin : pins) {
            if (oldPin.getId().equals(pin.getId())) {
                pins.remove(oldPin);
                return oldPin;
            }
        }
        return null;
    }

    public Pin getPinById(String id) {
        for (Pin pin : pins) {
            if (pin.getId().equals(id)) return pin;
        }
        return null;
    }

    public Pin getPinByUid(String uid) {
        for (Pin pin : pins) {
            if (pin.getUid().equals(uid)) return pin;
        }
        return null;
    }

    public String getTitle(Context context) {
        if (titleId == 0 || context == null) return title;
        return context.getString(titleId);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public ArrayList<Pin> getPins() {
        return pins;
    }

    public ArrayList<Pin> getShowPins() {
        return new ArrayList<>(pins);
    }
}
