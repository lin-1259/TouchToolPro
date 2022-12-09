package top.bogey.touch_tool.data.action;

public final class ActionTag {
    public ActionTag() {
        throw new RuntimeException();
    }

    // 起点
    public final static String GROUP_START = "START";
    public final static String START_MANUAL = "START_MANUAL";               // 手动开始
    public final static String START_TIME = "START_TIME";                   // 定时开始
    public final static String START_NOTIFICATION = "START_NOTIFICATION";   // 收到通知时开始
    public final static String START_APP = "START_APP";                     // 进入应用或应用某个界面开始
    public final static String START_BATTERY = "START_BATTERY";             // 电量到达某个区间开始
    public final static String START_BATTERY_STATE = "START_BATTERY_STATE"; // 电池处于某个状态时开始
    public final static String START_CUSTOM = "START_CUSTOM";               // 自定义开始

    // 状态点
    public final static String GROUP_STATE = "STATE";
    public final static String STATE_TIME = "STATE_TIME";                   // 处于某个时间段内
    public final static String STATE_APP = "STATE_APP";                     // 处于某个应用内或应用某个界面内
    public final static String STATE_BATTERY = "STATE_BATTERY";             // 电量处于某个区间内
    public final static String STATE_BATTERY_STATE = "STATE_BATTERY_STATE"; // 电池处于某个状态
    public final static String STATE_SCREEN = "STATE_SCREEN";               // 屏幕状态 息屏/锁屏/亮屏
    public final static String STATE_CAPTURE = "STATE_CAPTURE";             // 屏幕录制开启状态

    public final static String STATE_TEXT = "STATE_TEXT";                   // 界面内存在文本
    public final static String STATE_IMAGE = "STATE_IMAGE";                 // 界面内存在图片
    public final static String STATE_COLOR = "STATE_COLOR";                 // 界面内存在某个颜色
    public final static String STATE_NUMBER = "STATE_NUMBER";               // 计数

    // 动作点
    public final static String GROUP_ACTION = "ACTION";
    public final static String ACTION_DELAY = "ACTION_DELAY";               // 延迟
    public final static String ACTION_TEXT = "ACTION_TEXT";                 // 点击文本
    public final static String ACTION_TOUCH = "ACTION_TOUCH";               // 点击位置
    public final static String ACTION_COLOR = "ACTION_COLOR";               // 点击颜色
    public final static String ACTION_INPUT = "ACTION_INPUT";               // 给输入框输入文本
    public final static String ACTION_SYSTEM = "ACTION_SYSTEM";             // 系统按键什么的
    public final static String ACTION_SCREEN = "ACTION_SCREEN";             // 点亮/锁定屏幕
    public final static String ACTION_CAPTURE = "ACTION_CAPTURE";           // 屏幕录制开启/关闭
    public final static String ACTION_APP = "ACTION_APP";                   // 打开应用或应用内界面
    public final static String ACTION_TOAST = "ACTION_TOAST";               // 输出提示
    public final static String ACTION_CUSTOM = "ACTION_CUSTOM";             // 跳转到一个开始节点

    public final static String ACTION_NUMBER = "ACTION_NUMBER";             // 计数

    // 逻辑点
    public final static String GROUP_LOGIC = "LOGIC";
    public final static String LOGIC_CONDITION_ALL = "LOGIC_CONDITION_ALL"; // 达成所有条件
    public final static String LOGIC_CONDITION_ONE = "LOGIC_CONDITION_ONE"; // 只要有一个条件达成
    public final static String LOGIC_LOOP_TIMES = "LOGIC_LOOP_TIMES";       // 次数循环
    public final static String LOGIC_LOOP_STATE = "LOGIC_LOOP_STATE";       // 根据状态循环
}
