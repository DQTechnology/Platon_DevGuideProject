import 'package:digging/app/dialog/claim_reward_dialog.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:flutter/material.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:get/get_navigation/src/extension_navigation.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

import '../api/platscan_api.dart';
import '../common_style/button_style.dart';
import '../entity/address_detail_resp.dart';
import '../util/address_format_util.dart';
import '../util/amount_util.dart';

class MyDelegateStatefulWidget extends StatefulWidget {
  const MyDelegateStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _MyDelegateStatefulWidgetState();
  }
}

class _MyDelegateStatefulWidgetState<MyDelegateStatefulWidget> extends State {
  final RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  void _onRefresh() async {
    // monitor network fetch
    // await Future.delayed(Duration(milliseconds: 1000));
    // if failed,use refreshFailed()
    _refreshController.refreshCompleted();
  }

  /// 总计委托
  double totalDelegate = 0;

  /// 累计奖励
  double totalHaveReward = 0;

  /// 总待领取奖励
  double totalDelegateClaim = 0;

  List<String> addressList = List.empty();
  List<AddressDetailResp> detailList = List.empty();

  @override
  void initState() {
    super.initState();

    _loadAllAddressDetailInfo();
  }

  _loadAllAddressDetailInfo() async {
    //
    List<Future<AddressDetailResp>> batchRequest = List.empty(growable: true);

    addressList = WalletManager.getAllAddress();

    for (String address in addressList) {
      batchRequest.add(PlatScanApi.getAddressDetail(address));
    }

    detailList = await Future.wait(batchRequest);

    totalDelegate = 0;
    totalHaveReward = 0;
    totalDelegateClaim = 0;

    for (AddressDetailResp addressDetailResp in detailList) {
      totalDelegate += addressDetailResp.delegateValue;
      totalHaveReward +=
          (addressDetailResp.delegateClaim + addressDetailResp.haveReward);
      totalDelegateClaim += addressDetailResp.delegateClaim;
    }

    setState(() {});
  }

  Widget _getItem(int index) {
    if (index == 0) {
      return _TotalDelegateItemStatefulWidget(
        totalDelegate: totalDelegate,
        totalDelegateClaim: totalDelegateClaim,
        totalHaveReward: totalHaveReward,
      );
    }
    String address = addressList[index - 1];
    AddressDetailResp addressDetailResp = detailList[index - 1];

    return _DelegateItemStatefulWidget(
      address: address,
      addressDetailResp: addressDetailResp,
    );
  }

  int _getItemCount() {
    if (totalDelegate == 0) {
      return 1;
    }

    return detailList.length + 1;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(left: 10, right: 10, top: 4),
      child: SmartRefresher(
        header: const ClassicHeader(),
        child: ListView.builder(
          itemBuilder: (ctx, index) {
            return _getItem(index);
          },
          itemCount: _getItemCount(),
        ),
        controller: _refreshController,
        onRefresh: _onRefresh,
      ),
    );
  }
}

/// 总我委托的item
class _TotalDelegateItemStatefulWidget extends StatelessWidget {
  /// 总计委托
  double totalDelegate = 0;

  /// 累计奖励
  double totalHaveReward = 0;

  /// 总待领取奖励
  double totalDelegateClaim = 0;

  _TotalDelegateItemStatefulWidget(
      {this.totalDelegate = 0,
      this.totalDelegateClaim = 0,
      this.totalHaveReward = 0});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 200,
      padding: const EdgeInsets.only(left: 20, right: 20, top: 20, bottom: 0),
      decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(4),
          image: const DecorationImage(
            fit: BoxFit.cover,
            image: AssetImage("images/bg_total_delegated.png"),
          )),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            "总计委托(LAT)",
            style: TextStyle(fontSize: 13, color: Colors.white),
          ),
          Container(
            margin: const EdgeInsets.only(top: 4),
            child: Text(
              AmountUtil.convertValueToLat(totalDelegate),
              style: const TextStyle(fontSize: 22, color: Colors.white),
            ),
          ),
          Container(
            margin: const EdgeInsets.only(top: 20),
            child: Row(
              children: [
                Expanded(
                    flex: 1,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "待领取奖励",
                          style: TextStyle(fontSize: 13, color: Colors.white),
                        ),
                        Container(
                          margin: const EdgeInsets.only(top: 4),
                          child: Text(
                            AmountUtil.convertValueToLat(totalDelegateClaim),
                            style: const TextStyle(
                                fontSize: 13, color: Colors.white),
                          ),
                        ),
                      ],
                    )),
                Expanded(
                    flex: 1,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "累计奖励",
                          style: TextStyle(fontSize: 13, color: Colors.white),
                        ),
                        Container(
                          margin: const EdgeInsets.only(top: 4),
                          child: Text(
                            AmountUtil.convertValueToLat(totalHaveReward),
                            style: const TextStyle(
                                fontSize: 13, color: Colors.white),
                          ),
                        ),
                      ],
                    ))
              ],
            ),
          ),
          Container(
            height: 1,
            color: Colors.white,
            margin: const EdgeInsets.only(top: 20),
          ),
          const SizedBox(
            height: 15,
          ),
          Row(
            children: [
              const Expanded(
                flex: 1,
                child: Center(
                  child: Text("参与委托",
                      style: TextStyle(fontSize: 13, color: Colors.white)),
                ),
              ),
              Container(
                width: 1,
                height: 18,
                color: Colors.white,
              ),
              const Expanded(
                flex: 1,
                child: Center(
                    child: Text("领取奖励",
                        style: TextStyle(fontSize: 13, color: Colors.white))),
              )
            ],
          )
        ],
      ),
    );
  }
}

