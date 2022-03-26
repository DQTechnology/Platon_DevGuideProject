
import 'package:flutter/material.dart';

class StakingStatus {
  static String getNodeStatusText(int status) {
    if (status == 1) {
      return "候选中";
    } else if (status == 2) {
      return "活跃中";
    } else if (status == 3) {
      return "出块中";
    } else if (status == 6) {
      return "共识中";
    }
    return "未知";
  }

  static Color getNodeStatusColor(int status) {
    if (status == 1) {
      return const Color(0xff19a20e);
    } else if (status == 2) {
      return const Color(0xff4a90e2);
    } else if (status == 3) {
      return const Color(0xfff79d10);
    } else if (status == 6) {
      return const Color(0xfff79d10);
    }
    return Colors.black;
  }
}
