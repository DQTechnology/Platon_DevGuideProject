import 'package:digging/app/api/platscan_api.dart';
import 'package:digging/app/common_style/button_style.dart';
import 'package:digging/app/entity/resp_page.dart';
import 'package:digging/app/entity/transaction_list_param.dart';
import 'package:digging/app/entity/transaction_list_resp.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/app/util/time_util.dart';
import 'package:digging/app/util/transaction_type.dart';
import 'package:digging/sdk/protocol/Web3.dart';
import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:get/get.dart';
class AssetChainStatefulWidget extends StatefulWidget {
  const AssetChainStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _AssetChainStatefulWidgetState();
  }
}

class _AssetItem extends StatelessWidget {
  TransactionListResp rsp;
  int index = 0;

  String address;

  _AssetItem(this.rsp, this.index, this.address);

  @override
  Widget build(BuildContext context) {
    double top = 0;
    if (index == 0) {
      top = 4;
    }

    bool isSender = address == rsp.from;

    String image = getTransactionImage(isSender, rsp.txType);

    String txTypeDesc = getTransactionType(isSender, rsp.txType);

    Color latColor =
        getTransactionTypeColor(isSender, rsp.txReceiptStatus, rsp.txType);

    String balance = AmountUtil.convertValueToLat(rsp.value);

    if (((rsp.txType == 0) && isSender) || rsp.txType == 1004) {
      balance = "-" + balance;
    } else if ((rsp.txType == 0) && !isSender) {
      balance = "+" + balance;
    }

    return Container(
      padding: const EdgeInsets.only(left: 20, right: 20),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Image(height: 24, width: 24, image: AssetImage(image)),
          Container(
            margin: const EdgeInsets.only(
              left: 20,
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  txTypeDesc,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                const SizedBox(
                  height: 4,
                ),
                Text(
                  TimeUtil.timestampToString(rsp.timestamp),
                  style:
                      const TextStyle(fontSize: 13, color: Color(0xff999999)),
                ),
              ],
            ),
          ),
          const Expanded(child: SizedBox()),
          Container(
            alignment: Alignment.centerRight,
            child: Text(
              balance,
              style:  TextStyle(color: latColor, fontSize: 15),
            ),
          )
        ],
      ),
      margin: EdgeInsets.only(bottom: 10, left: 2, right: 2, top: top),
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(13),
          boxShadow: const [
            BoxShadow(
                offset: Offset(0, 0),
                blurRadius: 2,
                spreadRadius: 1,
                color: Color(0x88dddddd))
          ]),
    );
  }
}

class _AssetChainStatefulWidgetState extends State<AssetChainStatefulWidget> {
  final Web3 _web3 = Web3.build("http://35.247.155.162:6789");

  String _balance = "0.00";

  String _address = "";

  final List<TransactionListResp> _txList = [];

  final RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  void _onRefresh() async {
    await _loadTransactionList();
    _refreshController.refreshCompleted();
  }

  @override
  void initState() {
    super.initState();
    _loadBalance();
  }

  void _loadBalance() async {
    String walletName = await WalletManager.getCurrentWalletName();
    _address = await WalletManager.getWalletAddress(walletName);
    BigInt balance = await _web3.getBalance(_address);
    setState(() {
      _balance = AmountUtil.convertVonToLat(balance);
    });

    _loadTransactionList();
  }

  Future<void> _loadTransactionList() async {
    TransactionListParam param = TransactionListParam(_address, 1, 20);

    RespPage<TransactionListResp> respPage =
        await PlatScanApi.getTransactionListByAddress(param);

    if (respPage.data.isEmpty) {
      return;
    }

    setState(() {
      _txList.addAll(respPage.data);
    });
  }

  _AssetItem createAssetItem(BuildContext context, int index) {
    return _AssetItem(_txList[index], index, _address);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: const Color(0xff03081E),
        child: Column(
          children: [
            Container(
              width: double.infinity,
              height: 250,
              child: SafeArea(
                child: Column(
                  children: [
                    AppBar(
                      backgroundColor: Colors.transparent,
                      elevation: 0,
                      title: const Text(
                        "LAT",
                        style: TextStyle(fontSize: 18),
                      ),
                      centerTitle: true,
                    ),
                    const SizedBox(
                      height: 24,
                    ),
                    Text(
                      _balance,
                      style: const TextStyle(color: Colors.white, fontSize: 24),
                    ),
                    const SizedBox(
                      height: 20,
                    ),
                    Center(
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          OutlinedButton(
                              onPressed: () {
                                Get.toNamed("/sendlat");
                              },
                              child: const Text("发送"),
                              style: getButtonStyle()),
                          const SizedBox(
                            width: 16,
                          ),
                          OutlinedButton(
                              onPressed: () {},
                              child: const Text("接收"),
                              style: getButtonStyle())
                        ],
                      ),
                    )
                  ],
                ),
              ),
              decoration: const BoxDecoration(
                  image: DecorationImage(
                      fit: BoxFit.cover,
                      image: ExactAssetImage("images/bg_assets_top2.png"))),
            ),
            Expanded(
                flex: 1,
                child: Container(
                    padding:
                        const EdgeInsets.only(left: 10, top: 14, right: 16),
                    decoration: const BoxDecoration(
                        color: Color(0xfff2f5fa),
                        borderRadius: BorderRadius.only(
                            topLeft: Radius.circular(16),
                            topRight: Radius.circular(16))),
                    child: Column(
                      children: [
                        Container(
                          alignment: Alignment.topLeft,
                          margin: const EdgeInsets.only(left: 4),
                          child: const Text(
                            "交易记录",
                            style: TextStyle(
                                fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                        ),
                        Expanded(
                            flex: 1,
                            child: Container(
                              margin: const EdgeInsets.only(top: 6),
                              child: SmartRefresher(
                                header: const ClassicHeader(),
                                child: ListView.builder(
                                  itemBuilder: createAssetItem,
                                  itemExtent: 80.0,
                                  itemCount: _txList.length,
                                ),
                                controller: _refreshController,
                                onRefresh: _onRefresh,
                              ),
                            ))
                      ],
                    )))
          ],
        ),
      ),
    );
  }
}
