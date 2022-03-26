class DelegationIdInfo {
  String address = ""; /// 钱包地址

  String nodeId = ""; /// 节点id

  BigInt stakingBlockNum = BigInt.zero; /// 委托时的块高

  static List<DelegationIdInfo> fromList(List<dynamic> dataList) {
    List<DelegationIdInfo> delegateIdInfoList = List.empty(growable: true);

    for (Map<String, dynamic> jsonMap in dataList) {
      DelegationIdInfo delegationIdInfo = fromMap(jsonMap);

      delegateIdInfoList.add(delegationIdInfo);
    }

    return delegateIdInfoList;
  }

  static DelegationIdInfo fromMap(Map<String, dynamic> jsonMap) {
    DelegationIdInfo delegationIdInfo = DelegationIdInfo();
    delegationIdInfo.address = jsonMap["Addr"];
    delegationIdInfo.nodeId = jsonMap["NodeId"];
    delegationIdInfo.stakingBlockNum = BigInt.from(jsonMap["StakingBlockNum"]);
    return delegationIdInfo;
  }
}
