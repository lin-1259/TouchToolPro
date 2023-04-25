package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.ui.app.AppView;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinSelectApp extends PinValue {

    private final LinkedHashMap<String, ArrayList<String>> packages;
    private final int mode;

    public PinSelectApp() {
        this(AppView.MULTI_WITH_ACTIVITY_MODE);
    }

    public PinSelectApp(int mode) {
        super();
        packages = new LinkedHashMap<>();
        this.mode = mode;
    }

    public PinSelectApp(JsonObject jsonObject) {
        super(jsonObject);
        packages = GsonUtils.getAsType(jsonObject, "packages", new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {}.getType(), new LinkedHashMap<>());
        mode = GsonUtils.getAsInt(jsonObject, "mode", AppView.MULTI_WITH_ACTIVITY_MODE);
    }

    public LinkedHashMap<String, ArrayList<String>> getPackages() {
        return packages;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.AppPinColor);
    }

    @Override
    public boolean isEmpty() {
        return packages.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinSelectApp that = (PinSelectApp) o;

        if (mode != that.mode) return false;
        return packages.equals(that.packages);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + packages.hashCode();
        result = 31 * result + mode;
        return result;
    }
}
