package top.bogey.touch_tool.data.action;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class BaseAction {
    private String id;
    private final String cls;
    private String title;
    private String des;

    private final ArrayList<Pin> pins = new ArrayList<>();

    public int x;
    public int y;

    protected transient final ArrayList<Pin> tmpPins = new ArrayList<>();

    public BaseAction(Context context) {
        this(context, 0);
    }

    public BaseAction(Context context, @StringRes int titleId) {
        id = UUID.randomUUID().toString();
        cls = getClass().getName();
        if (titleId == 0) title = null;
        else title = context.getString(titleId);
    }

    public BaseAction(JsonObject jsonObject) {
        cls = getClass().getName();
        id = jsonObject.get("id").getAsString();
        JsonElement titleElement = jsonObject.get("title");
        if (titleElement != null) title = titleElement.getAsString();
        JsonElement desElement = jsonObject.get("des");
        if (desElement != null) des = desElement.getAsString();
        x = jsonObject.get("x").getAsInt();
        y = jsonObject.get("y").getAsInt();
        for (JsonElement jsonElement : jsonObject.get("pins").getAsJsonArray()) {
            Pin pin = new Pin(jsonElement.getAsJsonObject());
            tmpPins.add(pin);
        }
    }

    public BaseAction copy() {
        Gson gson = TaskRepository.getInstance().getGson();
        String json = gson.toJson(this);
        BaseAction copy = gson.fromJson(json, BaseAction.class);
        copy.setId(UUID.randomUUID().toString());
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
        BaseAction owner = linkedPin.getOwner(actionContext);
        runnable.addProgress();
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

    public ArrayList<Pin> getShowPins() {
        return new ArrayList<>(pins);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public static class BaseActionDeserialize implements JsonDeserializer<BaseAction> {
        @Override
        public BaseAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String cls = jsonObject.get("cls").getAsString();
            try {
                Class<?> aClass = Class.forName(cls);
                Constructor<?> constructor = aClass.getConstructor(JsonObject.class);
                return (BaseAction) constructor.newInstance(jsonObject);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
