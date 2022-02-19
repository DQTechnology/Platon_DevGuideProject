import 'dart:typed_data';

import 'package:bip39/bip39.dart' as bip39;
import 'package:bip32/bip32.dart' as bip32;
import 'package:convert/convert.dart';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/crypto/hash.dart';
import 'package:digging/sdk/utils/numeric.dart';

class WalletUtil {
// "m/44'/60'/0'/0/0"
  static const String PATH = "m/44'/60'/0'/0/0";

  static String generateMnemonic() {
    return bip39.generateMnemonic();
  }

  static ECKeyPair generatePlatONBip39Wallet() {
    // 1, 生成助记词
    String mnemonic = bip39.generateMnemonic();

    // 2 生成种子
    Uint8List seed = bip39.mnemonicToSeed(mnemonic);

    return ECKeyPair.createByU8ListPrivateKey(Hash.sha256(seed));
  }

  static ECKeyPair generateHDECKeyPair() {
    // 1, 生成助记词
    String mnemonic = bip39.generateMnemonic();

    return generateHDByMnemonic(mnemonic);
  }

  //
  static ECKeyPair generateHDByMnemonic(String mnemonic) {

    if (!bip39.validateMnemonic(mnemonic)) {
      throw Exception("生成助记词失败!");
    }
    // 2 生成种子
    Uint8List seed = bip39.mnemonicToSeed(mnemonic);
    //
    var root = bip32.BIP32.fromSeed(seed);

    final child = root.derivePath(PATH);

    String privateKey =
        Numeric.prependHexPrefix(hex.encode(child.privateKey as List<int>));

    return ECKeyPair.createByHexPrivateKey(privateKey);
  }
}
