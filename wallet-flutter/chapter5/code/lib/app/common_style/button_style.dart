import 'package:flutter/material.dart';

ButtonStyle getButtonStyle() {
  return ButtonStyle(
      padding: MaterialStateProperty.all(
          const EdgeInsets.only(left: 20, top: 0, bottom: 0, right: 20)),
      minimumSize: MaterialStateProperty.all(const Size(0, 34)),
      shape: MaterialStateProperty.all(
          RoundedRectangleBorder(borderRadius: BorderRadius.circular(20))),
      foregroundColor: MaterialStateProperty.all(const Color(0xff105CFF)),
      backgroundColor: MaterialStateProperty.all(Colors.white));
}
