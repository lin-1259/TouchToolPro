# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-assumenosideeffects class android.util.Log{
#    public static int d(...);
#}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class android.graphics.Point{*;}
-keep class android.graphics.Rect{*;}

-keep class top.bogey.touch_tool.utils.MatchResult{*;}

-keep class top.bogey.touch_tool.databinding.**{*;}
-keep class top.bogey.touch_tool.data.**{*;}

-keep enum top.bogey.touch_tool.** {*;}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile