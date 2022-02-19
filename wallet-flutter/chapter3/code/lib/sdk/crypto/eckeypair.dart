import 'dart:typed_data';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:pointycastle/api.dart';
import 'package:pointycastle/digests/sha256.dart';
import 'package:pointycastle/ecc/api.dart';
import 'package:pointycastle/ecc/curves/secp256k1.dart';
import 'package:pointycastle/macs/hmac.dart';
import 'package:pointycastle/signers/ecdsa_signer.dart';

class ECKeyPair {
  final BigInt _privateKey;

  final BigInt _publicKey;

  ECKeyPair(this._privateKey, this._publicKey);

  static final ECDomainParameters curve = ECCurve_secp256k1();

  static final BigInt halfCurveOrder = curve.n >> 1;

  BigInt getPrivateKey() {
    return _privateKey;
  }

  BigInt getPublicKey() {
    return _publicKey;
  }

  ECSignature sign(Uint8List transactionHash) {
    final signer = ECDSASigner(null, HMac(SHA256Digest(), 64));

    final key = ECPrivateKey(_privateKey, curve);

    signer.init(true, PrivateKeyParameter(key));

    var sig = signer.generateSignature(transactionHash) as ECSignature;

    if (sig.s.compareTo(halfCurveOrder) > 0) {
      final cannibalisedS = curve.n - sig.s;
      sig = ECSignature(sig.r, cannibalisedS);
    }
    return sig;
  }

  static ECKeyPair createByHexPrivateKey(String privateKey) {

    Uint8List privateKeyBytes = Numeric.hexToBytes(privateKey);

    return createByU8ListPrivateKey(privateKeyBytes);
  }

  static ECKeyPair createByBigIntPrivateKey(BigInt privateKey) {

    return ECKeyPair(privateKey,
        Numeric.bytesToUnsignedInt(_getPublicByPrivateKey(privateKey)));
  }

  static Uint8List _getPublicByPrivateKey(BigInt privateKey) {
    final p = (ECKeyPair.curve.G * privateKey)!;

    return Uint8List.view(p.getEncoded(false).buffer, 1);
  }

  static ECKeyPair createByU8ListPrivateKey(Uint8List privateKey) {
    return createByBigIntPrivateKey(Numeric.bytesToUnsignedInt(privateKey));
  }
}
