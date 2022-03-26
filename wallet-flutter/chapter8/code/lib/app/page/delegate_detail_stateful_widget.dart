import 'package:digging/app/api/platscan_api.dart';
import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/entity/address_detail_resp.dart';
import 'package:digging/app/entity/delegation_list_by_address_req.dart';
import 'package:digging/app/entity/delegation_list_by_address_resp.dart';
import 'package:digging/app/entity/resp_page.dart';
import 'package:digging/app/entity/staking_details_resp.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/address_format_util.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:digging/app/util/staking_status.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:get/get_navigation/src/extension_navigation.dart';

class DelegateDetailStatefulWidget extends StatefulWidget {
  const DelegateDetailStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _DelegateDetailStatefulWidgetState();
  }
}

class _DelegateDetailStatefulWidgetState<DelegateDetailStatefulWidget>
    extends State {
  late String _address;
  AddressDetailResp _detailResp = AddressDetailResp();

  final List<DelegationListByAddressResp> _delegateList =
      List.empty(growable: true);

  @override
  void initState() {
    super.initState();

    final arguments = Get.arguments as Map;
    _address = arguments["address"];

    _loadAddressInfo();
  }

  _loadAddressInfo() async {
    _detailResp = await PlatScanApi.getAddressDetail(_address);

    DelegationListByAddressReq delegationListByAddressReq =
        DelegationListByAddressReq(1, 20, _address);

    RespPage<DelegationListByAddressResp> detailRsp =
        await PlatScanApi.getDelegationListByAddress(
            delegationListByAddressReq);

    _delegateList.addAll(detailRsp.data);

    List<Future<StakingDetailsResp>> batchRequest = List.empty(growable: true);
    for (DelegationListByAddressResp delegate in _delegateList) {
      batchRequest.add(PlatScanApi.getStakingDetails(delegate.nodeId));
    }

    List<StakingDetailsResp> stakingDetailList =
        await Future.wait(batchRequest);

    for (int i = 0; i < _delegateList.length; ++i) {
      DelegationListByAddressResp delegationListByAddressResp =
          _delegateList[i];

      StakingDetailsResp stakingDetailsResp = stakingDetailList[i];

      delegationListByAddressResp.stakingIcon = stakingDetailsResp.stakingIcon;

      delegationListByAddressResp.status = stakingDetailsResp.status;
    }

    setState(() {});
  }

  Widget _getItem(int index) {
    if (index == 0) {
      return _AddressItemStatelessWidget(_address, _detailResp);
    }

    double bottom = 0;

    if (index == _delegateList.length) {
      bottom = 10;
    }

    return _DelegateItemStatelessWidget(_delegateList[index - 1], bottom);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Colors.white,
        child: Column(
          children: [
            PageHeader(
              title: "委托节点详情",
            ),
            Expanded(
                flex: 1,
                child: MediaQuery.removePadding(
                    removeTop: true,
                    context: context,
                    child: ListView.builder(
                      itemBuilder: (ctx, index) {
                        return _getItem(index);
                      },
                      itemCount: _delegateList.length + 1,
                    )))
          ],
        ),
      ),
    );
  }
}

class _AddressItemStatelessWidget extends StatelessWidget {
  final String _address;
  final AddressDetailResp _detailResp;

