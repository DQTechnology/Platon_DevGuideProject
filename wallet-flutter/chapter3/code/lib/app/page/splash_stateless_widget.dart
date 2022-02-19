import 'package:flutter/material.dart';

class SplashStatelessWidget extends StatelessWidget {
  const SplashStatelessWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    double width = size.width;
    double height = size.height;
    return Container(
        decoration: const BoxDecoration(color: Colors.white),
        child: Stack(children: [
          Positioned(
              top: ((height - 128) / 2) - 128,
              left: (width - 128) / 2,
              child: const Image(
                  width: 128,
                  height: 128,
                  image: AssetImage("images/splash_logo.png"))),
          Positioned(
              bottom: 6,
              width: width,
              child: const Center(
                  child: Text('Powered by DQTech',
                      textDirection: TextDirection.rtl,
                      style: TextStyle(
                          decoration: TextDecoration.none,
                          color: Color(0xff242424),
                          fontStyle: FontStyle.normal,
                          fontSize: 12))))
        ]));
  }
}
