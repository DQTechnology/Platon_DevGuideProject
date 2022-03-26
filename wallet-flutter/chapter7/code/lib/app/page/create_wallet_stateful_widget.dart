
import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/page/back_mnemonic_stateful_widget.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:flutter/material.dart';
import 'build_wallet_info_stateful_widget.dart';

class CreateWalletStatefulWidget extends StatefulWidget {
  const CreateWalletStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return CreateWalletState();
  }
}

class CreateWalletState extends State<CreateWalletStatefulWidget> {

  final WalletInfoController _controller = WalletInfoController();

  @override
  void initState() {
    super.initState();

    _controller.addListener(() {
      WalletInfo walletInfo = _controller.value;

      WalletManager.buildCreateWalletSession(
          walletInfo.walletName, walletInfo.password);

      // 跳转页面
      Navigator.push(context, PageRouteBuilder(pageBuilder:
          (BuildContext content, Animation<double> animation,
              Animation<double> secondaryAnimation) {
        // 跳转动画
        return SlideTransition(
            position: Tween<Offset>(
                    begin: const Offset(1, 0), end: const Offset(.0, .0))
                .animate(
                    CurvedAnimation(parent: animation, curve: Curves.easeIn)),
            child: const BackupMnemonicPhraseStatefulWidget());
      }));
    });
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        body: GestureDetector(
      //实现点击其他地方收起软键盘
      behavior: HitTestBehavior.translucent,
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Flex(
          crossAxisAlignment: CrossAxisAlignment.start,
          direction: Axis.vertical,
          children: [
            PageHeader(title: "创建钱包"),
            Expanded(
                flex: 1,
                child: Container(
                    padding: const EdgeInsets.only(
                        top: 16, left: 16, right: 16, bottom: 20),
                    decoration: const BoxDecoration(),
                    child: SizedBox(
                      width: double.infinity,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          BuildWalletInfoStatefulWidget(
                            controller: _controller,
                            buttonName: "创建钱包",
                          ),
                          const SizedBox(height: 10),
                          const Text(
                            "注意：请务必牢记钱包密码，服务器不会存储您的密码，遗忘丢失将无法找回！",
                            style: TextStyle(
                                color: Color(0xffff6b00), fontSize: 12),
                          )
                        ],
                      ),
                    ))),
          ]),
    ));
  }
}
