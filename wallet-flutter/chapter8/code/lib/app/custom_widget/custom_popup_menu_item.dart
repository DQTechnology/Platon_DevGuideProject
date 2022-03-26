import 'package:flutter/material.dart';

class CustomPopupMenuItem<T> extends PopupMenuItem<T> {

  const CustomPopupMenuItem({
    Key? key,
    T? value,
    bool enabled = true,
    VoidCallback? onTap,
    EdgeInsets? padding,
    double height = kMinInteractiveDimension,
    Widget? child,
  })  :
        super(
          key: key,
          value: value,
          onTap: onTap,
          enabled: enabled,
          padding: padding,
          height: height,
          child: child,
        );


  @override
  PopupMenuItemState<T, CustomPopupMenuItem<T>> createState() =>
      _CustomPopupMenuItemState<T>();
}

class _CustomPopupMenuItemState<T>
    extends PopupMenuItemState<T, CustomPopupMenuItem<T>> {

  @override
  void handleTap() {
    // 先执行关闭下拉菜单的代码
    Navigator.pop<T>(context, widget.value);
    // 再执行跳转页面的代码
    widget.onTap?.call();
  }
}
