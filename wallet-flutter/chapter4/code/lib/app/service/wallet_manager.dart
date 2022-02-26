import 'dart:io';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/crypto/wallet.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/utils/wallet_util.dart';
import 'package:path_provider/path_provider.dart';

class CreateWalletSessionInfo {
  String walletName;

  String password;

  List<String> mnemonicWords;

  CreateWalletSessionInfo(this.walletName, this.password, this.mnemonicWords);
}

class WalletManager {
  static CreateWalletSessionInfo? _createWalletInfo;

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
    String keystore = Wallet.createNew(ecKeyPair, password).toJson();

    Directory appDocDir = await getApplicationDocumentsDirectory();

    String appDocPath = appDocDir.path;

    File file = File("$appDocPath/$name.json");

    await file.writeAsString(keystore);

    return true;
  }
}
