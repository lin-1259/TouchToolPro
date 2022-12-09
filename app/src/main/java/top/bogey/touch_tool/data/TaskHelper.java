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

import top.bogey.touch_tool.R;

// 黑板类，记录着当前系统的一些属性
public class TaskHelper {
    private static TaskHelper helper;

    private final Map<CharSequence, PackageInfo> appMap = new LinkedHashMap<>();

    private CharSequence packageName;
    private CharSequence activityName;
    private CharSequence notificationText;
    private int baterryPercent;
    private int batteryState;

    public static TaskHelper getInstance() {
        if (helper == null) helper = new TaskHelper();
        return helper;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void resetAppMap(Context context) {
        appMap.clear();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo : packages) {
            appMap.put(packageInfo.packageName, packageInfo);
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

        if (common) {
            PackageInfo info = new PackageInfo();
            info.packageName = context.getString(R.string.common_package_name);
            packages.add(info);
        }

        if (find == null || find.length() == 0) {
            packages.addAll(appMap.values());
            return packages;
        }

        PackageManager manager = context.getPackageManager();
        Pattern pattern = Pattern.compile(find.toString().toLowerCase());

        for (PackageInfo value : appMap.values()) {
            if (value.packageName.equals(context.getPackageName())) continue;
            if (system || (value.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                CharSequence title = value.applicationInfo.loadLabel(manager);
                // 包名和应用名一致的基本上都是无效应用，跳过
                if (value.packageName.equalsIgnoreCase(title.toString())) continue;
                if (pattern.matcher(title).find() || pattern.matcher(value.packageName).find()) {
                    packages.add(value);
                }
            }
        }
        return packages;
    }

    public void enterActivity(CharSequence packageName, CharSequence className) {
        if (isActivityClass(packageName, className)) {
            setPackageName(packageName.toString());
            setActivityName(className.toString());
        }
    }

    public String getPackageName() {
        if (packageName == null) return null;
        return packageName.toString();
    }

    public void setPackageName(CharSequence packageName) {
        if (packageName == null) return;
        if (TextUtils.equals(packageName, this.packageName)) return;
        this.packageName = packageName;
    }

    public String getActivityName() {
        if (activityName == null) return null;
        return activityName.toString();
    }

    public void setActivityName(CharSequence activityName) {
        if (activityName == null) return;
        if (TextUtils.equals(activityName, this.activityName)) return;
        this.activityName = activityName;
    }

    public String getNotificationText() {
        if (notificationText == null) return null;
        return notificationText.toString();
    }

    public void setNotificationText(CharSequence notificationText) {
        this.notificationText = notificationText;
    }

    public int getBaterryPercent() {
        return baterryPercent;
    }

    public void setBaterryPercent(int baterryPercent) {
        this.baterryPercent = baterryPercent;
    }

    public int getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(int batteryState) {
        this.batteryState = batteryState;
    }
}
