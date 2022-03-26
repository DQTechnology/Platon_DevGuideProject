class AddressDetailResp {
  double balance = 0; // 余额（单位：LAT）
  double restrictingBalance = 0; // 锁仓余额（单位：LAT）
  double stakingValue = 0; // 质押的金额
  double delegateValue = 0; // 委托的金额
  double redeemedValue = 0; // 赎回中的金额
  double haveReward = 0; // 已提取委托（单位：LAT）
  double delegateClaim = 0; //待领取的委托奖励（单位：LAT）
  double delegateReleased = 0; //待赎回委托（单位：LAT）


  static  AddressDetailResp fromMap(Map<String, dynamic> jsonMap) {
    AddressDetailResp detailResp = AddressDetailResp();
    detailResp.balance = double.parse(jsonMap["balance"]);
    detailResp.restrictingBalance = double.parse(jsonMap["restrictingBalance"]);
    detailResp.stakingValue = double.parse(jsonMap["stakingValue"]);
    detailResp.delegateValue = double.parse(jsonMap["delegateValue"]);
    detailResp.redeemedValue = double.parse(jsonMap["redeemedValue"]);
    detailResp.haveReward = double.parse(jsonMap["haveReward"]);

    if(jsonMap["delegateClaim"] != null) {
      detailResp.delegateClaim = double.parse(jsonMap["delegateClaim"]);
    }


    detailResp.delegateReleased = double.parse(jsonMap["delegateReleased"]);
    return detailResp;
  }

}
