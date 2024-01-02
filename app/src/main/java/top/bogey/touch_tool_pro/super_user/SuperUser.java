package top.bogey.touch_tool_pro.super_user;

import top.bogey.touch_tool_pro.super_user.shizuku.ShizukuSuperUser;

public class SuperUser {
    private static ISuperUser SUPER_USER = null;

    public static boolean init() {
        ISuperUser superUser = getSuperUser();
        if (superUser.init()) {
            SUPER_USER = superUser;
            return true;
        }
        return false;
    }

    public static void tryInit() {
        ISuperUser superUser = getSuperUser();
        if (superUser.tryInit()) {
            SUPER_USER = superUser;
        }
    }

    public static void exit() {
        if (SUPER_USER != null) SUPER_USER.exit();
        SUPER_USER = null;
    }

    public static boolean isSuperUser() {
        return SUPER_USER != null;
    }

    public static CmdResult runCommand(String cmd) {
        if (SUPER_USER != null) {
            return SUPER_USER.runCommand(cmd);
        }
        return null;
    }

    private static ISuperUser getSuperUser() {
        return ShizukuSuperUser.getInstance();
    }
}
