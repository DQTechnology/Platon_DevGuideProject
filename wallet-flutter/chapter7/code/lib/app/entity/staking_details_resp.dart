class StakingDetailsResp {
  String nodeName = ""; //验证人名称

  String stakingIcon = ""; //验证人图标

  int status = 0;

  static StakingDetailsResp fromMap(Map<String, dynamic> jsonMap) {
    StakingDetailsResp detailResp = StakingDetailsResp();
    detailResp.nodeName = jsonMap["nodeName"];
    detailResp.stakingIcon = jsonMap["stakingIcon"];
    detailResp.status = jsonMap["status"];
    return detailResp;
  }
}
