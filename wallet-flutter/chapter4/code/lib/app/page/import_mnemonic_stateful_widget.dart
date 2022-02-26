import 'package:digging/app/service/wallet_manager.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'back_mnemonic_stateful_widget.dart';
import 'build_wallet_info_stateful_widget.dart';

class ImportMnemonicStatefulWidget extends StatefulWidget {
  const ImportMnemonicStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ImportMnemonicStatefulWidgetState();
  }
}

class _ImportMnemonicStatefulWidgetState extends State {
  final WalletInfoController _controller = WalletInfoController();

  // 创建textField对应的controller
  final List<TextEditingController> _mnemonicWorldControllers =
      List.generate(12, (index) => TextEditingController());

  bool _isMnemoicEnable = false;

  bool _bShowMnemoicError = false;

  @override
  void initState() {
    super.initState();

    _controller.addListener(() async {
      WalletInfo walletInfo = _controller.value;

      List<String> mnemonicWorlds = _getMnemonic();

      bool bSucceed = await WalletManager.importMnemonicWords(
          walletInfo.walletName, walletInfo.password, mnemonicWorlds);

      if (bSucceed) {
        Fluttertoast.showToast(msg: "导入助记词成功!");
      } else {
        Fluttertoast.showToast(msg: "导入助记词失败:请检查助记词是否正确!");
      }
    });
    _setWordWatcher();
  }

  List<String> _getMnemonic() {
    List<String> mnemonicWorlds = List.empty(growable: true);
    for (int i = 0; i < 12; ++i) {
      mnemonicWorlds.add(_mnemonicWorldControllers[i].value.text);
    }

    return mnemonicWorlds;
  }

  /// 设置助记词监听
  void _setWordWatcher() {
    for (var element in _mnemonicWorldControllers) {
      element.addListener(() {
        List<String> mnemonicWorlds = _getMnemonic();

        for (String mnemonic in mnemonicWorlds) {
          if (mnemonic.isEmpty) {
            setState(() {
              _bShowMnemoicError = true;
              _isMnemoicEnable = false;
            });
            return;
          }
        }

        setState(() {
          _bShowMnemoicError = false;
          _isMnemoicEnable = true;
        });
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: SingleChildScrollView(
            child: Container(
                padding: const EdgeInsets.only(
                    top: 10, left: 16, right: 16, bottom: 20),
                decoration: const BoxDecoration(color: Colors.white),
                child: SizedBox(
                  width: double.infinity,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "请将记下的助记词按顺序填入以下表格中，在每个助记词输入完成后按“空格”键可以跳转到下个单词，使用助记词导入的同时需要设定钱包密码。",
                        style: TextStyle(
                          fontSize: 12,
                          color: Color(0xff61646e),
                        ),
                      ),
                      const SizedBox(height: 16),
                      SizedBox(
                          width: double.infinity,
                          height: 222,
                          child: GridView.count(
                              padding: EdgeInsets.zero,
                              crossAxisCount: 3,
                              mainAxisSpacing: 10.0,
                              crossAxisSpacing: 10.0,
                              childAspectRatio: 2.4,
                              children: List.generate(
                                  12,
                                  (index) => Container(
                                      padding: const EdgeInsets.only(
                                          left: 5, right: 5),
                                      decoration: const BoxDecoration(
                                          color: Color(0xfff0f1f5)),
                                      child: TextField(
                                        controller:
                                            _mnemonicWorldControllers[index],
                                        textAlign: TextAlign.center,
                                        decoration: InputDecoration(
                                          enabledBorder:
                                              const UnderlineInputBorder(
                                                  borderSide: BorderSide(
                                                      color:
                                                          Colors.transparent)),
                                          focusedBorder:
                                              const UnderlineInputBorder(
                                                  borderSide: BorderSide(
                                                      color:
                                                          Colors.transparent)),
                                          hintStyle: const TextStyle(
                                              color: Color(0xffB8BDD2),
                                              fontSize: 14),
                                          hintText: (index + 1).toString(),
                                        ),
                                      ))))),
                      SizedBox(
                        height: _bShowMnemoicError ? 30 : 0,
                        child: const Text(
                          "助记词不能有空!",
                          style: TextStyle(color: Colors.red),
                        ),
                      ),
                      SizedBox(
                        height: _bShowMnemoicError ? 0 : 10,
                      ),
                      BuildWalletInfoStatefulWidget(
                        controller: _controller,
                        enableFlag: _isMnemoicEnable,
                        buttonName: "开始导入",
                      ),
                    ],
                  ),
                ))));
  }
}
