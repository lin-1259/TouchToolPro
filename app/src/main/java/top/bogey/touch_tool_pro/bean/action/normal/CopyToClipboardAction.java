package top.bogey.touch_tool_pro.bean.action.normal;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class CopyToClipboardAction extends NormalAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string);

    public CopyToClipboardAction() {
        super(ActionType.COPY);
        textPin = addPin(textPin);
    }

    public CopyToClipboardAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = reAddPin(textPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString text = (PinString) getPinValue(runnable, context, textPin);
        if (text.getValue() != null && !text.getValue().isEmpty()) {
            MainApplication instance = MainApplication.getInstance();
            ClipboardManager manager = (ClipboardManager) instance.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(instance.getString(R.string.app_name), text.getValue());
            manager.setPrimaryClip(clipData);
        }
        executeNext(runnable, context, outPin);
    }
}
