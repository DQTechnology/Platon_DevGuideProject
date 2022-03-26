import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class StatusBarUtil {
  static void setDark() {
    SystemChrome.setSystemUIOverlayStyle(getDarkOverlayStyle());
  }

  static SystemUiOverlayStyle getDarkOverlayStyle() {
    return const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        systemNavigationBarIconBrightness: Brightness.dark,
        statusBarIconBrightness: Brightness.dark,
        statusBarBrightness: Brightness.light);
  }

  static SystemUiOverlayStyle getLightOverlayStyle() {
    return const SystemUiOverlayStyle(
      /// 注意安卓要想实现沉浸式的状态栏 需要底部设置透明色
      statusBarColor: Colors.transparent,
      systemNavigationBarIconBrightness: Brightness.light,
      statusBarIconBrightness: Brightness.light,
      statusBarBrightness: Brightness.light,
    );
  }

  static void setWhite() {
    SystemUiOverlayStyle light = getLightOverlayStyle();
    SystemChrome.setSystemUIOverlayStyle(light);
  }
}
