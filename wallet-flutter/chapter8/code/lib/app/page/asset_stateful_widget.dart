import 'dart:core';
import 'package:digging/app/custom_widget/line_tab_indicator.dart';
import 'package:digging/app/service/network_manager.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/sdk/protocol/web3.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

import '../custom_widget/custom_popup_menu_item.dart';

class AssetPageController {
  VoidCallback? networkSwitchListener;

  VoidCallback? switchAddressListener;

  addNetworkSwitchListener(VoidCallback listener) {
    networkSwitchListener = listener;
  }

  addAddressSwitchListener(VoidCallback listener) {
    switchAddressListener = listener;
  }
}

class AssetStatefulWidget extends StatefulWidget {
  AssetPageController? controller;

  AssetStatefulWidget({Key? key, this.controller}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return AssetStatefulWidgetState();
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

class AssetStatefulWidgetState extends State<AssetStatefulWidget>
    with SingleTickerProviderStateMixin {
  final List<String> _tabs = ["资产"];
  late TabController _tabController;

  final RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  List<_AssetData> items = [];

  String _walletName = "--";

  String _address = "--";

  final GlobalKey _showImportPopupKey = GlobalKey();

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: _tabs.length, vsync: this);
    _tabController.addListener(() {});
    _getCurrentWalletName();
  }

  /// 更新状态
  updateStatue() {
    items.clear();
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

    Web3 web3 = NetworkManager.getWeb3();

    BigInt balance = await web3.getBalance(_address);

    setState(() {
      items.add(_AssetData("images/icon_platon_item_default.png", "LAT",
          AmountUtil.convertVonToLat(balance)));
    });
  }

  _onJumpToSelectNetworkPage() async {
    await Get.toNamed("/networksetting");

    setState(() {
      if (widget.controller != null &&
          widget.controller!.networkSwitchListener != null) {
        widget.controller!.networkSwitchListener!();
      }
    });
  }

  /// 显示弹出对话框
  _onShowImportPopupMenu() {
    RenderBox? renderBox =
        _showImportPopupKey.currentContext?.findRenderObject() as RenderBox?;

    final buttonWidth = renderBox?.size.width;
    final buttonHeight = renderBox?.size.height;

    Offset? positioned = renderBox?.localToGlobal(Offset.zero);

    double left = 0;

    double top = 0;
    if (buttonWidth != 0 && positioned != null) {
      top = buttonHeight! + positioned.dy + 10;
    }
    if (buttonWidth != 0 && positioned != null) {
      left = positioned.dx - buttonWidth! - 16; // 16是边距
    }

    showMenu(
        useRootNavigator: true,
        context: context,
        position: RelativeRect.fromLTRB(left, top, left, 0),
        items: <PopupMenuItem<String>>[
          CustomPopupMenuItem<String>(
            padding: const EdgeInsets.all(0),
            height: 40,
            onTap: () {
              Get.toNamed("/createwallet");
            },
            child: Row(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(
                  width: 10,
                ),
                const Image(
                    width: 20,
                    height: 20,
                    image: AssetImage("images/icon_more_classic_create.png")),
                const SizedBox(
                  width: 4,
                ),
                Container(
                  child: const Text(
                    "创建钱包",
                    style: TextStyle(fontSize: 14),
                  ),
                  height: 40,
                  alignment: Alignment.center,
                  decoration: const BoxDecoration(
                      // color: Colors.red,
                      border: Border(
                          bottom: BorderSide(
                              width: 0.5, color: Color(0xffebeef4)))),
                )
              ],
            ),
          ),
          CustomPopupMenuItem(
            height: 40,
            padding: const EdgeInsets.all(0),
            onTap: () {
              Get.toNamed("/importwallet");
            },
            child: Row(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(
                  width: 10,
                ),
                const Image(
                    width: 20,
                    height: 20,
                    image: AssetImage("images/icon_more_classic_import.png")),
                const SizedBox(
                  width: 4,
                ),
                Container(
                  child: const Text(
                    "导入钱包",
                    style: TextStyle(fontSize: 14),
                  ),
                  alignment: Alignment.center,
                )
              ],
            ),
          )
        ]);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        color: NetworkManager.getCurNetworkInfo().themeColor,
        child: Flex(
          direction: Axis.vertical,
          children: [
            Container(
              width: double.infinity,
              height: 250,
              child: SafeArea(
                child: Column(
                  children: [
                    Row(
                      children: [
                        Container(
                          height: 40,
                          alignment: Alignment.centerLeft,
                          margin: const EdgeInsets.only(left: 16, right: 16),
                          child: GestureDetector(
                            onTap: _onJumpToSelectNetworkPage,
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text(
                                  NetworkManager.getCurNetworkInfo()
                                      .netName, // 网络名
                                  style: const TextStyle(
                                      color: Colors.white,
                                      fontSize: 18,
                                      fontWeight: FontWeight.bold),
                                ),
                                const SizedBox(
                                  width: 6,
                                ),
                                const Image(
                                    width: 14,
                                    height: 14,
                                    image: AssetImage(
                                        "images/icon_node_seting_white.png"))
                              ],
                            ),
                          ),
                        ),
                        const Spacer(
                          flex: 1,
                        ),
                        GestureDetector(
                            // 显示导入账号对话框
                            key: _showImportPopupKey,
                            onTap: _onShowImportPopupMenu,
                            child: Container(
                              padding:
                                  const EdgeInsets.only(left: 10, right: 10),
                              width: 40,
                              height: 40,
                              margin: const EdgeInsets.only(right: 16),
                              child: const Image(
                                fit: BoxFit.fitWidth,
                                width: 20,
                                height: 2,
                                image: AssetImage("images/icon_add_white.png"),
                              ),
                            ))
                      ],
                    ),
                    const SizedBox(
                      height: 40,
                    ),
                    GestureDetector(
                      // 点击切换账号
                      onTap: () {
                        if (widget.controller != null &&
                            widget.controller!.switchAddressListener != null) {
                          widget.controller!.switchAddressListener!();
                        }
                      },
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            _walletName, // 钱包名
                            style: const TextStyle(
                                color: Colors.white, fontSize: 22),
                          ),
                          const Image(
                              width: 24,
                              height: 24,
                              image: AssetImage("images/icon_switch.png"))
                        ],
                      ),
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
