import 'dart:typed_data';

import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/crypto/raw_transaction.dart';
import 'package:digging/sdk/parameters/network_parameters.dart';
import 'package:digging/sdk/protocol/Web3.dart';
import 'package:digging/app/utils/amount_util.dart';
import 'package:digging/sdk/protocol/models/platon_block_info.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/transaction_encoder.dart';
import 'package:digging/sdk/utils/wallet_util.dart';
import 'package:flutter/material.dart';

void main() {
  // 使用测试网
  NetworkParameters.init(BigInt.from(210309), "lat");

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Digging',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Digging'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // 创建web3对象
  final Web3 _web3 = Web3.build("http://35.247.155.162:6789");

  void _onExecute() async {
    // // 获取当前链的地址前缀
    // String hrp = await _web3.getAddressHrp();
    // print("hrp: $hrp");
    // // 获取当前gas的价格
    // BigInt gasPrice = await _web3.gasPrice();
    // print("gasPrice: ${AmountUtil.convertVonToLat(gasPrice)}");

    // // 获取当前块高
    // BigInt blockNumber = await _web3.platonBlockNumber();

    // PlatonBlockInfo blockInfo = await _web3.getBlockByNumber(BigInt.from(6533257));

    // BigInt txCount = await _web3.getBlockTransactionCountByNumber(BigInt.from(6533257));
    // BigInt txCount = await _web3.getBlockTransactionCountByHash("0x0561ab627d3053c486a552e594f6b3f40f7acc2fd107866169feb34de346129b");

    // BigInt txCount = await _web3.getTransactionCount("lat1tgu6pts6nhmneu5zhqly3rc83r6y6ecfmde03e");
    //

    //  加载秘钥
    Credentials credentials = Credentials.createPrivate(
        "a4ac816da1ab40f805d026009247002f47c8c0a9af95b35ca9741c576466e1a8");

    // 获取钱包的交易次数作为nonce
    BigInt nonce = await _web3.getTransactionCount(credentials.address);
    // 获取当前gasPrice
    BigInt gasPrice = await _web3.gasPrice();
    // 构建交易对象
    RawTransaction rawTransaction = RawTransaction(
        nonce,
        gasPrice,
        BigInt.from(21000),
        "lat1zrq89dhle45g78mm4j8aq3dq5m2shpu56ggc6e",
        BigInt.parse("10000000000000000000"),
        "");
    // 签名
    Uint8List signData = TransactionEncoder.signMessageWithChainId(
        rawTransaction, credentials, BigInt.from(210309));
    // 转换为16进制字符串
   String hexSignData = Numeric.bytesToHex(signData, withPrefix: true);

  // 发送交易数据
   String txHash = await _web3.sendRawTransaction(hexSignData);


    int asda = 0;

    // //  获取钱余额
    // BigInt balance = await _web3.getBalance("lat1tgu6pts6nhmneu5zhqly3rc83r6y6ecfmde03e");
    // print("balance: ${AmountUtil.convertVonToLat(balance)}");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              child: const Text("执行"),
              onPressed: () {
                _onExecute();
              },
            ),
          ],
        ),
      ),
    );
  }
}
