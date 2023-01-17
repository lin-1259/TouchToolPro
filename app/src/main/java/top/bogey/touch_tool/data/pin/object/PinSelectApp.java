package top.bogey.touch_tool.data.pin.object;

import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PinSelectApp extends PinValue {

    private final LinkedHashMap<CharSequence, ArrayList<CharSequence>> packages = new LinkedHashMap<>();
    private final int mode;

    public PinSelectApp(int mode) {
        super();
        this.mode = mode;
    }

    public PinSelectApp(Parcel in) {
        super(in);
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        for (String key : bundle.keySet()) {
            packages.put(key, bundle.getCharSequenceArrayList(key));
        }
        mode = in.readInt();
    }

    public LinkedHashMap<CharSequence, ArrayList<CharSequence>> getPackages() {
        return packages;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        Bundle bundle = new Bundle();
        for (LinkedHashMap.Entry<CharSequence, ArrayList<CharSequence>> entry : packages.entrySet()) {
            bundle.putCharSequenceArrayList((String) entry.getKey(), entry.getValue());
        }
        dest.writeBundle(bundle);
        dest.writeInt(mode);
    }
}
