package top.bogey.touch_tool.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.start.AppStartAction;
import top.bogey.touch_tool.data.action.start.BatteryStartAction;
import top.bogey.touch_tool.data.action.start.BatteryStateStartAction;
import top.bogey.touch_tool.data.action.start.NotificationStartAction;
import top.bogey.touch_tool.data.action.start.StartAction;

// 黑板类，记录着当前系统的一些属性
public class WorldState {
    private static WorldState helper;

    private final Map<CharSequence, PackageInfo> appMap = new LinkedHashMap<>();

    private CharSequence packageName;
    private CharSequence activityName;
    private CharSequence notificationText;
    private int batteryPercent;
    private int batteryState;

    public static WorldState getInstance() {
        if (helper == null) helper = new WorldState();
        return helper;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void resetAppMap(Context context) {
        appMap.clear();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo : packages) {
            if (packageInfo.activities != null && packageInfo.activities.length > 0){
                appMap.put(packageInfo.packageName, packageInfo);
            }
        }
    }

    public boolean isActivityClass(CharSequence packageName, CharSequence className) {
        if (packageName == null) return false;
        PackageInfo packageInfo = appMap.get(packageName);
        if (packageInfo == null) return false;
        for (ActivityInfo activityInfo : packageInfo.activities) {
            if (TextUtils.equals(className, activityInfo.name)) return true;
        }
        return false;
    }

    public PackageInfo getPackage(CharSequence pkgName) {
        return appMap.get(pkgName);
    }

    public List<PackageInfo> findPackageList(Context context, boolean system, CharSequence find, boolean common) {
        List<PackageInfo> packages = new ArrayList<>();

        if (common && (find == null || find.length() == 0)) {
            PackageInfo info = new PackageInfo();
            info.packageName = context.getString(R.string.common_package_name);
            packages.add(info);
        }

        PackageManager manager = context.getPackageManager();
        Pattern pattern = null;
        if (!(find == null || find.length() == 0)) {
            pattern = Pattern.compile(find.toString().toLowerCase());
        }

        for (PackageInfo value : appMap.values()) {
            if (value.packageName.equals(context.getPackageName())) continue;
            if (system || (value.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                CharSequence title = value.applicationInfo.loadLabel(manager);
                // 包名和应用名一致的基本上都是无效应用，跳过
                if (value.packageName.equalsIgnoreCase(title.toString())) continue;
                if (pattern == null || pattern.matcher(title).find() || pattern.matcher(value.packageName).find()) {
                    packages.add(value);
                }
            }
        }
        return packages;
    }

    public void enterActivity(CharSequence packageName, CharSequence className) {
        if (isActivityClass(packageName, className)) {
            if (setPackageName(packageName) || setActivityName(className)) {
                checkAutoStartAction(AppStartAction.class);
            }
        }
    }

    private void checkAutoStartAction(Class<? extends StartAction> startActionClass) {
        MainAccessibilityService service = MainApplication.getService();
        if (service == null || !service.isServiceEnabled()) return;

        ArrayList<Task> tasks = TaskRepository.getInstance().getTasksByStart(startActionClass);
        for (Task task : tasks) {
            StartAction startAction = task.getStartAction(startActionClass);
            if (startAction.checkReady(this, task)) service.runTask(task, startAction);
        }
    }

    public CharSequence getPackageName() {
        return packageName;
    }

    public boolean setPackageName(CharSequence packageName) {
        if (packageName == null) return false;
        if (TextUtils.equals(packageName, this.packageName)) return false;
        this.packageName = packageName;
        return true;
    }

    public CharSequence getActivityName() {
        return activityName;
    }

    public boolean setActivityName(CharSequence activityName) {
        if (activityName == null) return false;
        if (TextUtils.equals(activityName, this.activityName)) return false;
        this.activityName = activityName;
        return true;
    }

    public CharSequence getNotificationText() {
        if (notificationText == null) return null;
        return notificationText;
    }

    public void setNotificationText(CharSequence notificationText) {
        this.notificationText = notificationText;
        checkAutoStartAction(NotificationStartAction.class);
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
        checkAutoStartAction(BatteryStartAction.class);
    }

    public int getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(int batteryState) {
        this.batteryState = batteryState;
        checkAutoStartAction(BatteryStateStartAction.class);
    }
}
