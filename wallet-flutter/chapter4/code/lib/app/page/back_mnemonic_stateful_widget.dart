import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:digging/app/page/verify_mnemonic_stateful_widget.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:flutter/material.dart';

class BackupMnemonicPhraseStatefulWidget extends StatefulWidget {
  const BackupMnemonicPhraseStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _BackupMnemonicPhraseStatefulWidgetState();
  }
}

class _BackupMnemonicPhraseStatefulWidgetState
    extends State<BackupMnemonicPhraseStatefulWidget> {
  String _getWord(int index) {
    CreateWalletSessionInfo? sessionInfo =
        WalletManager.getCreateWalletSession();
    if (sessionInfo == null) {
      return "";
    }
    return sessionInfo.mnemonicWords[index];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Container(
      decoration: const BoxDecoration(color: Colors.white),
      child: Flex(
        crossAxisAlignment: CrossAxisAlignment.start,
        direction: Axis.vertical,
        children: [
          PageHeader(title: "备份助记词"),
          Expanded(
              flex: 1,
              child: Container(
                padding: const EdgeInsets.only(
                    top: 16, left: 16, right: 16, bottom: 20),
                child: SizedBox(
                  width: double.infinity,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "请抄写下方助记词，我们将在下一步进行验证",
                        style: TextStyle(
                          fontSize: 17,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                      const SizedBox(height: 16),
                      const Text(
                        "助记词用于恢复钱包或重置钱包密码，请将它准确抄写在纸上并存放在只有您知道的安全地点。",
                        style: TextStyle(
                          fontSize: 12,
                          color: Color(0xff61646e),
                        ),
                      ),
                      const SizedBox(height: 16),
                      SizedBox(
                          width: double.infinity,
                          height: 257,
                          child: GridView.count(
                              padding: EdgeInsets.zero,
                              crossAxisCount: 3,
                              mainAxisSpacing: 10.0,
                              crossAxisSpacing: 10.0,
                              childAspectRatio: 2.4,
                              children: List.generate(
                                12,
                                (index) => Container(
                                    decoration: const BoxDecoration(
                                        color: Color(0xfff0f1f5)),
                                    child: Center(
                                      child: Text(
                                        _getWord(index),
                                        style: const TextStyle(fontSize: 14),
                                      ),
                                    )),
                              )))
                    ],
                  ),
                ),
              )),
          Container(
            margin: const EdgeInsets.only(left: 16, right: 16, bottom: 20),
            child: SizedBox(
              width: double.infinity,
              height: 50,
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
                          child: VerifyMnemonicPhraseStatefulWidget());
                    }));
                  },
                  borderRadius: BorderRadius.circular(44),
                  child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text("下一步"),
                        Container(
                            margin: const EdgeInsets.only(left: 5),
                            child: const Image(
                                width: 20,
                                height: 20,
                                image:
                                    AssetImage("images/icon_next_enable.png"))),
                      ]),
                  gradient: const LinearGradient(
                      colors: [Color(0xff104dcf), Color(0xff3b92f1)])),
            ),
          )
        ],
      ),
    ));
  }
}
