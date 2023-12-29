package top.bogey.touch_tool_pro.super_user;

public class SuperUser {
    private static ISuperUser SUPER_USER = null;

    public static boolean init(ISuperUser superUser) {
        if (superUser.init()) {
            SUPER_USER = superUser;
            return true;
        }
        return false;
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
}
