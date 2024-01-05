package top.bogey.touch_tool_pro.bean.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import top.bogey.touch_tool_pro.MainApplication;

public class ActionConfigInfo {
    private final @StringRes int title;
    private final @DrawableRes int icon;
    private final Class<? extends Action> actionClass;
    private final boolean superAction;

    public ActionConfigInfo() {
        title = 0;
        icon = 0;
        actionClass = null;
        superAction = false;
    }

    public ActionConfigInfo(int title, int icon, Class<? extends Action> actionClass) {
        this.title = title;
        this.icon = icon;
        this.actionClass = actionClass;
        superAction = false;
    }

    public ActionConfigInfo(int title, int icon, Class<? extends Action> actionClass, boolean superAction) {
        this.title = title;
        this.icon = icon;
        this.actionClass = actionClass;
        this.superAction = superAction;
    }

    public String getTitle() {
        if (title != 0) return MainApplication.getInstance().getString(title);
        return "";
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public boolean isSuperAction() {
        return superAction;
    }
}
