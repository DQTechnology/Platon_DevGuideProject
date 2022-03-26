import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../sdk/protocol/web3.dart';

class NetworkInfo {

  String netName; // 网络名

  String hrp; // 链的可读前缀

  String nodeUrl; // 节点连接地址

  BigInt chainId; // 链Id

  String scanUrl; // 浏览器连接

  Color themeColor; // 首页背景颜色

  bool isSelected; // 当前是否被选中

  NetworkInfo(
      {required this.netName,
      required this.hrp,
      required this.nodeUrl,
      required this.chainId,
      required this.scanUrl,
      required this.themeColor,
      required this.isSelected});
}

class NetworkManager {
  // 初始化开发网和主网
  static List<NetworkInfo> networkList = [
    NetworkInfo(
        netName: "PLATON 开发网络",
        hrp: "LAT",
        nodeUrl: "http://35.247.155.162:6789",
        chainId: BigInt.from(210309),
        scanUrl: "https://devnetscan.platon.network/browser-server",
        themeColor: const Color(0xff04081f),
        isSelected: false),
    NetworkInfo(
        netName: "PLATON 主网络",
        hrp: "LAT",
        nodeUrl: "https://samurai.platon.network",
        chainId: BigInt.from(100),
        scanUrl: "https://scan.platon.network/browser-server",
        themeColor: const Color(0xff0912D4),
        isSelected: false),
  ];

  static int _curNetworkIndex = 0;

  static late NetworkInfo _curNetworkInfo;

  static loadNodeConfig() async {
    final prefs = await SharedPreferences.getInstance();

    int? networkIndex = prefs.getInt("network");

    if (networkIndex != null) {
      _curNetworkIndex = networkIndex;
    }

    _curNetworkInfo = networkList[_curNetworkIndex];
    _curNetworkInfo.isSelected = true;
  }


  /// 获取web3实例
  static Web3 getWeb3() {
    return Web3.build(_curNetworkInfo.nodeUrl);
  }


  static NetworkInfo getNetworkInfoByIndex(int index) {
    return networkList[index];
  }

  static int getNetworkNum() {
    return networkList.length;
  }

  static int getCurNetworkIndex() {
    return _curNetworkIndex;
  }

  static NetworkInfo getCurNetworkInfo() {
    return _curNetworkInfo;
  }

  static switchNetwork(int index) async {

    if(index == _curNetworkIndex) {
      return;
    }

    final prefs = await SharedPreferences.getInstance();

    await prefs.setInt("network", index);
    _curNetworkIndex = index;
    _curNetworkInfo.isSelected = false;
    _curNetworkInfo = networkList[index];
    _curNetworkInfo.isSelected = true;
  }
}
