class TransactionListResp {
  String txHash = ""; //交易hash
  String from = ""; //发送方地址（操作地址）
  String to = ""; //接收方地址
  BigInt seq = BigInt.zero; //排序号
  double value = 0; //金额(单位:von)
  double actualTxCost = 0; //交易费用(单位:von)
  int txType = 0; //交易类型 0：转账  1：合约发布  2：合约调用    5：MPC交易
  BigInt serverTime = BigInt.one; //服务器时间
  BigInt timestamp = BigInt.one; //出块时间
  BigInt blockNumber = BigInt.one; //交易所在区块高度
  String failReason = ""; //失败原因
  String receiveType = ""; //此字段表示的是to字段存储的账户类型：account-钱包地址，contract-合约地址，
  int txReceiptStatus = 0;

  static List<TransactionListResp> fromList(List<dynamic> dataList) {
    List<TransactionListResp> txList = List.empty(growable: true);

    for (Map<String, dynamic> jsonMap in dataList) {
      TransactionListResp txRsp = fromMap(jsonMap);

      txList.add(txRsp);
    }

    return txList;
  }

  static TransactionListResp fromMap(Map<String, dynamic> jsonMap) {
    TransactionListResp txRsp = TransactionListResp();

    txRsp.txHash = jsonMap["txHash"];
    txRsp.from = jsonMap["from"];
    txRsp.to = jsonMap["to"];
    txRsp.seq = BigInt.from(jsonMap["seq"]);

    txRsp.value = double.parse(jsonMap["value"]);

    txRsp.actualTxCost = double.parse(jsonMap["actualTxCost"]);
    txRsp.txType = int.parse(jsonMap["txType"]);
    txRsp.serverTime = BigInt.from(jsonMap["serverTime"]);
    txRsp.timestamp = BigInt.from(jsonMap["timestamp"]);
    txRsp.blockNumber = BigInt.from(jsonMap["blockNumber"]);
    txRsp.failReason = jsonMap["failReason"];

    txRsp.receiveType = jsonMap["receiveType"];
    txRsp.txReceiptStatus = jsonMap["txReceiptStatus"];
    return txRsp;
  }

// TransactionListResp.name(
//     this.txHash,
//     this.from,
//     this.to,
//     this.seq,
//     this.value,
//     this.actualTxCost,
//     this.txType,
//     this.serverTime,
//     this.timestamp,
//     this.blockNumber,
//     this.failReason,
//     this.receiveType,
//     this.txReceiptStatus); //交易状态

}
