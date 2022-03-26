import 'package:flutter/material.dart';

class ShadowButton extends StatefulWidget {
  final BorderRadiusGeometry? borderRadius;
  final double? width;
  final double height;
  final Gradient gradient;
  final VoidCallback? onPressed;
  final Widget child;
  final bool showShadow;
  final Color shadowColor;
  late bool isEnable;

  final Gradient disableGradient;

  ShadowButton(
      {Key? key,
      required this.onPressed,
      required this.child,
      this.borderRadius,
      this.shadowColor = Colors.transparent,
      this.width,
      this.height = 44.0,
      this.gradient =
          const LinearGradient(colors: [Color(0xff1b60f3), Color(0xff3b92f1)]),
      this.disableGradient =
          const LinearGradient(colors: [Color(0xffeaeaea), Color(0xffffffff)]),
      this.showShadow = false,
      this.isEnable = true})
      : super(key: key);

  @override
  _ShadowButtonState createState() {
    return _ShadowButtonState();
  }
}

class _ShadowButtonState extends State<ShadowButton> {
  @override
  Widget build(BuildContext context) {
    final borderRadius = widget.borderRadius ?? BorderRadius.circular(0);
    return Ink(
        decoration: BoxDecoration(
          gradient: widget.isEnable ? widget.gradient : widget.disableGradient,
          borderRadius: borderRadius,
          boxShadow: [
            BoxShadow(
                color: widget.shadowColor,
                offset: const Offset(0, 0), //阴影y轴偏移量
                blurRadius: 0.5,
                spreadRadius: 0)
          ]
        ),
        child: InkWell(
          onTap: widget.onPressed,
          borderRadius: BorderRadius.circular(20),
          child: Container(

            alignment: Alignment.center,
            // onTap:
            child: widget.child,
          ),
        ),
      );

  }
}
