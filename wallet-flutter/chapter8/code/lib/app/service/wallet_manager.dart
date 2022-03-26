import 'dart:convert';
import 'dart:io';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/crypto/wallet.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/wallet_util.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CreateWalletSessionInfo {
  String walletName;

  String password;

  List<String> mnemonicWords;

  CreateWalletSessionInfo(this.walletName, this.password, this.mnemonicWords);
}

class WalletManager {
  static CreateWalletSessionInfo? _createWalletInfo;

  static final Set<String> _walletSet = {};

  static final Map<String, String> _addressWalletNameMap = {};

  static String _walletName = "";

  static Future<void> loadAllWallet() async {
    Directory appDocDir = await getApplicationDocumentsDirectory();

    String appDocPath = appDocDir.path;

    Directory directory = Directory("$appDocPath/keyfiles");

    if (!await directory.exists()) {
      return;
    }

    await directory.list(recursive: false).forEach((ele) async {
      String walletName = ele.path.split("/").last.split(".")[0];
      _walletSet.add(walletName);

      String address = await getWalletAddress(walletName);

      _addressWalletNameMap[address] = walletName;
    });
  }

  static List<String> getAllAddress() {
    return _addressWalletNameMap.keys.toList();
  }

  /// 判断是否存在钱包
  static bool isExistWallet() {
    return _walletSet.isNotEmpty;
  }

  static Future<String> getCurrentWalletName() async {
    if (_walletName.isNotEmpty) {
      return _walletName;
    }

    final prefs = await SharedPreferences.getInstance();

    String? walletName = prefs.getString("curWallet");

    if (walletName != null) {
      _walletName = walletName;
    } else {
      if (_walletSet.isNotEmpty) {
        _walletName = _walletSet.first;
        prefs.setString("curWallet", _walletName);
      }
    }
    return _walletName;
  }

  static switchWallet(String walletName) async {
    final prefs = await SharedPreferences.getInstance();

    prefs.setString("curWallet", walletName);

    _walletName = walletName;
  }

  static Future<String> getCurrentAddress() async {
    String walletName = await getCurrentWalletName();

    return await getWalletAddress(walletName);
  }

  static int getWalletNum()  {
    return _walletSet.length;
  }

  static String getWalletNameByAddress(String address) {
    return _addressWalletNameMap[address] ?? "";
  }

  static Future<String> getWalletAddress(String walletName) async {
    Directory appDocDir = await getApplicationDocumentsDirectory();

    String appDocPath = appDocDir.path;

    File file = File("$appDocPath/keyfiles/$walletName.json");

    String keystore = await file.readAsString();

    final data = json.decode(keystore);

    String? address = data["address"];

    if (address == null) {
      return "";
    }
    return address;
  }

  /// 生成创建钱包的会话
  static void buildCreateWalletSession(String walletName, String password) {
    String mnemonic = WalletUtil.generateMnemonic();

    List<String> mnemonicWords = mnemonic.split(" ");

    _createWalletInfo =
        CreateWalletSessionInfo(walletName, password, mnemonicWords);
  }

  static CreateWalletSessionInfo? getCreateWalletSession() {
    return _createWalletInfo;
  }

  static void clearCreateWalletSession() {
    _createWalletInfo = null;
  }

  /// 生成钱包
  static Future<bool> generateWallet() async {
    CreateWalletSessionInfo? sessionInfo = getCreateWalletSession();
    if (sessionInfo == null) {
      return false;
    }
    return importMnemonicWords(sessionInfo.walletName, sessionInfo.password,
        sessionInfo.mnemonicWords);
  }

  static Future<Wallet> getCurrentWallet(String password) async {
    return await getWallet(await getCurrentWalletName(), password);
  }

  static Future<Wallet> getWallet(String walletName, String password) async {
    Directory appDocDir = await getApplicationDocumentsDirectory();

    String appDocPath = appDocDir.path;

    File file = File("$appDocPath/keyfiles/$walletName.json");

    String keystore = await file.readAsString();

    return Wallet.fromJson(keystore, password);
  }

  ///
  static Future<bool> importMnemonicWords(
      String name, String password, List<String> mnemonicWords) async {
    /// 把助记词以空格符拼接在一起
    String mnemonic = mnemonicWords.join(" ");

    /// 创建密钥对
    ECKeyPair? ecKeyPair = WalletUtil.generateHDByMnemonic(mnemonic);

    if (ecKeyPair == null) {
      return false;
    }

    /// 保存密钥对
    return _storeECKeyPair(name, password, ecKeyPair);
  }

  static Future<bool> importKeyStore(
      String name, String password, String keystore) async {
    try {
      /// 先用密码解密钱包文件, 看是否可以解密, 如果密码正确则给导入,否则报错
      Wallet wallet = Wallet.fromJson(keystore, password);

      return _storeECKeyPair(name, password, wallet.keyPair);
    } catch (e) {
      return false;
    }
  }

  static Future<bool> importPrivateKey(
      String name, String password, String privateKey) async {
    ECKeyPair? ecKeyPair =
        ECKeyPair.createByHexPrivateKey(Numeric.cleanHexPrefix(privateKey));

    return _storeECKeyPair(name, password, ecKeyPair);
  }

  /// 存储私钥
  static Future<bool> _storeECKeyPair(
      String name, String password, ECKeyPair ecKeyPair) async {

    Wallet wallet = Wallet.createNew(ecKeyPair, password);

    String keystore = wallet.toJson();

    Directory appDocDir = await getApplicationDocumentsDirectory();

    String appDocPath = appDocDir.path;

    File file = File("$appDocPath/keyfiles/$name.json");

    if (!await file.exists()) {
      await file.create(recursive: true);
    }
    await file.writeAsString(keystore);

    _walletSet.add(name);
    _addressWalletNameMap[wallet.address] = name;

    return true;
  }
}
