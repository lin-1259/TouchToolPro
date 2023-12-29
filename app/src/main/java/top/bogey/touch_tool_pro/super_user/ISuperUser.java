package top.bogey.touch_tool_pro.super_user;

public interface ISuperUser {

    boolean init();

    void exit();

    CmdResult runCommand(String cmd);
}
