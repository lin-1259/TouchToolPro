// IUserService.aidl
package top.bogey.touch_tool_pro;

interface IUserService {
    void destroy() = 16777114;
    String runCommand(String cmd) = 1;
}