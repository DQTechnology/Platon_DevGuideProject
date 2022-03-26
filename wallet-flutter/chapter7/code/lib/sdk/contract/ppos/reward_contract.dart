
import 'dart:typed_data';

import 'package:digging/sdk/contract/ppos/base_contract.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/raw_transaction.dart';

import 'package:digging/sdk/protocol/models/transaction_receipt.dart';
import 'package:digging/sdk/rlp/rlp_encoder.dart';
import 'package:digging/sdk/rlp/rlp_string.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/transaction_encoder.dart';
import 'package:digging/sdk/protocol/web3.dart';

class RewardContract extends BaseContract{
  static const String _address = "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqxlcypcy";

  RewardContract({required Web3 web3, required Credentials credentials}): super(web3, credentials);

  static RewardContract load(Web3 web3, Credentials credentials) {
    return RewardContract(web3: web3, credentials:credentials);
  }


  Future<TransactionReceipt> withdrawDelegateReward() async {
    List<dynamic> paramList = List.empty(growable: true);

    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.createByBigInt(BigInt.from(5000)))));

    String paramEncodeData =
    Numeric.bytesToHex(RlpEncoder.encode(paramList), withPrefix: false);

    BigInt nonce = await web3.getTransactionCount(credentials.address);
    BigInt gasPrice = await web3.gasPrice();

    RawTransaction rawTransaction = RawTransaction(nonce, gasPrice,
        BigInt.from(38640), _address, BigInt.zero, paramEncodeData);

    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, credentials, BigInt.from(210309));

    String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

    String txHash = await web3.sendRawTransaction(hexSignData);

    return await getTransactionReceipt(txHash);
  }



}