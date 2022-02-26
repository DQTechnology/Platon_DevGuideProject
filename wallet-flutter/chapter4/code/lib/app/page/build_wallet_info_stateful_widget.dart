import 'package:digging/app/custom_widget/custom_text_field.dart';
import 'package:digging/app/custom_widget/item_title.dart';
import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:flutter/cupertino.dart';

class WalletInfo {
  final String walletName;
  final String password;

  const WalletInfo({this.walletName = "", this.password = ""});

  static const WalletInfo empty = WalletInfo();
}

class WalletInfoController extends ValueNotifier<WalletInfo> {
  WalletInfoController({WalletInfo value = WalletInfo.empty}) : super(value);

  @override
  set value(WalletInfo newValue) {
    super.value = newValue;
  }

  @override
  get value {
    return super.value;
  }
}

class BuildWalletInfoStatefulWidget extends StatefulWidget {
  String buttonName;

  bool showRepeatPassword;

  WalletInfoController? controller;

  bool enableFlag;

  bool showPasswordStrengthTip;

  BuildWalletInfoStatefulWidget(
      {Key? key,
      required this.buttonName,
      this.showRepeatPassword = true,
      this.enableFlag = true,
      this.showPasswordStrengthTip = true,
      this.controller})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _BuildWalletInfoStatefulWidgetState();
  }
}

class _BuildWalletInfoStatefulWidgetState
    extends State<BuildWalletInfoStatefulWidget> {
  final ValueNotifier<TextEditingValue> _nameController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  final ValueNotifier<TextEditingValue> _passwordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  final ValueNotifier<TextEditingValue> _repeatPasswordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  bool _isEnableName = false;
  bool _isEnablePassword = false;

  bool _isEnabled = false;

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

  void _enableCreate() {
    _isEnabled = _isEnableName && _isEnablePassword && widget.enableFlag;
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

    /// 不验证重复密码
    if (!widget.showRepeatPassword) {
      setState(() {
        _isEnablePassword = true;
        _enableCreate();
      });
      return null;
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

  void _onFinish() {
    if (!_isEnabled) {
      return;
    }

    if (widget.controller == null) {
      return;
    }
    // 去除输入框的焦点
    FocusScope.of(context).requestFocus(FocusNode());

    String name = _nameController.value.text.trim();
    String password = _passwordController.value.text.trim();
    widget.controller!.value = WalletInfo(walletName: name, password: password);
  }

  @override
  Widget build(BuildContext context) {
    _enableCreate();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ItemTitle(title: "钱包名称"),
        CustomTextFiled(
          controller: _nameController,
          hintText: "请输入钱包名称",
          validator: _validateWalletName,
        ),
        ItemTitle(title: "钱包密码"),
        CustomTextFiled(
          forceHideErrorTip: _isEnablePassword,
          controller: _passwordController,
          hintText: widget.showPasswordStrengthTip ? "不少于6位字符串，建议混合大小写、数字、符号" : "请输入钱包文件密码",
          showPasswordBtn: true,
          validator: _validatePassword,
        ),
        LayoutBuilder(
            builder: (BuildContext context, BoxConstraints constraints) {
          if (widget.showRepeatPassword) {
            return CustomTextFiled(
              forceHideErrorTip: _isEnablePassword,
              controller: _repeatPasswordController,
              hintText: "确认钱包密码",
              showPasswordBtn: true,
              validator: _validateRepeatPassword,
            );
          } else {
            return const SizedBox(height: 0, width: 0);
          }
        }),
        LayoutBuilder(
            builder: (BuildContext context, BoxConstraints constraints) {
          if (widget.showPasswordStrengthTip) {
            return const Text(
              "*不少于6位字符，建议混合大小写、数字、特殊字符",
              style: TextStyle(fontSize: 14, color: Color(0xffB8BDD2)),
            );
          } else {
            return const SizedBox(height: 0, width: 0);
          }
        }),
        Container(
            margin: const EdgeInsets.only(top: 30),
            child: SizedBox(
              width: double.infinity,
              height: 44,
              child: ShadowButton(
                  isEnable: _isEnabled,
                  shadowColor: const Color(0xffdddddd),
                  borderRadius: BorderRadius.circular(44),
                  onPressed: _onFinish,
                  child: Text(
                    widget.buttonName,
                    style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: _isEnabled
                            ? const Color(0xfff6f6f6)
                            : const Color(0xffd8d8d8)),
                  )),
            )),
      ],
    );
  }
}
