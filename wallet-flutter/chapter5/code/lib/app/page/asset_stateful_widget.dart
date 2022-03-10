import 'dart:core';
import 'package:digging/app/custom_widget/line_tab_indicator.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/sdk/protocol/Web3.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

class AssetStatefulWidget extends StatefulWidget {
  const AssetStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _AssetStatefulWidgetState();
  }
}

class _AssetData {
  String image;
  String name;
  String balance;

  _AssetData(this.image, this.name, this.balance);
}

class _AssetItem extends StatelessWidget {
  _AssetData assetData;

  int index = 0;

  _AssetItem(this.assetData, this.index);

  @override
  Widget build(BuildContext context) {
    double top = 0;
    if (index == 0) {
      top = 4;
    }
    return GestureDetector(
      onTap: () {
        Get.toNamed("/assetchain");
      },
      child: Container(
        padding: const EdgeInsets.only(left: 20, right: 20),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const Image(
                height: 38,
                width: 38,
                image: AssetImage("images/icon_platon_item_default.png")),
            Container(
              margin: const EdgeInsets.only(
                left: 20,
              ),
              child: Text(
                assetData.name,
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
            ),
            const Expanded(child: SizedBox()),
            Container(
              alignment: Alignment.centerRight,
              child: Text(
                assetData.balance,
                style: const TextStyle(color: Color(0xff999999), fontSize: 15),
              ),
            )
          ],
        ),
        margin: EdgeInsets.only(bottom: 10, left: 16, right: 16, top: top),
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
      ),
    );
  }
}

class _AssetStatefulWidgetState extends State<AssetStatefulWidget>
    with SingleTickerProviderStateMixin {
  final List<String> _tabs = ["资产"];
  late TabController _tabController;
  final RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  List<_AssetData> items = [];

  String _walletName = "--";

  String _address = "--";

  final Web3 _web3 = Web3.build("http://35.247.155.162:6789");

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: _tabs.length, vsync: this);
    _tabController.addListener(() {});
    _getCurrentWalletName();
  }

  void _onRefresh() async {
    // monitor network fetch
    // await Future.delayed(Duration(milliseconds: 1000));
    // if failed,use refreshFailed()
    _refreshController.refreshCompleted();
  }

  // void _onLoading() async {
  //   // monitor network fetch
  //   await Future.delayed(Duration(milliseconds: 1000));
  //   // if failed,use loadFailed(),if no data return,use LoadNodata()
  //   // items.add((items.length + 1).toString());
  //   if (mounted) setState(() {});
  //   _refreshController.loadComplete();
  // }

  void _getCurrentWalletName() async {
    _walletName = await WalletManager.getCurrentWalletName();
    _address = await WalletManager.getWalletAddress(_walletName);
    setState(() {});

    BigInt balance = await _web3.getBalance(_address);

    setState(() {
      items.add(_AssetData("images/icon_platon_item_default.png", "LAT",
          AmountUtil.convertVonToLat(balance)));
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        color: const Color(0xff03081E),
        child: Flex(
          direction: Axis.vertical,
          children: [
            Container(
              width: double.infinity,
              height: 250,
              child: SafeArea(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      _walletName,
                      style: const TextStyle(color: Colors.white, fontSize: 22),
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    Text(
                      _address,
                      style: const TextStyle(color: Colors.white, fontSize: 14),
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
                          child: TabBar(
                              controller: _tabController,
                              unselectedLabelColor: const Color(0xff898C9C),
                              indicatorSize: TabBarIndicatorSize.label,
                              isScrollable: true,
                              labelStyle: const TextStyle(
                                  fontSize: 16, fontWeight: FontWeight.bold),
                              unselectedLabelStyle: const TextStyle(
                                fontSize: 16,
                              ),
                              indicator: const LineTabIndicator(
                                  insets: EdgeInsets.only(left: 2, right: 15),
                                  borderSide: BorderSide(
                                      width: 3.5, color: Color(0xff105CFF))),
                              labelColor: const Color(0xff000000),
                              tabs: List.generate(_tabs.length, (index) {
                                String tabName = _tabs[index];
                                return Tab(
                                  height: 32,
                                  child: Align(
                                    alignment: Alignment.centerLeft,
                                    child: Text(tabName),
                                  ),
                                );
                              })),
                        ),
                        Expanded(
                            flex: 1,
                            child: Container(
                              margin: const EdgeInsets.only(top: 6),
                              child: SmartRefresher(
                                header: const ClassicHeader(),
                                child: ListView.builder(
                                  itemBuilder: (c, i) => _AssetItem(
                                    items[i],
                                    i,
                                  ),
                                  itemExtent: 80.0,
                                  itemCount: items.length,
                                ),
                                controller: _refreshController,
                                onRefresh: _onRefresh,
                              ),
                            ))
                      ],
                    )))
          ],
        ));
  }
}
