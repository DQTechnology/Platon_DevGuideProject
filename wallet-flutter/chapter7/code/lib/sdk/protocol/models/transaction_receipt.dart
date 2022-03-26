import 'package:digging/sdk/utils/numeric.dart';

import 'log.dart';

class TransactionReceipt {
  String transactionHash = "";
  BigInt transactionIndex = BigInt.zero;
  String blockHash = "";
  BigInt blockNumber = BigInt.zero;
  BigInt cumulativeGasUsed = BigInt.zero;
  BigInt gasUsed = BigInt.zero;
  String contractAddress = "";
  String root = "";

  BigInt status = BigInt.zero;
  String from = "";
  String to = "";
  List<Log> logs = List.empty();
  String logsBloom = "";

  static TransactionReceipt fromMap(Map<String, dynamic> jsonMap) {
    TransactionReceipt txReceipt = TransactionReceipt();
    txReceipt.transactionHash = jsonMap["transactionHash"];
    txReceipt.transactionIndex = Numeric.decodeQuantity(jsonMap["transactionIndex"]);
    txReceipt.blockHash = jsonMap["blockHash"];
    txReceipt.blockNumber =  Numeric.decodeQuantity(jsonMap["blockNumber"]);
    txReceipt.cumulativeGasUsed =  Numeric.decodeQuantity(jsonMap["cumulativeGasUsed"]);
    txReceipt.gasUsed =  Numeric.decodeQuantity(jsonMap["gasUsed"]);

    txReceipt.contractAddress = jsonMap["contractAddress"];
    txReceipt.root = jsonMap["root"];
    txReceipt.status =  Numeric.decodeQuantity(jsonMap["status"]);
    txReceipt.from = jsonMap["from"];

    txReceipt.to = jsonMap["to"];
    txReceipt.logsBloom = jsonMap["logsBloom"];

    return txReceipt;
  }
}
