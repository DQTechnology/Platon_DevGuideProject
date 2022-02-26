import 'package:flutter/material.dart';

typedef Validator = String? Function(TextEditingValue value);

class CustomTextFiled extends StatefulWidget {
  String hintText;

  Validator? validator;

  ValueNotifier<TextEditingValue>? controller;

  bool showPasswordBtn;

  bool forceHideErrorTip;

  CustomTextFiled(
      {Key? key,
      this.hintText = "",
      this.validator,
      this.controller,
      this.forceHideErrorTip = false,
      this.showPasswordBtn = false})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _CustomTextFiledState();
  }
}

class _CustomTextFiledState extends State<CustomTextFiled> {
  _CustomTextFiledState();

  final FocusNode _focusNode = FocusNode();

  bool _hide = false;

  bool _hasError = false;

  String _errMsg = "";

  final TextEditingController controller = TextEditingController();

  @override
  void initState() {
    _hide = widget.showPasswordBtn;

    _focusNode.addListener(() {

      setState(() {});
    });

    controller.addListener(() {
      if (widget.controller != null) {
        widget.controller!.value = controller.value;
      }
      if (widget.validator == null) {
        return;
      }
      String? errMsg = widget.validator!(controller.value);
      setState(() {
        if (errMsg != null) {
          _errMsg = errMsg;
          _hasError = true;
        } else {
          _errMsg = "";
          _hasError = false;
        }
      });
    });

    super.initState();
  }

  /// 显示获取隐藏密码
  void _onShowOrHide() {
    setState(() {
      _focusNode.requestFocus();
      _hide = !_hide;
    });
  }

  Color _getColor() {
    if (_hasError) {
      return Colors.red;
    }

    if (_focusNode.hasFocus) {
      return const Color(0xff3485de);
    }
    return const Color(0xffD5D8DF);
  }

  @override
  Widget build(BuildContext context) {


    if (widget.forceHideErrorTip) {
      _errMsg = "";
      _hasError = false;
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
            decoration: BoxDecoration(
                border: Border(bottom: BorderSide(color: _getColor()))),
            child: Flex(
              direction: Axis.horizontal,
              children: [
                Expanded(
                  flex: 1,
                  child: TextField(
                    controller: controller,
                    focusNode: _focusNode,
                    obscureText: _hide,
                    decoration: InputDecoration(
                      enabledBorder: const UnderlineInputBorder(
                          borderSide: BorderSide(color: Colors.transparent)),
                      focusedBorder: const UnderlineInputBorder(
                          borderSide: BorderSide(color: Colors.transparent)),
                      hintStyle: const TextStyle(
                          color: Color(0xffB8BDD2), fontSize: 14),
                      hintText: widget.hintText,
                    ),
                  ),
                ),
                Offstage(
                  offstage: !widget.showPasswordBtn,
                  child: GestureDetector(
                      onTap: _onShowOrHide,
                      child: Image(
                        width: 26,
                        height: 26,
                        image: _hide
                            ? const AssetImage("images/icon_close_eyes.png")
                            : const AssetImage("images/icon_open_eyes.png"),
                      )),
                )
              ],
            )),
        Container(
            margin: const EdgeInsets.only(top: 6),
            child: Text(
              _errMsg,
              style: const TextStyle(color: Colors.red, fontSize: 12),
            )),
      ],
    );
  }
}
