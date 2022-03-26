import 'dart:typed_data';
import 'package:typed_data/typed_buffers.dart';
import 'package:digging/sdk/crypto/eckeypair.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'package:pointycastle/ecc/api.dart';

import 'hash.dart';

class SignatureData {
  final Uint8List _v;
  final Uint8List _r;
  final Uint8List _s;

  SignatureData(this._v, this._r, this._s);

  Uint8List get V {
    return _v;
  }

  Uint8List get R {
    return _r;
  }

  Uint8List get S {
    return _s;
  }
}

class Sign {
  static SignatureData signMessage(Uint8List message, ECKeyPair keyPair) {
    return signMessageWithHash(message, keyPair, true);
  }

  static SignatureData signMessageWithHash(
      Uint8List message, ECKeyPair keyPair, bool needToHash) {
    BigInt publicKey = keyPair.getPublicKey();

    Uint8List messageHash;

    if (needToHash) {
      messageHash = Hash.sha3(message);
    } else {
      messageHash = message;
    }

    ECSignature sig = keyPair.sign(messageHash);

    var recId = -1;
    for (var i = 0; i < 4; i++) {
      final k = recoverFromSignature(i, sig, messageHash, ECKeyPair.curve);
      if (k == publicKey) {
        recId = i;
        break;
      }
    }

    if (recId == -1) {
      throw Exception(
          'Could not construct a recoverable key. This should never happen');
    }

    int headerByte = recId + 27;
    Uint8Buffer uint8buffer = Uint8Buffer();

    uint8buffer.add(headerByte);

    Uint8List v = uint8buffer.buffer.asUint8List(0, uint8buffer.length);

    Uint8List r = Numeric.unsignedIntToBytes(sig.r);
    Uint8List s = Numeric.unsignedIntToBytes(sig.s);

    return SignatureData(v, r, s);
  }

  static BigInt? recoverFromSignature(
      int recId, ECSignature sig, Uint8List msg, ECDomainParameters params) {
    final n = params.n;
    final i = BigInt.from(recId ~/ 2);
    final x = sig.r + (i * n);

    //Parameter q of curve
    final prime = BigInt.parse(
        'fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f',
        radix: 16);
    if (x.compareTo(prime) >= 0) return null;

    final R = _decompressKey(x, (recId & 1) == 1, params.curve);
    if (!(R * n)!.isInfinity) return null;

    final e = Numeric.bytesToUnsignedInt(msg);

    final eInv = (BigInt.zero - e) % n;
    final rInv = sig.r.modInverse(n);
    final srInv = (rInv * sig.s) % n;
    final eInvrInv = (rInv * eInv) % n;

    final q = (params.G * eInvrInv)! + (R * srInv);

    final bytes = q!.getEncoded(false);
    return Numeric.bytesToUnsignedInt(bytes.sublist(1));
  }

  static ECPoint _decompressKey(BigInt xBN, bool yBit, ECCurve c) {
    List<int> x9IntegerToBytes(BigInt s, int qLength) {
      final bytes = Numeric.intToBytes(s);
      if (qLength < bytes.length) {
        return bytes.sublist(0, bytes.length - qLength);
      } else if (qLength > bytes.length) {
        final tmp = List<int>.filled(qLength, 0);

        final offset = qLength - bytes.length;
        for (var i = 0; i < bytes.length; i++) {
          tmp[i + offset] = bytes[i];
        }
        return tmp;
      }
      return bytes;
    }

    final compEnc = x9IntegerToBytes(xBN, 1 + ((c.fieldSize + 7) ~/ 8));
    compEnc[0] = yBit ? 0x03 : 0x02;
    return c.decodePoint(compEnc)!;
  }

  static Uint8List publicKeyFromPrivate(BigInt privateKey) {
    if (privateKey.bitLength > ECKeyPair.curve.n.bitLength) {
      privateKey = privateKey % ECKeyPair.curve.n;
    }
    final p = (ECKeyPair.curve.G * privateKey)!;

    return Uint8List.view(p.getEncoded(false).buffer, 1);
  }
}
