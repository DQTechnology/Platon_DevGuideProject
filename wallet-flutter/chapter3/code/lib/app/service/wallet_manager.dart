import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/utils/wallet_util.dart';

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
  static bool generateWallet() {
    CreateWalletSessionInfo? sessionInfo = getCreateWalletSession();
    if (sessionInfo == null) {
      return false;
    }
    return _importMnemonicWords(sessionInfo.walletName, sessionInfo.password,
        sessionInfo.mnemonicWords);
  }

  ///
  static bool _importMnemonicWords(
      String name, String password, List<String> mnemonicWords) {
    String mnemonic = mnemonicWords.join(" ");

    ECKeyPair ecKeyPair = WalletUtil.generateHDByMnemonic(mnemonic);

    Credentials credentials = Credentials.createByECKeyPair(ecKeyPair);



    return _storePrivateKey(name, password, ecKeyPair);
  }
  /// 存储私钥
  static bool _storePrivateKey(
      String name, String password, ECKeyPair ecKeyPair) {
    /// todo
    return true;
  }
}
