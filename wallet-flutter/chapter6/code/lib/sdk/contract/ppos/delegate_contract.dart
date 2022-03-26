import 'dart:typed_data';

import 'package:digging/sdk/contract/ppos/gas_provider.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/raw_transaction.dart';
import 'package:digging/sdk/parameters/inner_contracts.dart';
import 'package:digging/sdk/protocol/Web3.dart';
import 'package:digging/sdk/rlp/rlp_encoder.dart';
import 'package:digging/sdk/rlp/rlp_string.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/transaction_encoder.dart';

class DelegateContract {
  static String _address = "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzsjx8h7";
  Web3 _web3;
  Credentials _credentials;

  DelegateContract(this._web3, this._credentials);

  static DelegateContract load(Web3 web3, Credentials credentials) {
    return DelegateContract(web3, credentials);
  }

  /// nodeId  节点Id
  ///  stakingAmountType 0: 自由金额； 1: S锁仓
  ///  amount 委托的金额(按照最小单位算，1LAT = 10**18 von
  ///
  void delegate(String nodeId, int stakingAmountType, BigInt amount) async {
    List<dynamic> paramList = List.empty(growable: true);
    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.createByBigInt(BigInt.from(1004)))));

    paramList.add(RlpString.create(RlpEncoder.encode(
        RlpString.createByBigInt(BigInt.from(stakingAmountType)))));

    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.create(Numeric.hexToBytes(nodeId)))));
    paramList.add(
        RlpString.create(RlpEncoder.encode(RlpString.createByBigInt(amount))));

    String paramEncodeData =
        Numeric.bytesToHex(RlpEncoder.encode(paramList), withPrefix: false);

    BigInt nonce = await _web3.getTransactionCount(_credentials.address);
    BigInt gasPrice = await _web3.gasPrice();

    RawTransaction rawTransaction = RawTransaction(nonce, gasPrice,
        BigInt.from(49920), _address, BigInt.zero, paramEncodeData);

    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, _credentials, BigInt.from(210309));

    String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

    String txHash = await _web3.sendRawTransaction(hexSignData);

    int asda = 0;

  }

  /// nodeId  节点Id
  ///  stakingBlockNumber 委托时的块高
  ///  amount 委托的金额(按照最小单位算，1LAT = 10**18 von
  ///
  void undelegate(
      String nodeId, BigInt stakingBlockNumber, BigInt amount) async {
    List<dynamic> paramList = List.empty(growable: true);
    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.createByBigInt(BigInt.from(1005)))));

    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.createByBigInt(stakingBlockNumber))));

    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.create(Numeric.hexToBytes(nodeId)))));
    paramList.add(
        RlpString.create(RlpEncoder.encode(RlpString.createByBigInt(amount))));

    String paramEncodeData =
        Numeric.bytesToHex(RlpEncoder.encode(paramList), withPrefix: false);

    BigInt nonce = await _web3.getTransactionCount(_credentials.address);
    BigInt gasPrice = await _web3.gasPrice();

    RawTransaction rawTransaction = RawTransaction(nonce, gasPrice,
        BigInt.from(49920), _address, BigInt.zero, paramEncodeData);

    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, _credentials, BigInt.from(210309));

    String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

    String txHash = await _web3.sendRawTransaction(hexSignData);
  }
}
