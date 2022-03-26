class DelegationIdInfo {
  String address = "";

  String nodeId = "";

  BigInt stakingBlockNum = BigInt.zero;

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