/// 委托列表
class _DelegateItemStatefulWidget extends StatelessWidget {

  String address;

  AddressDetailResp addressDetailResp;

  _DelegateItemStatefulWidget(
      {required this.address, required this.addressDetailResp});

  /// 弹出领取奖励对话框
  _showClaimRewardDialog() {

      Get.bottomSheet(ClaimRewardDialog(
      address: address,
      delegateClaim: addressDetailResp.delegateClaim,
    ));
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        Get.toNamed("/delegatedetail", arguments: {"address": address});
      },
      child: Container(
        margin: const EdgeInsets.only(top: 14),
        padding: const EdgeInsets.only(bottom: 10),
        decoration: BoxDecoration(boxShadow: const [
          BoxShadow(
              offset: Offset(0, 0),
              blurRadius: 2,
              spreadRadius: 2,
              color: Color(0x88dddddd))
        ], color: Colors.white, borderRadius: BorderRadius.circular(4)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding:
                  const EdgeInsets.only(top: 5, bottom: 5, right: 10, left: 10),
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
                      Text(WalletManager.getWalletNameByAddress(address)),
                      const SizedBox(
                        height: 5,
                      ),
                      Text(
                        AddressFormatUtil.formatAddress(address),
                        style: const TextStyle(color: Color(0xff616e64)),
                      )
                    ],
                  ),
                  const Spacer(flex: 1),
                  const Text(
                    "详情",
                    style: TextStyle(fontSize: 13, color: Color(0xff105cfe)),
                  ),
                  const SizedBox(
                    width: 3,
                  ),
                  const Image(
                      height: 10, image: AssetImage("images/icon_right.png")),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.only(
                  top: 10, left: 10, right: 10, bottom: 14),
              child: Row(
                children: [
                  Expanded(
                      flex: 1,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text(
                            "总委托",
                            style: TextStyle(
                                fontSize: 12, color: Color(0xff898c9e)),
                          ),
                          const SizedBox(
                            height: 7,
                          ),
                          Text(
                            AmountUtil.convertValueToLat(
                                addressDetailResp.delegateValue),
                            style: const TextStyle(
                                fontSize: 14, color: Colors.black),
                          )
                        ],
                      )),
                  Expanded(
                      flex: 1,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text("累计奖励",
                              style: TextStyle(
                                  fontSize: 12, color: Color(0xff898c9e))),
                          const SizedBox(
                            height: 7,
                          ),
                          Text(
                            AmountUtil.convertValueToLat(
                                addressDetailResp.haveReward +
                                    addressDetailResp.delegateClaim),
                            style: const TextStyle(
                                fontSize: 14, color: Colors.black),
                          )
                        ],
                      ))
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.only(left: 10, right: 10, bottom: 0),
              child: Row(
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "待领取奖励",
                        style:
                            TextStyle(fontSize: 12, color: Color(0xff898c9e)),
                      ),
                      const SizedBox(
                        height: 7,
                      ),
                      Text(
                        AmountUtil.convertValueToLat(
                            addressDetailResp.delegateClaim),
                        style:
                            const TextStyle(fontSize: 14, color: Colors.black),
                      )
                    ],
                  ),
                  const Spacer(flex: 1),
                  OutlinedButton(
                      onPressed: _showClaimRewardDialog,
                      child: const Text("领取"),
                      style: getButtonStyle2()),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
