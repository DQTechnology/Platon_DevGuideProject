import 'package:digging/app/page/build_wallet_info_stateful_widget.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:get/route_manager.dart';

class ImportPrivateKeyStatefulWidget extends StatefulWidget {
  const ImportPrivateKeyStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ImportPrivateKeyStatefulWidgetState();
  }
}

class _ImportPrivateKeyStatefulWidgetState
    extends State<ImportPrivateKeyStatefulWidget> {
  bool _isPrivateKeyEnable = false;

  bool _bShowPrivateKeyError = false;

  final TextEditingController _controller = TextEditingController();

  final WalletInfoController _walletController = WalletInfoController();

  @override
  void initState() {
    super.initState();

    _controller.addListener(() {
      String keystore = _controller.value.text;
      setState(() {
        _isPrivateKeyEnable = keystore.isNotEmpty;
        _bShowPrivateKeyError = !_isPrivateKeyEnable;
      });
    });

    _walletController.addListener(() async {
      WalletInfo walletInfo = _walletController.value;
      String privateKey = _controller.value.text;
      bool bSucceed = await WalletManager.importPrivateKey(
          walletInfo.walletName, walletInfo.password, privateKey);
      if (bSucceed) {
        Fluttertoast.showToast(msg: "导入秘钥成功!");
        Get.offAllNamed("/main");
      } else {
        Fluttertoast.showToast(msg: "导入秘钥失败!");
      }
    });
  }

  void _onPaste() async {
    ClipboardData? clipboardData = await Clipboard.getData('text/plain');
    if (clipboardData == null) {
      return;
    }
    if (clipboardData.text != null) {
      String text = clipboardData.text!;

      _controller.text = text;
      // 设置光标的位置
      _controller.selection = TextSelection.fromPosition(
          TextPosition(affinity: TextAffinity.downstream, offset: text.length));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
          child: Container(
        padding:
            const EdgeInsets.only(top: 10, left: 16, right: 16, bottom: 20),
        child: SizedBox(
          width: double.infinity,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                "输入私钥内容至输入框。请留意字符大小写。使用私钥导入的同时需要设定钱包密码。",
                style: TextStyle(
                  fontSize: 12,
                  color: Color(0xff61646e),
                ),
              ),
              const SizedBox(height: 16),
              Container(
                height: 140,
                decoration: const BoxDecoration(color: Color(0xfff0f1f5)),
                child: Flex(
                  direction: Axis.vertical,
                  children: [
                    Expanded(
                      flex: 1,
                      child: Container(
                        padding: const EdgeInsets.all(5),
                        child: TextField(
                          controller: _controller,
                          maxLines: null,
                          decoration: const InputDecoration(
                            enabledBorder: UnderlineInputBorder(
                                borderSide:
                                    BorderSide(color: Colors.transparent)),
                            focusedBorder: UnderlineInputBorder(
                                borderSide:
                                    BorderSide(color: Colors.transparent)),
                            hintStyle: TextStyle(
                                color: Color(0xffB8BDD2), fontSize: 14),
                            hintText: "请输入钱包文件",
                          ),
                        ),
                      ),
                    ),
                    Container(
                      alignment: Alignment.centerRight,
                      child: GestureDetector(
                          onTap: _onPaste,
                          child: Container(
                              padding: const EdgeInsets.all(10),
                              child: const Text(
                                "粘贴",
                                style: TextStyle(color: Color(0xff105cfe)),
                              ))),
                    ),
                  ],
                ),
              ),
              SizedBox(
                height: _bShowPrivateKeyError ? 30 : 0,
                child: const Text(
                  "请输入秘钥!",
                  style: TextStyle(color: Colors.red),
                ),
              ),
              SizedBox(
                height: _bShowPrivateKeyError ? 0 : 10,
              ),
              BuildWalletInfoStatefulWidget(
                  controller: _walletController,
                  enableFlag: _isPrivateKeyEnable,
                  buttonName: "开始导入")
            ],
          ),
        ),
      )),
    );
  }
}
