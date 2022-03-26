import 'package:flutter/cupertino.dart';

class ItemTitle extends StatelessWidget {
  String title;

  double fontSize;

  ItemTitle({Key? key, required this.title, this.fontSize = 15}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Text(
      title,
      style:  TextStyle(
        fontSize: fontSize,
        color: const Color(0xff61646E),
      ),
    );
  }
}
