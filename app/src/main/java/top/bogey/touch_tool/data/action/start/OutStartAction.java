package top.bogey.touch_tool.data.action.start;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.data.pin.object.PinString;

public class OutStartAction extends StartAction {
    private final Pin<? extends PinObject> idPin;

    public OutStartAction(Context context) {
        super(context, R.string.action_out_start_title);
        idPin = addPin(new Pin<>(new PinString(getId()), context.getString(R.string.action_normal_start_subtitle_condition)));
    }

    public OutStartAction(Parcel in) {
        super(in);
        idPin = addPin(pinsTmp.remove(0));
    }

    @Override
    public RestartType getRestartType() {
        PinSpinner value = (PinSpinner) restartPin.getValue();
        return RestartType.values()[value.getIndex()];
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        ((PinString) idPin.getValue()).setValue(getId());
        super.writeToParcel(dest, flags);
    }
}
