# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program\android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
  }
  -keep public class * implements com.bumptech.glide.module.GlideModule
  #高德地图代码混淆(如果爆出warning，在报出warning的包加入类似的语句：-dontwarn 包名)
  -keep class com.amap.api.location.**{*;}

  -keep class com.amap.api.fence.**{*;}

  -keep class com.autonavi.aps.amapapi.model.**{*;}
  #微信sdk代码混淆
  -keep class com.tencent.mm.sdk.** {
     *;
  }
  #百度推送代码混淆
    -libraryjars libs/pushservice-VERSION.jar
    -dontwarn com.baidu.**
    -keep class com.baidu.**{*; }
