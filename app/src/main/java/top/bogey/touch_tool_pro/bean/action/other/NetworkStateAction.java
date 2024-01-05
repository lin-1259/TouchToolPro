package top.bogey.touch_tool_pro.bean.action.other;

import android.net.NetworkCapabilities;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.WorldState;

public class NetworkStateAction extends Action {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.network_state), R.string.action_network_state_subtitle_network, true);

    public NetworkStateAction() {
        super(ActionType.NETWORK_STATE);
        statePin = addPin(statePin);
    }

    public NetworkStateAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
    }

    public static int networkTypeToIndex(int state) {
        return switch (state) {
            case NetworkCapabilities.TRANSPORT_CELLULAR -> 1;
            case NetworkCapabilities.TRANSPORT_WIFI -> 2;
            default -> 0;
        };
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        int networkType = WorldState.getInstance().getNetworkType();
        statePin.getValue(PinSpinner.class).setIndex(networkTypeToIndex(networkType));
    }
}
