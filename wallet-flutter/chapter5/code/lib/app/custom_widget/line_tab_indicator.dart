import 'package:flutter/cupertino.dart';

class LineTabIndicator extends Decoration {
  final BorderSide borderSide;
  final EdgeInsetsGeometry insets;

  const LineTabIndicator(
      {this.borderSide = const BorderSide(width: 2.0, color: Color(0xff105CFF)),
      this.insets = EdgeInsets.zero});

  @override
  _LineTabIndicatorPainter createBoxPainter([VoidCallback? onChanged]) {
    return _LineTabIndicatorPainter(this, onChanged);
  }
}

class _LineTabIndicatorPainter extends BoxPainter {
  final LineTabIndicator decoration;

  BorderSide get borderSide => decoration.borderSide;

  EdgeInsetsGeometry get insets => decoration.insets;

  late Paint _paint;

  _LineTabIndicatorPainter(this.decoration, VoidCallback? onChanged)
      : super(onChanged) {
    // 设置paint为圆角
    _paint = borderSide.toPaint()..strokeCap = StrokeCap.round;
  }

  @override
  void paint(Canvas canvas, Offset offset, ImageConfiguration configuration) {
    assert(configuration.size != null);
    assert(configuration.textDirection != null);

    final Rect rect = offset & configuration.size!;

    // 获取indicator的边界
    final TextDirection textDirection = configuration.textDirection!;
    Rect indicatorRect = insets.resolve(textDirection).deflateRect(rect);

    indicatorRect = Rect.fromLTRB(
        indicatorRect.left,
        indicatorRect.top,
        indicatorRect.right,
        indicatorRect.bottom - decoration.borderSide.width);

    canvas.drawLine(
        indicatorRect.bottomLeft, indicatorRect.bottomRight, _paint);
  }
}
