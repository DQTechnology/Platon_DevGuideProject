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

    return Bech32.addressEncodeUint8List(networkParameters.getCurrentHrp, originBytes);
  }

  static Credentials createByECKeyPair(ECKeyPair ecKeyPair) {

    String hexAddress = Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair));

    String address =
        Bech32.addressEncode(NetworkParameters.getHrp, hexAddress);

    return Credentials(ecKeyPair, address);
  }

  static Credentials createPrivateAndPublicKey(
      String privateKey, String publicKey) {
    return createByECKeyPair(
        ECKeyPair(Numeric.toBigInt(privateKey), Numeric.toBigInt(publicKey)));
  }

  static Credentials createPrivate(String privateKey) {
    return createByECKeyPair(
        ECKeyPair.createByBigIntPrivateKey(Numeric.toBigInt(privateKey)));
  }

}
