import 'dart:convert';
import 'dart:io';

import 'package:digging/app/entity/delegation_list_by_address_req.dart';
import 'package:digging/app/entity/delegation_list_by_address_resp.dart';
import 'package:digging/app/entity/resp_page.dart';
import 'package:digging/app/entity/staking_details_resp.dart';
import 'package:digging/app/entity/transaction_list_param.dart';
import 'package:digging/app/entity/transaction_list_resp.dart';

import '../entity/address_detail_resp.dart';
import '../entity/alive_staking_list_req.dart';
import '../entity/alive_staking_list_resp.dart';

class PlatScanApi {
  static final HttpClient _httpClient = HttpClient();

  static const String _url = "https://devnetscan.platon.network/browser-server";

  static Future<HttpClientRequest> getHttpClientRequest(String path) async {
    Uri uri = Uri.parse("$_url$path");

    HttpClientRequest request = await _httpClient.postUrl(uri);

    request.headers.add("content-type", "application/json");
    return request;
  }

  static Future<RespPage<TransactionListResp>> getTransactionListByAddress(
      TransactionListParam param) async {
    HttpClientRequest request =
        await getHttpClientRequest("/transaction/transactionListByAddress");
    String strJson = param.toJson();
    request.add(utf8.encode(strJson));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> jsonMap = json.decode(responseBody);

      RespPage<TransactionListResp> rsp = RespPage.fromMap(jsonMap);

      List<TransactionListResp> txList =
          TransactionListResp.fromList(jsonMap["data"]);

      rsp.data = txList;
      return rsp;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  static Future<AddressDetailResp> getAddressDetail(String address) async {
    HttpClientRequest request = await getHttpClientRequest("/address/details");
    request.add(utf8.encode("{\"address\":\"$address\"}"));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> jsonMap = json.decode(responseBody);

      return AddressDetailResp.fromMap(jsonMap["data"]);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  static Future<RespPage<AliveStakingListResp>> getAliveStakingList(
      AliveStakingListReq aliveStakingListReq) async {
    HttpClientRequest request =
        await getHttpClientRequest("/staking/aliveStakingList");
    String strJson = aliveStakingListReq.toJson();
    request.add(utf8.encode(strJson));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> jsonMap = json.decode(responseBody);
      RespPage<AliveStakingListResp> rsp = RespPage.fromMap(jsonMap);

      List<AliveStakingListResp> aliveStakingList =
          AliveStakingListResp.fromList(jsonMap["data"]);
      rsp.data = aliveStakingList;
      return rsp;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  static Future<RespPage<DelegationListByAddressResp>> getDelegationListByAddress(
      DelegationListByAddressReq delegationListByAddressReq) async {
    HttpClientRequest request =
        await getHttpClientRequest("/staking/delegationListByAddress");

    String strJson = delegationListByAddressReq.toJson();
    request.add(utf8.encode(strJson));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> jsonMap = json.decode(responseBody);
      RespPage<DelegationListByAddressResp> rsp = RespPage.fromMap(jsonMap);

      List<DelegationListByAddressResp> rspList =
      DelegationListByAddressResp.fromList(jsonMap["data"]);
      rsp.data = rspList;
      return rsp;
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }

  static Future<StakingDetailsResp> getStakingDetails(String nodeId) async {
    HttpClientRequest request =
    await getHttpClientRequest("/staking/stakingDetails");

    String strJson ="{\"nodeId\":\"$nodeId\"}";

    request.add(utf8.encode(strJson));
    HttpClientResponse response = await request.close();
    if (response.statusCode == HttpStatus.ok) {
      String responseBody = await response.transform(utf8.decoder).join();
      Map<String, dynamic> jsonMap = json.decode(responseBody);

      return StakingDetailsResp.fromMap(jsonMap["data"]);
    } else {
      throw Exception("请求错误,错误码: ${response.statusCode}");
    }
  }
}
