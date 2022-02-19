import 'package:flutter/cupertino.dart';

class ItemTitle extends StatelessWidget {
  String title;

  ItemTitle({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Text(
      title,
      style: const TextStyle(
        fontSize: 17,
        color: Color(0xff61646E),
      ),
    );
  }
}
