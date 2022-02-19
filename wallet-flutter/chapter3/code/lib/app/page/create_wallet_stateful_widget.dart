import 'package:digging/app/custom_widget/custom_text_field.dart';
import 'package:digging/app/custom_widget/item_title.dart';
import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:digging/app/page/back_mnemonic_stateful_widget.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/status_bar_util.dart';
import 'package:flutter/material.dart';

class CreateWalletStatefulWidget extends StatefulWidget {
  const CreateWalletStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return CreateWalletState();
  }
}

class CreateWalletState extends State<CreateWalletStatefulWidget> {
  bool _isEnabled = false;

  final ValueNotifier<TextEditingValue> _nameController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  final ValueNotifier<TextEditingValue> _passwordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  final ValueNotifier<TextEditingValue> _repeatPasswordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  bool _isEnableName = false;
  bool _isEnablePassword = false;

  @override
  void initState() {
    super.initState();
  }

  /// 验证钱包名称
  String? _validateWalletName(TextEditingValue value) {
    String name = value.text.trim();

    if (name.isEmpty) {
      _isEnableName = false;
      setState(() {
        _enableCreate();
      });
      return "钱包名称不能为空";
    }

    if (name.length > 20) {
      _isEnableName = false;
      setState(() {
        _enableCreate();
      });
      return "请输入1-20位字符";
    }

    setState(() {
      _isEnableName = true;
      _enableCreate();
    });

    return null;
  }

  /// 验证密码
  String? _validatePassword(TextEditingValue value) {
    String password = value.text.trim();

    if (password.isEmpty) {
      setState(() {
        _isEnablePassword = false;
        _enableCreate();
      });
      return "密码不能为空";
    }
    if (password.length < 6) {
      setState(() {
        _isEnablePassword = false;
        _enableCreate();
      });

      return "密码至少6个字符";
    }

    String repeatPassword = _repeatPasswordController.value.text.trim();

    if (repeatPassword != password) {
      setState(() {
        _isEnablePassword = false;
        _enableCreate();
      });
      return "两次密码不一致";
    }
    setState(() {
      _isEnablePassword = true;
      _enableCreate();
    });

    return null;
  }

  /// 验证重复密码
  String? _validateRepeatPassword(TextEditingValue value) {
    if (_repeatPasswordController.value.text.isEmpty) {
      _isEnablePassword = false;
      setState(() {
        _enableCreate();
      });
      return "确认密码不能为空";
    }
    if (_repeatPasswordController.value.text !=
        _passwordController.value.text) {
      _isEnablePassword = false;
      setState(() {
        _enableCreate();
      });
      return "两次密码不一致";
    }

    setState(() {
      _isEnablePassword = true;
      _enableCreate();
    });

    return null;
  }

  void _enableCreate() {
    _isEnabled = _isEnableName && _isEnablePassword;
  }

  void _onCreateWallet() {
    if (!_isEnabled) {
      return;
    }

    String name = _nameController.value.text.trim();
    String password = _passwordController.value.text.trim();

    WalletManager.buildCreateWalletSession(name, password);

    // 跳转页面
    Navigator.push(context, PageRouteBuilder(pageBuilder: (BuildContext content,
        Animation<double> animation, Animation<double> secondaryAnimation) {
      // 跳转动画
      return SlideTransition(
          position: Tween<Offset>(
                  begin: const Offset(1, 0), end: const Offset(.0, .0))
              .animate(
                  CurvedAnimation(parent: animation, curve: Curves.easeIn)),
          child: const BackupMnemonicPhraseStatefulWidget());
    }));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: GestureDetector(
      //实现点击其他地方收起软键盘
      behavior: HitTestBehavior.translucent,
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        decoration: const BoxDecoration(color: Colors.white),
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
                            ItemTitle(title: "钱包名称"),
                            CustomTextFiled(
                              controller: _nameController,
                              hintText: "请输入钱包名称",
                              validator: _validateWalletName,
                            ),
                            const SizedBox(height: 10),
                            ItemTitle(title: "钱包密码"),
                            CustomTextFiled(
                              forceHideErrorTip: _isEnablePassword,
                              controller: _passwordController,
                              hintText: "不少于6位字符串，建议混合大小写、数字、符号",
                              showPasswordBtn: true,
                              validator: _validatePassword,
                            ),
                            CustomTextFiled(
                              forceHideErrorTip: _isEnablePassword,
                              controller: _repeatPasswordController,
                              hintText: "确认钱包密码",
                              showPasswordBtn: true,
                              validator: _validateRepeatPassword,
                            ),
                            const SizedBox(height: 8),
                            const Text(
                              "*不少于6位字符，建议混合大小写、数字、特殊字符",
                              style: TextStyle(
                                  fontSize: 14, color: Color(0xffB8BDD2)),
                            ),
                            Container(
                                margin: const EdgeInsets.only(top: 20),
                                child: SizedBox(
                                  width: double.infinity,
                                  height: 50,
                                  child: ShadowButton(
                                      isEnable: _isEnabled,
                                      shadowColor: const Color(0xffdddddd),
                                      borderRadius: BorderRadius.circular(44),
                                      onPressed: _onCreateWallet,
                                      child: Text(
                                        "创建钱包",
                                        style: TextStyle(
                                            fontSize: 16,
                                            fontWeight: FontWeight.bold,
                                            color: _isEnabled
                                                ? const Color(0xfff6f6f6)
                                                : const Color(0xffd8d8d8)),
                                      )),
                                )),
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
      ),
    ));
  }
}
