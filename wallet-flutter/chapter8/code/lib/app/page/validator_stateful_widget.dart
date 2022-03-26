import 'package:digging/app/util/staking_status.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import '../api/platscan_api.dart';
import '../entity/alive_staking_list_req.dart';
import '../entity/alive_staking_list_resp.dart';
import '../entity/resp_page.dart';

class ValidatorStatefulWidget extends StatefulWidget {
  const ValidatorStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ValidatorStatefulWidgetState();
  }
}

class _ValidatorStatefulWidgetState<ValidatorStatefulWidget> extends State {
  final RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  void _onRefresh() async {
    // monitor network fetch
    // await Future.delayed(Duration(milliseconds: 1000));
    // if failed,use refreshFailed()
    _refreshController.refreshCompleted();
  }

  List<AliveStakingListResp> aliveStakingList = List.empty();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    _loadAliveStakingList();
  }

  _loadAliveStakingList() async {
    AliveStakingListReq req = AliveStakingListReq(1, 300);

    RespPage<AliveStakingListResp> respPage =
        await PlatScanApi.getAliveStakingList(req);
    aliveStakingList = respPage.data;

    if (mounted) {
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(left: 10, right: 10, top: 4),
      child: SmartRefresher(
        controller: _refreshController,
        header: const ClassicHeader(),
        onRefresh: _onRefresh,
        child: ListView.builder(
          itemBuilder: (ctx, index) {
            return GestureDetector(
              onTap: () {
                AliveStakingListResp aliveStaking = aliveStakingList[index];
                Get.toNamed("/senddelegate", arguments: {
                  "nodeName": aliveStaking.nodeName,
                  "nodeId": aliveStaking.nodeId,
                  "stakingIcon": aliveStaking.stakingIcon,
                });
              },
              child: _NodeItemStatelessWidget(aliveStakingList[index]),
            );
          },
          itemExtent: 80,
          itemCount: aliveStakingList.length,
        ),
      ),
    );
  }
}

class _NodeItemStatelessWidget extends StatelessWidget {
  AliveStakingListResp aliveStaking;

  _NodeItemStatelessWidget(this.aliveStaking);

  ImageProvider _loadNodeImage(String iconUrl) {
    if (iconUrl.isNotEmpty) {
      return NetworkImage(iconUrl);
    }
    return const AssetImage("images/icon_validators_default.png");
  }

  AssetImage _getRankBkImage() {
    if (aliveStaking.ranking == 1) {
      return const AssetImage("images/icon_rank_first.png");
    } else if (aliveStaking.ranking == 2) {
      return const AssetImage("images/icon_rank_second.png");
    } else if (aliveStaking.ranking == 3) {
      return const AssetImage("images/icon_rank_third.png");
    }
    return const AssetImage("images/icon_rank_others.png");
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Container(
          margin: const EdgeInsets.all(8),
          padding: const EdgeInsets.only(left: 8, top: 8, bottom: 8, right: 20),
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(4),
              color: Colors.white,
              boxShadow: const [
                BoxShadow(
                    offset: Offset(0, 0),
                    blurRadius: 2,
                    spreadRadius: 2,
                    color: Color(0x88dddddd))
              ]),
          child: Row(
            children: [
              Container(
                width: 42,
                height: 42,
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(42),
                    image: DecorationImage(
                        image: _loadNodeImage(aliveStaking.stakingIcon))),
              ),
              const SizedBox(
                width: 6,
              ),
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        aliveStaking.nodeName,
                        style:
                            const TextStyle(fontSize: 14, color: Colors.black),
                      ),
                      Container(
                        margin: const EdgeInsets.only(left: 6),
                        padding: const EdgeInsets.only(
                            left: 8, right: 8, top: 0, bottom: 0),
                        alignment: Alignment.center,
                        decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(3),
                            border: Border.all(
                                width: 1,
                                color: StakingStatus.getNodeStatusColor(
                                    aliveStaking.status))),
                        child: Text(
                          StakingStatus.getNodeStatusText(aliveStaking.status),
                          style: TextStyle(
                              color: StakingStatus.getNodeStatusColor(
                                  aliveStaking.status),
                              fontSize: 10),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: 10,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const Text(
                        "委托",
                        textAlign: TextAlign.right,
                        style: TextStyle(
                            fontSize: 13, color: Color(0xff61646e), height: 1),
                      ),
                      const SizedBox(
                        width: 6,
                      ),
                      Text(
                        "${aliveStaking.delegateValue} LAT/ ${aliveStaking.delegateQty}",
                        style: const TextStyle(
                            fontSize: 13, color: Colors.black, height: 1),
                      )
                    ],
                  ),
                ],
              ),
              const Spacer(
                flex: 1,
              ),
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    aliveStaking.deleAnnualizedRate,
                    style:
                        const TextStyle(fontSize: 17, color: Color(0xff105cfe)),
                  ),
                  const Text(
                    "预计收益率",
                    style: TextStyle(fontSize: 11, color: Color(0xff898c9e)),
                  )
                ],
              ),
            ],
          ),
        ),
        Positioned(
            right: 8,
            top: 8,
            child: Container(
              alignment: Alignment.center,
              padding: const EdgeInsets.only(left: 3, bottom: 2),
              width: 20,
              height: 20,
              decoration: BoxDecoration(
                  image: DecorationImage(
                image: _getRankBkImage(),
              )),
              child: Text(
                aliveStaking.ranking.toString(),
                style: const TextStyle(fontSize: 11, color: Colors.white),
              ),
            )),
      ],
    );
  }
}
