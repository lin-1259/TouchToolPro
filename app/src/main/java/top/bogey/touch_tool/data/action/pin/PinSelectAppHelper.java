package top.bogey.touch_tool.data.action.pin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PinSelectAppHelper {
    public final static int SINGLE_MODE = 0;
    public final static int SINGLE_WITH_ACTIVITY_MODE = 1;
    public final static int MULTI_MODE = 2;
    public final static int MULTI_WITH_ACTIVITY_MODE = 3;

    private final Map<CharSequence, List<CharSequence>> packages = new LinkedHashMap<>();
    private final int mode;

    public PinSelectAppHelper(int mode) {
        this.mode = mode;
    }

    public Map<CharSequence, List<CharSequence>> getPackages() {
        return packages;
    }

    public int getMode() {
        return mode;
    }

}
