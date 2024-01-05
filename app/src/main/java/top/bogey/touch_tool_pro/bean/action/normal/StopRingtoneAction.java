package top.bogey.touch_tool_pro.bean.action.normal;

import android.media.MediaPlayer;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class StopRingtoneAction extends NormalAction {
    private transient Pin ringPin = new Pin(new PinString(PinSubType.RINGTONE), R.string.action_play_ringtone_action_subtitle_url);

    public StopRingtoneAction() {
        super(ActionType.STOP_RINGTONE);
        ringPin = addPin(ringPin);
    }

    public StopRingtoneAction(JsonObject jsonObject) {
        super(jsonObject);
        ringPin = reAddPin(ringPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        String path = ringPin.getValue(PinString.class).getValue();
        if (path != null && !path.isEmpty()) {
            Object env = runnable.getRuntimeEnv(path);
            if (env instanceof MediaPlayer player) {
                try {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    player.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        executeNext(runnable, context, outPin);
    }
}
