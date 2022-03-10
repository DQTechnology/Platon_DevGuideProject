import 'package:digging/app/page/build_wallet_info_stateful_widget.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';

class ImportKeystoreStatefulWidget extends StatefulWidget {
  const ImportKeystoreStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ImportKeystoreStatefulWidgetState();
  }
}

class _ImportKeystoreStatefulWidgetState
    extends State<ImportKeystoreStatefulWidget> {
  bool _isKeystoreEnable = false;

  bool _bShowKeystoreError = false;

  final TextEditingController _controller = TextEditingController();

  final WalletInfoController _walletController = WalletInfoController();

  @override
  void initState() {
    super.initState();

    _controller.addListener(() {
      String keystore = _controller.value.text;
      setState(() {
        _isKeystoreEnable = keystore.isNotEmpty;
        _bShowKeystoreError = !_isKeystoreEnable;
      });
    });

    _walletController.addListener(() async {
      String keystore = _controller.value.text;

      bool bSucceed = await WalletManager.importKeyStore(
          _walletController.value.walletName,
          _walletController.value.password,
          keystore);

      if (bSucceed) {
        Fluttertoast.showToast(msg: "导入钱包文件成功!");
      } else {
        Fluttertoast.showToast(msg: "导入钱包文件失败,请检查文件是否合法或者密码是否正确!");
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
                "直接复制粘贴您之前生成的钱包文件(.json)内容至输入框。",
                style: TextStyle(
                  fontSize: 12,
                  color: Color(0xff61646e),
                ),
              ),
              const SizedBox(height: 16),
              Container(
                height: 160,
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
                height: _bShowKeystoreError ? 30 : 0,
                child: const Text(
                  "请输入钱包文件!",
                  style: TextStyle(color: Colors.red),
                ),
              ),
              SizedBox(
                height: _bShowKeystoreError ? 0 : 10,
              ),
              BuildWalletInfoStatefulWidget(
                  controller: _walletController,
                  showRepeatPassword: false,
                  showPasswordStrengthTip: false,
                  enableFlag: _isKeystoreEnable,
                  buttonName: "开始导入")
            ],
          ),
        ),
      )),
    );
  }
}
