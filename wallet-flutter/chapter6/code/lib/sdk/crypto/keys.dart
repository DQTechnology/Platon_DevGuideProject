import 'dart:math';
import 'package:digging/sdk/crypto/random_bridge.dart';
import 'package:digging/sdk/crypto/hash.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:pointycastle/api.dart';
import 'package:pointycastle/ecc/api.dart';
import 'package:pointycastle/key_generators/api.dart';
import 'package:pointycastle/key_generators/ec_key_generator.dart';
import 'eckeypair.dart';
import 'hash.dart';

class Keys {
  static const int privateKeySize = 32;
  static const int _publicKeySize = 64;

  static const int addressSize = 160;
  static const int addressLengthInHex = addressSize >> 2;
  static const int publicKeyLengthInHex = _publicKeySize << 1;
  static const int privateKeyLengthInHex = privateKeySize << 1;

  static final Random _random = Random.secure();

  static ECKeyPair createEcKeyPair() {
    final generator = ECKeyGenerator();

    final keyParams = ECKeyGeneratorParameters(ECKeyPair.curve);

    generator.init(ParametersWithRandom(keyParams, RandomBridge(_random)));

    final key = generator.generateKeyPair();
    final ecPrivateKey = key.privateKey as ECPrivateKey;

    BigInt privateKey = ecPrivateKey.d!;

    return ECKeyPair.createByBigIntPrivateKey(privateKey);
  }

  static String getAddress(ECKeyPair ecKeyPair) {
    return getAddressByBigIntPublicKey(ecKeyPair.getPublicKey());
  }

  static String getAddressByBigIntPublicKey(BigInt publicKey) {
    return getAddressByStrPublicKey(Numeric.toHexStringWithPrefixZeroPadded(
        publicKey, publicKeyLengthInHex));
  }

  static String getAddressByStrPublicKey(String publicKey) {
    String publicKeyNoPrefix = Numeric.cleanHexPrefix(publicKey);

    if (publicKeyNoPrefix.length < publicKeyLengthInHex) {
      publicKeyNoPrefix =
          "0" * (publicKeyLengthInHex - publicKeyNoPrefix.length) +
              publicKeyNoPrefix;
    }

    final hashBytes = Hash.sha3Hex(publicKeyNoPrefix);

    String hexStr = Numeric.bytesToHex(hashBytes);

    return hexStr.substring(hexStr.length - addressLengthInHex);
  }

  static String toChecksumAddress(String address) {
    String lowercaseAddress = Numeric.cleanHexPrefix(address).toLowerCase();

    String addressHash =
        Numeric.cleanHexPrefix(Hash.sha3HexToString(lowercaseAddress));

    String result = "0x";
    for (int i = 0; i < lowercaseAddress.length; ++i) {
      if (int.parse(addressHash[i], radix: 16) >= 8) {
        result += lowercaseAddress[i].toUpperCase();
      } else {
        result += lowercaseAddress[i];
      }
    }
    return result;
  }
}
