import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/address_format_util.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/sdk/contract/ppos/delegate_contract.dart';
import 'package:digging/sdk/contract/ppos/dto/call_response.dart';
import 'package:digging/sdk/contract/ppos/dto/delegation_id_info.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/wallet.dart';
import 'package:digging/sdk/protocol/models/transaction_receipt.dart';
import 'package:digging/sdk/protocol/web3.dart';
import 'package:digging/sdk/utils/convert.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:get/get.dart';
import '../custom_widget/custom_text_field.dart';
import '../custom_widget/item_title.dart';
import '../custom_widget/shadow_button.dart';

class WithDrawDelegateStatefulWidget extends StatefulWidget {
  const WithDrawDelegateStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _WithDrawDelegateStatefulWidgetState();
  }
}

class _WithDrawDelegateStatefulWidgetState<WithDrawDelegateStatefulWidget>
    extends State {
  late String _nodeId;
  late String _nodeName;
  late String _stakingIcon;

  String _walletName = "";
  String _walletAddress = "";

  BigInt _balance = BigInt.zero;

  double _delegateValue = 0.0;

  String _gasFee = "0.00";

  final Web3 _web3 = Web3.build("http://35.247.155.162:6789");

  ValueNotifier<TextEditingValue> passwordController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  ValueNotifier<TextEditingValue> delegateAmountController =
      ValueNotifier<TextEditingValue>(TextEditingValue.empty);

  @override
  void initState() {
    super.initState();

    final arguments = Get.arguments as Map;

    _nodeId = arguments["nodeId"];
    _nodeName = arguments["nodeName"];
    _stakingIcon = arguments["stakingIcon"];
    _delegateValue = arguments["delegateValue"];
    _loadWalletInfo();
  }

  _loadWalletInfo() async {
    _walletName = await WalletManager.getCurrentWalletName();
    _walletAddress = await WalletManager.getWalletAddress(_walletName);

    _balance = await _web3.getBalance(_walletAddress);
    BigInt price = await _web3.gasPrice();
    _gasFee = AmountUtil.convertVonToLat(price * BigInt.from(49920));

    setState(() {});
  }

  ImageProvider _loadNodeImage() {
    if (_stakingIcon.isNotEmpty) {
      return NetworkImage(_stakingIcon);
    }
    return const AssetImage("images/icon_validators_default.png");
  }

  /// 赎回委托
  _onWithDrawDelegate() async {

    String password = passwordController.value.text;
    //
    Wallet wallet = await WalletManager.getWallet(_walletName, password);
    //
    Credentials credentials = Credentials.createByECKeyPair(wallet.keyPair);
    //
    DelegateContract delegateContract =
        DelegateContract.load(_web3, credentials);
    /// 获取钱包地址所有委托的节点信息
    CallResponse<List<DelegationIdInfo>> delegationIdInfoRsp =
        await delegateContract.getRelatedListByDelAddr(_walletAddress);

    List<DelegationIdInfo>? delegationIdInfoList = delegationIdInfoRsp.data;

    if (delegationIdInfoList == null) {
      Fluttertoast.showToast(msg: "获取委托信息失败!");
      return;
    }
    /// 获取指定节点信息
    DelegationIdInfo? delegationIdInfo =
        getDelegationIdInfo(delegationIdInfoList);

    if (delegationIdInfo == null) {
      Fluttertoast.showToast(msg: "获取委托节点信息失败!");
      return;
    }
    /// 赎回的数量
    BigInt amount =
        Convert.toVon(delegateAmountController.value.text, Unit.KPVON);
    /// 赎回委托
    TransactionReceipt txReceipt = await delegateContract.undelegate(
        delegationIdInfo.nodeId, delegationIdInfo.stakingBlockNum, amount);

    if(txReceipt.status == BigInt.one) {
      Fluttertoast.showToast(msg: "赎回成功");
    } else {
      Fluttertoast.showToast(msg: "赎回失败");
    }
  }

  DelegationIdInfo? getDelegationIdInfo(
      List<DelegationIdInfo> delegationIdInfoList) {
    for (DelegationIdInfo delegationIdInfo in delegationIdInfoList) {
      if (Numeric.cleanHexPrefix(delegationIdInfo.nodeId) ==
          Numeric.cleanHexPrefix(_nodeId)) {
        return delegationIdInfo;
      }
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        PageHeader(title: "赎回"),
        Expanded(
          flex: 1,
          child: SingleChildScrollView(
            child: Container(
              padding: const EdgeInsets.only(
                  top: 20, left: 16, right: 16, bottom: 20),
              width: double.infinity,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Container(
                        width: 42,
                        height: 42,
                        decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(42),
                            image: DecorationImage(image: _loadNodeImage())),
                        // child: Image(
                        //   image: _loadNodeImage(),
                        // ),
                      ),
                      const SizedBox(
                        width: 6,
                      ),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            _nodeName,
                            style: const TextStyle(
                                fontSize: 14, color: Colors.black),
                          ),
                          const SizedBox(
                            height: 6,
                          ),
                          Text(
                            AddressFormatUtil.formatAddress(_nodeId),
                            style: const TextStyle(
                                fontSize: 13, color: Color(0xff898c9e)),
                          )
                        ],
                      )
                    ],
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 30),
                    padding: const EdgeInsets.only(top: 8, bottom: 8),
                    decoration: const BoxDecoration(
                        image: DecorationImage(
                            fit: BoxFit.cover,
                            image: ExactAssetImage(
                                "images/icon_my_delegate_item_bg.png"))),
                    child: Row(
                      children: [
                        const Image(
                            width: 42,
                            height: 42,
                            image: AssetImage('images/avatar_14.png')),
                        const SizedBox(
                          width: 10,
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(_walletName),
                            Text(
                              AddressFormatUtil.formatAddress(_walletAddress),
                              style: const TextStyle(color: Color(0xff616e64)),
                            )
                          ],
                        )
                      ],
                    ),
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 16),
                    padding: const EdgeInsets.only(
                        top: 20, bottom: 20, left: 10, right: 10),
                    decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(4),
                        boxShadow: const [
                          BoxShadow(
                              offset: Offset(0, 0),
                              blurRadius: 2,
                              spreadRadius: 1,
                              color: Color(0x88dddddd))
                        ]),
                    child: Row(
                      children: [
                        const Text(
                          "已委托",
                          style: TextStyle(fontSize: 15, color: Colors.black),
                        ),
                        const Spacer(
                          flex: 1,
                        ),
                        Text(
                          AmountUtil.convertValueToLat(_delegateValue),
                          style: const TextStyle(
                              fontSize: 15, color: Colors.black),
                        ),
                        const SizedBox(
                          width: 4,
                        ),
                        const Text(
                          "LAT",
                          style: TextStyle(fontSize: 12, color: Colors.black),
                        )
                      ],
                    ),
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  ItemTitle(title: "赎回数量"),
                  CustomTextFiled(
                    controller: delegateAmountController,
                    hintText: "输入数量不能少于10LAT",
                  ),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      const Text(
                        "预计手续费: ",
                        style:
                            TextStyle(fontSize: 13, color: Color(0xffb6bbd0)),
                        strutStyle: StrutStyle(leading: 0.5, fontSize: 13),
                      ),
                      Text(
                        _gasFee,
                        style: const TextStyle(
                            fontSize: 13, color: Color(0xffb6bbd0)),
                        strutStyle:
                            const StrutStyle(leading: 0.5, fontSize: 13),
                      ),
                      const SizedBox(
                        width: 3,
                      ),
                      const Text(
                        "LAT",
                        style:
                            TextStyle(fontSize: 13, color: Color(0xffb6bbd0)),
                        strutStyle: StrutStyle(leading: 0.5, fontSize: 13),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  ItemTitle(title: "钱包密码"),
                  CustomTextFiled(
                    hintText: "请输入钱包密码",
                    controller: passwordController,
                  ),
                  Container(
                      margin: const EdgeInsets.only(top: 20),
                      color: Colors.transparent,
                      child: SizedBox(
                          width: double.infinity,
                          height: 44,
                          child: ShadowButton(
                            isEnable: true,
                            shadowColor: const Color(0xffdddddd),
                            borderRadius: BorderRadius.circular(44),
                            onPressed: _onWithDrawDelegate,
                            child: const Text("赎回",
                                style: TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.bold,
                                    color: Color(0xfff6f6f6))),
                          ))),
                  const SizedBox(
                    height: 20,
                  ),
                  Row(
                    children: [
                      Container(
                        width: 2,
                        height: 14,
                        decoration: const BoxDecoration(
                            gradient: LinearGradient(colors: [
                          Color(0xff1b60f3),
                          Color(0xff3b92f1)
                        ])),
                        margin: const EdgeInsets.only(right: 10),
                      ),
                      const Text(
                        "说明",
                        style: TextStyle(
                            fontSize: 14,
                            color: Colors.black,
                            fontWeight: FontWeight.bold),
                      )
                    ],
                  ),
                  const SizedBox(
                    height: 6,
                  ),
                  const Text(
                    "赎回交易提交成功，委托的Token可立即到账；\n待赎回委托必须一次性全部赎回；\n赎回数量扣除后，已委托剩余数量少于10LAT时，自动赎回全部。",
                    style: TextStyle(fontSize: 14, color: Color(0xff898c9e)),
                  ),
                  const Text(
                    "注意：操作全部赎回时，将自动领取委托奖励。",
                    style: TextStyle(fontSize: 14, color: Colors.black),
                  ),
                ],
              ),
            ),
          ),
        ),
      ],
    ));
  }
}
