import 'package:digging/app/util/status_bar_util.dart';
import 'package:flutter/material.dart';

class PageHeader extends StatelessWidget {
  String title;

  PageHeader({Key? key, required this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 80,
      width: double.infinity,
      decoration: const BoxDecoration(
          image: DecorationImage(
        fit: BoxFit.fill,
        image: AssetImage("images/bg_status_bar.png"),
      )),
      child: AppBar(
        systemOverlayStyle: StatusBarUtil.getDarkOverlayStyle(),
        leadingWidth: 30,
        shadowColor: const Color(0xffbbbbbb),
        elevation: 0,
        leading: IconButton(
          color: Colors.black,
          onPressed: () {
            Navigator.pop(context);
          },
          icon: const Icon(Icons.arrow_back_ios),
        ),
        title:  Text(title,
            style: const TextStyle(
                color: Colors.black,
                fontSize: 18,
                fontWeight: FontWeight.bold)),
        backgroundColor: Colors.transparent,
      ),
    );
  }
}