  const _AddressItemStatelessWidget(this._address, this._detailResp);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 150,
      padding: const EdgeInsets.only(left: 10, right: 10, top: 20),
      margin: const EdgeInsets.only(left: 10, top: 10, right: 10),
      decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(4),
          image: const DecorationImage(
            fit: BoxFit.cover,
            image: AssetImage("images/bg_validators_detail.png"),
          )),
      child: Column(
        children: [
          Row(
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
                  Text(
                    WalletManager.getWalletNameByAddress(_address),
                    style: const TextStyle(color: Colors.white),
                  ),
                  const SizedBox(
                    height: 5,
                  ),
                  Text(
                    AddressFormatUtil.formatAddress(_address),
                    style: const TextStyle(color: Colors.white),
                  )
                ],
              ),
            ],
          ),
          Container(
            margin: const EdgeInsets.only(left: 8, top: 30),
            child: Row(
              children: [
                Expanded(
                    flex: 1,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "可委托余额",
                          style: TextStyle(fontSize: 12, color: Colors.white),
                        ),
                        const SizedBox(
                          height: 7,
                        ),
                        Text(
                          AmountUtil.convertValueToLat(_detailResp.balance),
                          style: const TextStyle(
                              fontSize: 14, color: Colors.white),
                        )
                      ],
                    )),
                Expanded(
                  flex: 1,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "总委托",
                        style: TextStyle(fontSize: 12, color: Colors.white),
                      ),
                      const SizedBox(
                        height: 7,
                      ),
                      Text(
                        AmountUtil.convertValueToLat(_detailResp.stakingValue),
                        style:
                            const TextStyle(fontSize: 14, color: Colors.white),
                      )
                    ],
                  ),
                )
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _DelegateItemStatelessWidget extends StatelessWidget {
  DelegationListByAddressResp _delegateInfo;

  double _bottom = 0;

  _DelegateItemStatelessWidget(this._delegateInfo, this._bottom);

  ImageProvider _loadNodeImage() {
    if (_delegateInfo.stakingIcon.isNotEmpty) {
      return NetworkImage(_delegateInfo.stakingIcon);
    }

    return const AssetImage("images/icon_validators_default.png");
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(left: 10, top: 10, right: 10, bottom: _bottom),
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
                const EdgeInsets.only(top: 8, bottom: 5, right: 10, left: 10),
            decoration: const BoxDecoration(
                image: DecorationImage(
                    fit: BoxFit.cover,
                    image: ExactAssetImage(
                        "images/icon_my_delegate_item_bg.png"))),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  width: 42,
                  height: 42,
                  decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(42),
                      image: DecorationImage(image: _loadNodeImage())),
                ),
                const SizedBox(
                  width: 10,
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(_delegateInfo.nodeName),
                    const SizedBox(
                      height: 5,
                    ),
                    Text(
                      AddressFormatUtil.formatAddress(_delegateInfo.nodeId),
                      style: const TextStyle(
                          color: Color(0xff616e64), fontSize: 12),
                    )
                  ],
                ),
                const Spacer(
                  flex: 1,
                ),
                Text(
                  StakingStatus.getNodeStatusText(_delegateInfo.status),
                  style: TextStyle(
                      color: StakingStatus.getNodeStatusColor(
                          _delegateInfo.status),
                      fontSize: 12),
                )
              ],
            ),
          ),
          Container(
              margin: const EdgeInsets.only(top: 14),
              padding: const EdgeInsets.only(left: 10, right: 10),
              child: Row(
                children: [
                  Expanded(
                      flex: 1,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text(
                            "已委托",
                            style: TextStyle(
                                fontSize: 12, color: Color(0xff898c9e)),
                          ),
                          const SizedBox(
                            height: 7,
                          ),
                          Text(
                            AmountUtil.convertValueToLat(
                                _delegateInfo.delegateValue),
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
                        const Text(
                          "待赎回委托",
                          style:
                              TextStyle(fontSize: 12, color: Color(0xff898c9e)),
                        ),
                        const SizedBox(
                          height: 7,
                        ),
                        Text(
                          AmountUtil.convertValueToLat(
                              _delegateInfo.delegateReleased),
                          style: const TextStyle(
                              fontSize: 14, color: Colors.black),
                        )
                      ],
                    ),
                  )
                ],
              )),
          Container(
            margin: const EdgeInsets.only(top: 15, left: 10, right: 10),
            padding: const EdgeInsets.only(left: 10, right: 10),
            height: 40,
            color: const Color(0xffecf2ff),
            child: Row(
              children: [
                const Image(
                    width: 20,
                    height: 20,
                    image: AssetImage("images/icon_claim_reward_blue.png")),
                const SizedBox(
                  width: 4,
                ),
                const Text(
                  "可领取奖励",
                  style: TextStyle(fontSize: 11, color: Color(0xff898c9e)),
                  strutStyle: StrutStyle(
                    forceStrutHeight: true,
                    leading: 0.5,
                    fontSize: 11,
                  ),
                ),
                const SizedBox(
                  width: 4,
                ),
                Text(
                  AmountUtil.convertValueToLat(_delegateInfo.delegateClaim),
                  style: const TextStyle(fontSize: 16, color: Colors.black),
                  strutStyle: const StrutStyle(
                      forceStrutHeight: true, leading: 0.5, fontSize: 16),
                )
              ],
            ),
          ),
          Container(
            padding: EdgeInsets.only(top: 10),
            margin: const EdgeInsets.only(top: 10, left: 10, right: 10),
            decoration: const BoxDecoration(
                border: Border(
                    top: BorderSide(width: 0.5, color: Color(0xffe4e7f3)))),
            child: Row(
              children: [
                Expanded(
                  flex: 1,
                  child: GestureDetector(
                    onTap: () {
                      Get.toNamed("/senddelegate", arguments: {
                        "nodeName": _delegateInfo.nodeName,
                        "nodeId": _delegateInfo.nodeId,
                        "stakingIcon": _delegateInfo.stakingIcon,
                      });
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: const [
                        Image(
                            width: 20,
                            height: 20,
                            image:
                                AssetImage("images/icon_detail_delegate.png")),
                        SizedBox(
                          width: 4,
                        ),
                        Text(
                          "委托",
                          style: TextStyle(
                              fontSize: 14,
                              color: Colors.black,
                              fontWeight: FontWeight.bold),
                          textAlign: TextAlign.center,
                        )
                      ],
                    ),
                  ),
                  // child:
                ),
                Container(
                  height: 14,
                  width: 0.5,
                  color: const Color(0x66dcdfe8),
                ),
                Expanded(
                    flex: 1,
                    child: GestureDetector(
                      onTap: () {
                        Get.toNamed("/withdrawdelegate", arguments: {
                          "nodeName": _delegateInfo.nodeName,
                          "nodeId": _delegateInfo.nodeId,
                          "stakingIcon": _delegateInfo.stakingIcon,
                          "delegateValue": _delegateInfo.delegateValue
                        });
                      },
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: const [
                          Image(
                              width: 20,
                              height: 20,
                              image: AssetImage(
                                  "images/icon_detail_undelegate.png")),
                          SizedBox(
                            width: 4,
                          ),
                          Text("赎回委托",
                              style: TextStyle(
                                  fontSize: 14,
                                  color: Colors.black,
                                  fontWeight: FontWeight.bold),
                              textAlign: TextAlign.center)
                        ],
                      ),
                    ))
              ],
            ),
          )
        ],
      ),
    );
  }
}
