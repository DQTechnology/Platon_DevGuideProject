import 'dart:convert';
import 'dart:io';
import 'package:digging/sdk/utils/numeric.dart';

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

  T _decodeJson<T>(String responseBody) {
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
      return _decodeJson<String>(responseBody);
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
      String result = _decodeJson<String>(responseBody);
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
      return _decodeJson<String>(responseBody);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  /// 获取块高
  Future<BigInt> gasPrice() async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8
        .encode(json.encode(buildParam("platon_gasPrice", List.empty()))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody);
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }
  /// 获取块高
  Future<BigInt> getBalance(String address) async {
    HttpClientRequest request = await getHttpClientRequest();
    request.add(utf8
        .encode(json.encode(buildParam("platon_getBalance", List.from([address, "latest"])))));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      String result = _decodeJson<String>(responseBody);
      return Numeric.decodeQuantity(result);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }
}
