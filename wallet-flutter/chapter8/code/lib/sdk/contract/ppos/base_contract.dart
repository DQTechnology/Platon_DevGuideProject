import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/protocol/web3.dart';
import 'package:digging/sdk/protocol/models/transaction_receipt.dart';

class BaseContract {

  final Web3 web3;
  final Credentials credentials;


  BaseContract(this.web3, this.credentials);


  Future<TransactionReceipt> getTransactionReceipt(String txHash) async {
    TransactionReceipt? txReceipt = await web3.getTransactionReceipt(txHash);
    if (txReceipt != null) {
      return txReceipt;
    }
    // 延迟5s再获取
    await Future.delayed(const Duration(seconds: 5));
    return getTransactionReceipt(txHash);
  }
}