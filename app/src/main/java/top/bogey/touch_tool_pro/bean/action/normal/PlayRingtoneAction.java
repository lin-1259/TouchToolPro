package top.bogey.touch_tool_pro.bean.action.normal;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class PlayRingtoneAction extends NormalAction {
    private transient Pin ringPin = new Pin(new PinString(PinSubType.RINGTONE), R.string.action_play_ringtone_action_subtitle_url);

    public PlayRingtoneAction() {
        super(ActionType.PLAY_RINGTONE);
        ringPin = addPin(ringPin);
    }

    public PlayRingtoneAction(JsonObject jsonObject) {
        super(jsonObject);
        ringPin = reAddPin(ringPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        String path = ringPin.getValue(PinString.class).getValue();
        if (path != null && !path.isEmpty()) {
            try {
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(MainApplication.getInstance(), Uri.parse(path));
                player.setAudioStreamType(AudioManager.STREAM_RING);
                player.setOnPreparedListener(MediaPlayer::start);
                player.setOnCompletionListener(MediaPlayer::release);
                player.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executeNext(runnable, context, outPin);
    }
}
