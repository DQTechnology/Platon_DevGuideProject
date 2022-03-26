import 'dart:convert';
import 'dart:io';
import 'package:digging/sdk/protocol/models/transaction_receipt.dart';
import 'package:digging/sdk/utils/numeric.dart';

import 'models/platon_block_info.dart';

class Web3 {
  late Uri _uri;

  final HttpClient _httpClient = HttpClient();

  Web3._(String url) {
    _uri = Uri.parse(url);
  }

  ///
  static Web3 build(String _url) {
    return Web3._(_url);
  }

  Future<HttpClientRequest> getHttpClientRequest() async {
    HttpClientRequest request = await _httpClient.postUrl(_uri);
    request.headers.add("content-type", "application/json");
    return request;
  }

  T? _decodeJson<T>(String responseBody) {
    Map<String, dynamic> jsonMap = json.decode(responseBody);
    return jsonMap["result"] as T;
  }

  Map<String, dynamic> buildParam(String method, List<dynamic> params,
      {int id = 1}) {
    return {"jsonrpc": "2.0", "method": method, "params": params, "id": id};
  }

  /// 获取当前的客户端版本
  Future<String> web3ClientVersion() async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8
        .encode(json.encode(buildParam("web3_clientVersion", List.empty()))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      return _decodeJson<String>(responseBody)!;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 获取块高
  Future<BigInt> platonBlockNumber() async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8
        .encode(json.encode(buildParam("platon_blockNumber", List.empty()))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 获取块高
  Future<String> getAddressHrp() async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8
        .encode(json.encode(buildParam("platon_getAddressHrp", List.empty()))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      return _decodeJson<String>(responseBody)!;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 获取块高
  Future<BigInt> gasPrice() async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(
        utf8.encode(json.encode(buildParam("platon_gasPrice", List.empty()))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 获取块高
  Future<BigInt> getBalance(String address) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(
        buildParam("platon_getBalance", List.from([address, "latest"])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 通过块高获取块的信息
  Future<PlatonBlockInfo> getBlockByNumber(BigInt blockNumber) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(buildParam("platon_getBlockByNumber",
        List.from([Numeric.encodeQuantity(blockNumber), true])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> result =
          _decodeJson<Map<String, dynamic>>(responseBody)!;
      PlatonBlockInfo blockInfo = PlatonBlockInfo.fromJson(result);
      return blockInfo;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 从与给定区块编号匹配的区块中返回区块中的交易数量
  Future<BigInt> getBlockTransactionCountByNumber(BigInt blockNumber) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(buildParam(
        "platon_getBlockTransactionCountByNumber",
        List.from([Numeric.encodeQuantity(blockNumber)])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 从与给定区块编号匹配的区块中返回区块中的交易数量
  Future<BigInt> getBlockTransactionCountByHash(String blockHash) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(buildParam(
        "platon_getBlockTransactionCountByHash", List.from([blockHash])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 该地址发送的交易次数的整数
  Future<BigInt> getTransactionCount(String address) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(buildParam(
        "platon_getTransactionCount", List.from([address, "latest"])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 该地址发送的交易次数的整数
  Future<TransactionReceipt?> getTransactionReceipt(String txHash) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(
        buildParam("platon_getTransactionReceipt", List.from([txHash])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();


      Map<String, dynamic>? result = _decodeJson<Map<String, dynamic>>(responseBody);

      if(result == null) {
        return null;
      }

      TransactionReceipt receipt = TransactionReceipt.fromMap(result);


      return receipt;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }


  /// 该地址发送的交易次数的整数
  Future<String> sendRawTransaction(String rawData) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8.encode(json.encode(
        buildParam("platon_sendRawTransaction", List.from([rawData])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return result;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  ///  执行call
  Future<String> platonCall(List<Object> params) async {
    HttpClientRequest request = await getHttpClientRequest();

    request.add(utf8.encode(json.encode(
        buildParam("platon_call", params))));

    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody)!;
      return result;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }



}
