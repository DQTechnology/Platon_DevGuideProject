import 'dart:typed_data';

import 'package:digging/app/custom_widget/custom_text_field.dart';
import 'package:digging/app/custom_widget/item_title.dart';
import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/raw_transaction.dart';
import 'package:digging/sdk/crypto/wallet.dart';
import 'package:digging/sdk/protocol/Web3.dart';
import 'package:digging/sdk/utils/convert.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/transaction_encoder.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'build_wallet_info_stateful_widget.dart';

class SendLatStatefulWidget extends StatefulWidget {
  const SendLatStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _SendLatStatefulWidgetState();
  }
}

class _SendLatStatefulWidgetState extends State<SendLatStatefulWidget> {
  final WalletInfoController _controller = WalletInfoController();

  final Web3 _web3 = Web3.build("http://35.247.155.162:6789");

  String _gasFee = "";
  String _balance = "";

  final ValueNotifier<TextEditingValue> _receiverController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);
  final ValueNotifier<TextEditingValue> _sendBalanceController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);
  final ValueNotifier<TextEditingValue> _remarkController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  final ValueNotifier<TextEditingValue> _passwordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  @override
  void initState() {
    super.initState();

    _loadBalance();
    _loadGasFee();
  }

  void _loadGasFee() async {
    BigInt price = await _web3.gasPrice();
    _gasFee = AmountUtil.convertVonToLat(price * BigInt.from(21000));

    setState(() {});
  }

  void _loadBalance() async {
    String address = await WalletManager.getCurrentAddress();

    BigInt balance = await _web3.getBalance(address);

    _balance = AmountUtil.convertVonToLat(balance);
    setState(() {});
  }

  void _onSendLAT() async {
    Wallet wallet =
        await WalletManager.getCurrentWallet(_passwordController.value.text);

    Credentials credentials = Credentials.createByECKeyPair(wallet.keyPair);

    BigInt nonce = await _web3.getTransactionCount(credentials.address);
    BigInt gasPrice = await _web3.gasPrice();

    RawTransaction rawTransaction = RawTransaction(
        nonce,
        gasPrice,
        BigInt.from(21000),
        _receiverController.value.text,
        Convert.toVon(_sendBalanceController.value.text, Unit.KPVON),
        _remarkController.value.text);

    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, credentials, BigInt.from(210309));

    String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

    String txHash = await _web3.sendRawTransaction(hexSignData);

    Fluttertoast.showToast(msg: "发送成功!");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          PageHeader(title: "发送"),
          Expanded(
              flex: 1,
              child: SingleChildScrollView(
                child: Container(
                  padding: const EdgeInsets.only(
                      top: 10, left: 16, right: 16, bottom: 20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      ItemTitle(title: "发送至"),
                      CustomTextFiled(
                        controller: _receiverController,
                        hintText: "请输入接收地址",
                      ),
                      ItemTitle(title: "发送金额"),
                      CustomTextFiled(
                        controller: _sendBalanceController,
                        hintText: "0.00000",
                      ),
                      CustomTextFiled(
                        controller: _remarkController,
                        hintText: "备注(可选,30字以内)",
                      ),
                      ItemTitle(title: "钱包密码"),
                      CustomTextFiled(
                        showPasswordBtn: true,
                        controller: _passwordController,
                        hintText: "请输入钱包密码",
                      ),
                      Container(
                          padding: const EdgeInsets.all(14),
                          width: double.infinity,
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text(
                                "交易手续费",
                                style: TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.bold,
                                    color: Color(0xff61646e)),
                              ),
                              const SizedBox(
                                height: 8,
                              ),
                              Text(
                                _gasFee,
                                style: const TextStyle(
                                    fontSize: 18, fontWeight: FontWeight.bold),
                              )
                            ],
                          ),
                          decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(4),
                              boxShadow: const [
                                BoxShadow(
                                    offset: Offset(0, 0),
                                    blurRadius: 2,
                                    spreadRadius: 1,
                                    color: Color(0x88dddddd))
                              ])),
                      Container(
                          margin: const EdgeInsets.only(top: 50),
                          child: SizedBox(
                              width: double.infinity,
                              height: 44,
                              child: ShadowButton(
                                isEnable: true,
                                shadowColor: const Color(0xffdddddd),
                                borderRadius: BorderRadius.circular(44),
                                onPressed: _onSendLAT,
                                child: const Text("发送",
                                    style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                        color: Color(0xfff6f6f6)
                                        // color: _isEnabled
                                        //     ? const Color(0xfff6f6f6)
                                        //     : const Color(0xffd8d8d8)),
                                        )),
                              )))
                    ],
                  ),
                ),
              ))
        ],
      ),
    );
  }
}
