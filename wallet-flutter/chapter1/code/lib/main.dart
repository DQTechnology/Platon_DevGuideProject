import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/parameters/network_parameters.dart';
import 'package:digging/sdk/protocol/Web3.dart';
import 'package:digging/sdk/utils/amount_util.dart';
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

  String _address = "lat19ydcsmfnxlqw4640u93r3chn2pk0c8shsn3lhg";
  String _lat = "0";

  void _generateWallet() {
    // 创建钱包
    ECKeyPair ecKeyPair = WalletUtil.generatePlatONBip39Wallet();

    Credentials credentials = Credentials.createByECKeyPair(ecKeyPair);

    setState(() {
      _address = credentials.address;
      print(_address);
    });
  }

  void _getBalance() async {
    // 创建钱包
    BigInt balance =
        await _web3.getBalance(_address);

    setState(() {
      _lat = AmountUtil.convertVonToLat(balance);
    });
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
            Text(_address), // 显示钱包地址
            ElevatedButton(
              child: const Text("生成钱包地址"),
              onPressed: () {
                _generateWallet();
              },
            ),
            Text(_lat), // 显示lat
            ElevatedButton(
              child: const Text("获取钱包余额"),
              onPressed: () {
                _getBalance();
              },
            ),
          ],
        ),
      ),
    );
  }
}
