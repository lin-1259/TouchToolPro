// IUserService.aidl
package top.bogey.touch_tool_pro.shizuku;

import top.bogey.touch_tool_pro.shizuku.CmdResult;

interface IUserService {
    void destroy() = 16777114;
    CmdResult runCommand(String cmd) = 1;
}