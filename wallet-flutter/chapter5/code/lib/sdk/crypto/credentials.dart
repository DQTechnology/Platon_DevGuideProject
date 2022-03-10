import 'dart:typed_data';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/crypto/keys.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:digging/sdk/parameters/network_parameters.dart';
import 'bech32.dart';

class Credentials {
  final ECKeyPair _ecKeyPair;
  final String _address;

  Credentials(this._ecKeyPair, this._address);

  ECKeyPair get ecKeyPair {
    return _ecKeyPair;
  }

  String get address {
    return _address;
  }

  String getAddressByNetworkParameters(NetworkParameters networkParameters) {
    Uint8List originBytes = Bech32.addressDecode(_address);

    return Bech32.addressEncodeUint8List(
        networkParameters.getCurrentHrp, originBytes);
  }

  static Credentials createByECKeyPair(ECKeyPair ecKeyPair) {

    String address = getAddressByECKeyPair(ecKeyPair);

    return Credentials(ecKeyPair, address);
  }

  static String getAddressByECKeyPair(ECKeyPair ecKeyPair) {
    String hexAddress = Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair));

    return Bech32.addressEncode(NetworkParameters.getHrp, hexAddress);
  }

  static Credentials createByPrivateAndPublicKey(
      String privateKey, String publicKey) {
    return createByECKeyPair(
        ECKeyPair(Numeric.toBigInt(privateKey), Numeric.toBigInt(publicKey)));
  }

  static Credentials createByPrivate(String privateKey) {
    return createByECKeyPair(
        ECKeyPair.createByBigIntPrivateKey(Numeric.toBigInt(privateKey)));
  }
}
