
import 'dart:typed_data';

import 'package:digging/sdk/utils/numeric.dart';
import 'package:pointycastle/digests/keccak.dart';
import 'package:pointycastle/digests/sha256.dart';

class Hash {

  static final KeccakDigest _keccakDigest = KeccakDigest(256);

  static final SHA256Digest _sha256digest = SHA256Digest();


  static Uint8List sha256(Uint8List input) {
    _sha256digest.reset();
    return _sha256digest.process(input);
  }

  static Uint8List sha3(Uint8List input) {
    _keccakDigest.reset();
    return _keccakDigest.process(input);
  }

  static Uint8List sha3Hex(String input) {
    return sha3(Numeric.hexToBytes(input));
  }

  static String sha3HexToString(String input, {bool withPrefix = true}) {

    return Numeric.bytesToHex(sha3Hex(input),
        withPrefix: withPrefix);
  }

}
