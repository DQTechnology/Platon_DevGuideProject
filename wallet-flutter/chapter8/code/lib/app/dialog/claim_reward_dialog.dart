import 'package:digging/app/custom_widget/custom_text_field.dart';
import 'package:digging/app/custom_widget/item_title.dart';
import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/sdk/contract/ppos/reward_contract.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/wallet.dart';
import 'package:digging/sdk/protocol/models/transaction_receipt.dart';
import 'package:digging/sdk/protocol/web3.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:get/get.dart';

import '../service/network_manager.dart';

class ClaimRewardDialog extends StatefulWidget {
  String address;
  double delegateClaim;

  ClaimRewardDialog({
    Key? key,
    required this.address,
    required this.delegateClaim,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ClaimRewardDialogState();
  }
}

class _ClaimRewardDialogState extends State<ClaimRewardDialog> {
  String _gasFee = "0.00";

  BigInt _balance = BigInt.zero;

  final Web3 _web3 = NetworkManager.getWeb3();

  ValueNotifier<TextEditingValue> passwordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  @override
  void initState() {
    super.initState();

    _loadData();
  }

  _loadData() async {
    _balance = await _web3.getBalance(widget.address);

    BigInt price = await _web3.gasPrice();
    _gasFee = AmountUtil.convertVonToLat(price * BigInt.from(49920));
    setState(() {});
  }

  _onClainReward() async {
    String password = passwordController.value.text;

    String walletName = WalletManager.getWalletNameByAddress(widget.address);

    Wallet wallet = await WalletManager.getWallet(walletName, password);

    Credentials credentials = Credentials.createByECKeyPair(wallet.keyPair);

    RewardContract rewardContract = RewardContract.load(_web3, credentials);

    TransactionReceipt txReceipt =
        await rewardContract.withdrawDelegateReward();
    if (txReceipt.status == BigInt.one) {
      Fluttertoast.showToast(msg: "领取成功");
    } else {
      Fluttertoast.showToast(msg: "领取失败");
    }
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(20), topRight: Radius.circular(20)),
      child: Container(
        padding:
            const EdgeInsets.only(top: 10, bottom: 10, left: 20, right: 20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.only(top: 4, bottom: 10),
              decoration: const BoxDecoration(
                  border: Border(
                      bottom:
                          BorderSide(width: 0.5, color: Color(0xffedeef2)))),
              child: const Text(
                "领取奖励",
                textAlign: TextAlign.center,
                style: TextStyle(
                    fontSize: 16,
                    color: Colors.black,
                    fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(
              height: 10,
            ),
            Text(
              AmountUtil.convertValueToLat(widget.delegateClaim),
              style: const TextStyle(color: Color(0xff105cfe), fontSize: 22),
            ),
            const SizedBox(
              height: 6,
            ),
            const Text(
              "预估奖励，以实际到账为准，一次最多可领取20个节点的奖励",
              textAlign: TextAlign.start,
              style: TextStyle(color: Color(0xff898c9e), fontSize: 12),
            ),
            const SizedBox(
              height: 10,
            ),
            Row(
              children: [
                const Text(
                  "预计手续费: ",
                  style: TextStyle(fontSize: 14, color: Color(0xff61646e)),
                  strutStyle: StrutStyle(leading: 0.5, fontSize: 13),
                ),
                const Spacer(
                  flex: 1,
                ),
                Text(
                  _gasFee,
                  style: const TextStyle(fontSize: 14, color: Colors.black),
                  strutStyle: const StrutStyle(leading: 0.5, fontSize: 14),
                ),
                const SizedBox(
                  width: 3,
                ),
                const Text(
                  "LAT",
                  style: TextStyle(fontSize: 14, color: Colors.black),
                  strutStyle: StrutStyle(leading: 0.5, fontSize: 14),
                ),
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Row(
              children: [
                const Text(
                  "领取钱包: ",
                  style: TextStyle(fontSize: 14, color: Color(0xff61646e)),
                  strutStyle: StrutStyle(leading: 0.5, fontSize: 13),
                ),
                const Spacer(
                  flex: 1,
                ),
                Text(
                  WalletManager.getWalletNameByAddress(widget.address),
                  style: const TextStyle(fontSize: 14, color: Colors.black),
                  strutStyle: const StrutStyle(leading: 0.5, fontSize: 14),
                ),
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Container(
              alignment: Alignment.centerLeft,
              child: ItemTitle(
                title: "钱包密码",
                fontSize: 14,
              ),
            ),
            CustomTextFiled(
              hintText: "请输入钱包密码",
              controller: passwordController,
            ),
            Text("可用余额: ${AmountUtil.convertVonToLat(_balance)} LAT"),
            Container(
                margin: const EdgeInsets.only(top: 10),
                color: Colors.transparent,
                child: SizedBox(
                    width: double.infinity,
                    height: 44,
                    child: ShadowButton(
                      isEnable: true,
                      shadowColor: const Color(0xffdddddd),
                      borderRadius: BorderRadius.circular(44),
                      onPressed: _onClainReward,
                      child: const Text("确认",
                          style: TextStyle(
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                              color: Color(0xfff6f6f6))),
                    ))),
          ],
        ),
      ),
    );
    ;
  }
}
