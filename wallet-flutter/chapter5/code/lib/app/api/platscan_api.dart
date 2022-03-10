import 'dart:convert';
import 'dart:io';

import 'package:digging/app/entity/resp_page.dart';
import 'package:digging/app/entity/transaction_list_param.dart';
import 'package:digging/app/entity/transaction_list_resp.dart';

class PlatScanApi {
  static final HttpClient _httpClient = HttpClient();

  static const String _url ="https://devnetscan.platon.network/browser-server";

  static Future<HttpClientRequest> getHttpClientRequest(String path) async {

    Uri uri = Uri.parse("$_url$path");

    HttpClientRequest request = await _httpClient.postUrl(uri);

    request.headers.add("content-type", "application/json");
    return request;
  }

  static Future<RespPage<TransactionListResp>> getTransactionListByAddress(TransactionListParam param) async {
    HttpClientRequest request = await getHttpClientRequest("/transaction/transactionListByAddress");

    String strJson = param.toJson();

    request.add(utf8.encode(strJson));

    HttpClientResponse response = await request.close();

    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> jsonMap = json.decode(responseBody);

      RespPage<TransactionListResp> rsp = RespPage.fromMap(jsonMap);

      List<TransactionListResp> txList = TransactionListResp.fromList(jsonMap["data"]);

      rsp.data = txList;
      return rsp;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }

  }
}
