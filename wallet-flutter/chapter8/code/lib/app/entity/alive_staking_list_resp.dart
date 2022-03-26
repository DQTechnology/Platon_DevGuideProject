class AliveStakingListResp {
  int ranking = 0; // 排行

  String nodeId = ""; //节点Id

  String nodeName = ""; // 验证人名

  String stakingIcon = ""; // 验证人图标

  int status = 0; // 状态 1：候选中 2：活跃中 3 出块中 6 共识中

  String totalValue = "0.00"; // 质押总数 = 有效的质押 + 委托

  String delegateValue = "0"; //委托总数

  int delegateQty = 0; //委托人数

  int slashLowQty = 0; // 低出块率举报次数

  BigInt blockQty = BigInt.zero; // 产生的区块数

  String expectedIncome = "0.00%"; //预计年化率(从验证人加入时刻开始计算)

  String deleAnnualizedRate = "0.00%"; // 预计委托年化率（从验证人加入时刻开始计算）

  String delegatedRewardRatio = "0.00%"; //委托奖励比例

  String genBlocksRate = "0.00%"; // 出块率

  String version = "1"; //版本

  static List<AliveStakingListResp> fromList(List<dynamic> dataList) {
    List<AliveStakingListResp> aliveStakingList = List.empty(growable: true);

    for (Map<String, dynamic> jsonMap in dataList) {
      AliveStakingListResp aliveStakingListResp = fromMap(jsonMap);

      aliveStakingList.add(aliveStakingListResp);
    }

    return aliveStakingList;
  }

  static AliveStakingListResp fromMap(Map<String, dynamic> jsonMap) {
    AliveStakingListResp aliveStakingListResp = AliveStakingListResp();
    aliveStakingListResp.ranking = jsonMap["ranking"];
    aliveStakingListResp.nodeId = jsonMap["nodeId"];
    aliveStakingListResp.nodeName = jsonMap["nodeName"];
    aliveStakingListResp.stakingIcon = jsonMap["stakingIcon"];
    aliveStakingListResp.status = jsonMap["status"];

    aliveStakingListResp.totalValue = jsonMap["totalValue"];
    aliveStakingListResp.delegateValue = jsonMap["delegateValue"];

    aliveStakingListResp.delegateQty = jsonMap["delegateQty"];
    aliveStakingListResp.slashLowQty = jsonMap["slashLowQty"];

    aliveStakingListResp.blockQty = BigInt.from(jsonMap["blockQty"]);

    aliveStakingListResp.expectedIncome = jsonMap["expectedIncome"];

    aliveStakingListResp.deleAnnualizedRate = jsonMap["deleAnnualizedRate"];
    aliveStakingListResp.delegatedRewardRatio = jsonMap["delegatedRewardRatio"];
    aliveStakingListResp.genBlocksRate = jsonMap["genBlocksRate"];
    aliveStakingListResp.version = jsonMap["version"];
    return aliveStakingListResp;
  }
}
