package top.bogey.touch_tool.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainActivity;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.start.AppStartAction;
import top.bogey.touch_tool.data.action.start.BatteryChargingStartAction;
import top.bogey.touch_tool.data.action.start.BatteryStartAction;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.data.action.start.NormalStartAction;
import top.bogey.touch_tool.data.action.start.NotificationStartAction;
import top.bogey.touch_tool.data.action.start.StartAction;

// 黑板类，记录着当前系统的一些属性
public class WorldState {
    private static WorldState helper;

    private final LinkedHashMap<CharSequence, PackageInfo> appMap = new LinkedHashMap<>();

    private CharSequence packageName;
    private CharSequence activityName;

    private CharSequence notificationPackage;
    private CharSequence notificationText;

    private int batteryPercent;
    private int batteryState;

    private final LinkedHashMap<ManualStartAction, Task> manualStartActions = new LinkedHashMap<>();

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
            if (packageInfo.activities != null && packageInfo.activities.length > 0) {
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

    public ArrayList<PackageInfo> findPackageList(Context context, boolean system, CharSequence find, boolean common) {
        ArrayList<PackageInfo> packages = new ArrayList<>();

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

    private void checkAutoStartAction(Class<? extends StartAction> actionType) {
        MainAccessibilityService service = MainApplication.getService();
        if (service == null || !service.isServiceEnabled()) return;

        // 特有的开始项
        ArrayList<Task> tasks = TaskRepository.getInstance().getTasksByStart(actionType);
        for (Task task : tasks) {
            for (StartAction startAction : task.getStartActions(actionType)) {
                if (startAction.checkReady(this, task)) service.runTask(task, startAction);
            }
        }

        // 通用的开始项
        tasks = TaskRepository.getInstance().getTasksByStart(NormalStartAction.class);
        for (Task task : tasks) {
            for (StartAction startAction : task.getStartActions(NormalStartAction.class)) {
                if (startAction.checkReady(this, task)) service.runTask(task, startAction);
            }
        }
    }

    private void showManualActionDialog(boolean show) {
        MainAccessibilityService service = MainApplication.getService();
        if (service == null || !service.isServiceEnabled()) return;

        manualStartActions.clear();
        if (show) {
            ArrayList<Task> tasks = TaskRepository.getInstance().getTasksByStart(ManualStartAction.class);
            for (Task task : tasks) {
                for (StartAction startAction : task.getStartActions(ManualStartAction.class)) {
                    if (startAction.checkReady(this, task)) {
                        manualStartActions.put((ManualStartAction) startAction, task);
                    }
                }
            }
        }

        MainActivity activity = MainApplication.getActivity();
        if (activity == null) {
            Intent intent = new Intent(service, MainActivity.class);
            intent.putExtra(MainActivity.INTENT_KEY_BACKGROUND, true);
            intent.putExtra(MainActivity.INTENT_KEY_SHOW_PLAY, manualStartActions.size());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            service.startActivity(intent);
        } else {
            activity.handlePlayFloatView(manualStartActions.size());
        }
    }

    public void enterActivity(CharSequence packageName, CharSequence className) {
        if (isActivityClass(packageName, className)) {
            boolean pkg = setPackageName(packageName);
            boolean cls = setActivityName(className);
            if (pkg || cls) {
                checkAutoStartAction(AppStartAction.class);
                showManualActionDialog(true);
            }
        }
    }

    public void setEnterActivity(CharSequence packageName, CharSequence className) {
        if (isActivityClass(packageName, className)) {
            setPackageName(packageName);
            setActivityName(className);
            showManualActionDialog(false);
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
        return notificationText;
    }

    public CharSequence getNotificationPackage() {
        return notificationPackage;
    }

    public void setNotification(CharSequence notificationPackage, CharSequence notificationText) {
        this.notificationPackage = notificationPackage;
        this.notificationText = notificationText;
        checkAutoStartAction(NotificationStartAction.class);
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(int batteryPercent) {
        if (batteryPercent == this.batteryPercent) return;
        this.batteryPercent = batteryPercent;
        checkAutoStartAction(BatteryStartAction.class);
    }

    public int getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(int batteryState) {
        if (batteryState == this.batteryState) return;
        this.batteryState = batteryState;
        checkAutoStartAction(BatteryChargingStartAction.class);
    }

    public LinkedHashMap<ManualStartAction, Task> getManualStartActions() {
        return manualStartActions;
    }
}
