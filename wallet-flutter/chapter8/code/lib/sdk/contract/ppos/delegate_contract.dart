import 'dart:convert';
import 'dart:typed_data';

import 'package:convert/convert.dart';
import 'package:digging/sdk/contract/ppos/dto/call_response.dart';
import 'package:digging/sdk/contract/ppos/dto/delegation_id_info.dart';
import 'package:digging/sdk/crypto/bech32.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/raw_transaction.dart';
import 'package:digging/sdk/protocol/models/transaction_receipt.dart';
import 'package:digging/sdk/protocol/web3.dart';
import 'package:digging/sdk/rlp/rlp_encoder.dart';
import 'package:digging/sdk/rlp/rlp_string.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/transaction_encoder.dart';
import 'base_contract.dart';

class DelegateContract extends BaseContract{
  static const String _address = "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzsjx8h7";


  DelegateContract({required Web3 web3, required Credentials credentials}): super(web3, credentials);

  static DelegateContract load(Web3 web3, Credentials credentials) {
    return DelegateContract(web3: web3, credentials:credentials);
  }

  /// nodeId  节点Id
  ///  stakingAmountType 0: 自由金额； 1: S锁仓
  ///  amount 委托的金额(按照最小单位算，1LAT = 10**18 von
  ///
  Future<TransactionReceipt> delegate(String nodeId, int stakingAmountType, BigInt amount) async {
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

    BigInt nonce = await web3.getTransactionCount(credentials.address);
    BigInt gasPrice = await web3.gasPrice();

    RawTransaction rawTransaction = RawTransaction(nonce, gasPrice,
        BigInt.from(49920), _address, BigInt.zero, paramEncodeData);

    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, credentials, BigInt.from(210309));

    String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

    String txHash = await web3.sendRawTransaction(hexSignData);

    return await getTransactionReceipt(txHash);
  }

  /// nodeId  节点Id
  ///  stakingBlockNumber 委托时的块高
  ///  amount 委托的金额(按照最小单位算，1LAT = 10**18 von
  ///
  Future<TransactionReceipt> undelegate(
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

    BigInt nonce = await web3.getTransactionCount(credentials.address);
    BigInt gasPrice = await web3.gasPrice();

    RawTransaction rawTransaction = RawTransaction(nonce, gasPrice,
        BigInt.from(49920), _address, BigInt.zero, paramEncodeData);

    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, credentials, BigInt.from(210309));

    String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

    String txHash = await web3.sendRawTransaction(hexSignData);

    return await getTransactionReceipt(txHash);
  }

  Future<TransactionReceipt> getTransactionReceipt(String txHash) async {
    TransactionReceipt? txReceipt = await web3.getTransactionReceipt(txHash);
    if (txReceipt != null) {
      return txReceipt;
    }
    // 延迟5s再获取
    await Future.delayed(const Duration(seconds: 5));
    return getTransactionReceipt(txHash);
  }

  /// nodeId  节点Id
  ///  stakingBlockNumber 委托时的块高
  ///  amount 委托的金额(按照最小单位算，1LAT = 10**18 von
  ///
  Future<CallResponse<List<DelegationIdInfo>>> getRelatedListByDelAddr(
      String address) async {
    List<dynamic> paramList = List.empty(growable: true);

    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.createByBigInt(BigInt.from(1103)))));

    address = Bech32.addressDecodeHex(address);

    paramList.add(RlpString.create(
        RlpEncoder.encode(RlpString.create(Numeric.hexToBytes(address)))));

    String paramEncodeData =
        Numeric.bytesToHex(RlpEncoder.encode(paramList), withPrefix: true);
    //param的格式为
    // {
    //  from: "" 调用钱包地址
    //  to: ""  委托智能合约地址
    //  data: ""  调用的参数
    // }
    Map<String, String> callData = {
      "from": address,
      "to": _address,
      "data": paramEncodeData
    };
    String result = await web3.platonCall([callData, "latest"]);

    result = Numeric.cleanHexPrefix(result);

    result = utf8.decode(hex.decode(result));

    Map<String, dynamic> jsonMap = json.decode(result);

    CallResponse<List<DelegationIdInfo>> rsp =
        CallResponse<List<DelegationIdInfo>>();

    int code = jsonMap["Code"];
    rsp.code = code;
    if (code != 0) {
      if (jsonMap.containsKey("ErrMsg")) {
        rsp.errMsg = jsonMap["ErrMsg"];
      }
      return rsp;
    }

    List<DelegationIdInfo> delegationIdInfoList =
        DelegationIdInfo.fromList(jsonMap["Ret"]);
    rsp.data = delegationIdInfoList;
    return rsp;
  }
}
