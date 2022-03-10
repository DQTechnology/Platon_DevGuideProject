import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:digging/app/page/create_wallet_stateful_widget.dart';
import 'package:digging/app/page/import_stateful_widget.dart';
import 'package:digging/app/util/status_bar_util.dart';
import 'package:flutter/material.dart';

class OperateMenuStatelessWidget extends StatelessWidget {
  const OperateMenuStatelessWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // 获取屏幕的宽高
    StatusBarUtil.setDark();
    final size = MediaQuery.of(context).size;
    double width = size.width;
    double height = size.height;
    return Container(
        decoration: const BoxDecoration(color: Colors.white),
        child: Stack(children: [
          // 显示logo
          Positioned(
              top: ((height - 110) / 2) - 128,
              left: (width - 110) / 2,
              child: const Image(
                  width: 130,
                  height: 130,
                  image: AssetImage("images/splash4.png"))),
          Positioned(
              bottom: 6,
              width: width,
              child: Center(
                  child: Column(children: [
                SizedBox(
                  width: width - 100,
                  height: 44,
                  child: ShadowButton(
                      shadowColor: const Color(0xffbbbbbb),
                      onPressed: () {
                        // 跳转页面
                        Navigator.push(context, PageRouteBuilder(pageBuilder:
                            (BuildContext content, Animation<double> animation,
                                Animation<double> secondaryAnimation) {
                          // 跳转动画
                          return SlideTransition(
                              position: Tween<Offset>(
                                      begin: const Offset(1, 0),
                                      end: const Offset(.0, .0))
                                  .animate(CurvedAnimation(
                                      parent: animation, curve: Curves.easeIn)),
                              child: const CreateWalletStatefulWidget());
                        }));
                      },
                      borderRadius: BorderRadius.circular(44),
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const Text("创建钱包"),
                            Container(
                                margin: const EdgeInsets.only(left: 5),
                                child: const Image(
                                    width: 20,
                                    height: 20,
                                    image: AssetImage(
                                        "images/icon_create_wallet.png"))),
                          ]),
                      gradient: const LinearGradient(
                          colors: [Color(0xff104dcf), Color(0xff3b92f1)])),
                ),
                Container(
                  margin: const EdgeInsets.only(top: 12),
                  child: SizedBox(
                    width: width - 100,
                    height: 44,
                    child: ShadowButton(
                        shadowColor: const Color(0xffbbbbbb),
                        onPressed: () {
                          // 跳转页面
                          Navigator.push(context, PageRouteBuilder(pageBuilder:
                              (BuildContext content,
                                  Animation<double> animation,
                                  Animation<double> secondaryAnimation) {
                            // 跳转动画
                            return SlideTransition(
                                position: Tween<Offset>(
                                        begin: const Offset(1, 0),
                                        end: const Offset(.0, .0))
                                    .animate(CurvedAnimation(
                                        parent: animation,
                                        curve: Curves.easeIn)),
                                child: const ImportStatefulWidget());
                          }));
                        },
                        borderRadius: BorderRadius.circular(44),
                        child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              const Text(
                                "导入钱包",
                                style: TextStyle(color: Color(0xff000000)),
                              ),
                              Container(
                                  margin: const EdgeInsets.only(left: 5),
                                  child: const Image(
                                      width: 20,
                                      height: 20,
                                      image: AssetImage(
                                          "images/icon_import_wallet.png"))),
                            ]),
                        gradient: const LinearGradient(
                            colors: [Color(0xffeaeaea), Color(0xffffffff)])),
                  ),
                ),
                Container(
                  margin: const EdgeInsets.only(top: 12, bottom: 12),
                  child: const Text(
                    "已有钱包？使用钱包文件、助记词、私钥导入",
                    style: TextStyle(
                        decoration: TextDecoration.none,
                        fontSize: 12,
                        color: Color(0xff61646e),
                        fontWeight: FontWeight.normal),
                  ),
                )
              ])))
        ]));
  }
}
