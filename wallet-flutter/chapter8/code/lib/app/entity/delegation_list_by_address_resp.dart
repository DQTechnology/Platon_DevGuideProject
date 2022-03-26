class DelegationListByAddressResp {
  String nodeId = ""; //节点id
  String nodeName = ""; //节点名称
  String stakingIcon = ""; //节点图标
  int status = 0; // 节点状态
  double delegateValue = 0; //委托数量
  double delegateHas = 0; //未锁定委托（ATP）
  double delegateLocked = 0; //已锁定委托（ATP）
  double delegateUnlock = 0; //已解除委托（ATP）
  double delegateReleased = 0; //赎回中委托（ATP）
  double delegateClaim = 0; //待提取委托（ATP）

  static List<DelegationListByAddressResp> fromList(List<dynamic> dataList) {
    List<DelegationListByAddressResp> rspList = List.empty(growable: true);

    for (Map<String, dynamic> jsonMap in dataList) {
      DelegationListByAddressResp rsp = fromMap(jsonMap);
      rspList.add(rsp);
    }

    return rspList;
  }

  static DelegationListByAddressResp fromMap(Map<String, dynamic> jsonMap) {
    DelegationListByAddressResp rsp = DelegationListByAddressResp();

    rsp.nodeId = jsonMap["nodeId"];
    rsp.nodeName = jsonMap["nodeName"];
    rsp.delegateValue = double.parse(jsonMap["delegateValue"]);
    rsp.delegateHas = double.parse(jsonMap["delegateHas"]);
    rsp.delegateLocked = double.parse(jsonMap["delegateLocked"]);
    rsp.delegateUnlock = double.parse(jsonMap["delegateUnlock"]);

    rsp.delegateReleased = double.parse(jsonMap["delegateReleased"]);
    rsp.delegateReleased = double.parse(jsonMap["delegateReleased"]);
    rsp.delegateClaim = double.parse(jsonMap["delegateClaim"]);

    return rsp;
  }
}
