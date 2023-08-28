package top.bogey.touch_tool_pro.bean.action;

import androidx.annotation.StringRes;

public class ActionCheckResult {
    public final ActionResultType type;
    public final @StringRes int tips;

    public ActionCheckResult(ActionResultType type, int tips) {
        this.type = type;
        this.tips = tips;
    }

    public enum ActionResultType {
        NORMAL,
        WARNING,
        ERROR
    }
}
