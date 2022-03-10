import 'dart:typed_data';
import 'package:typed_data/typed_buffers.dart';
import 'package:digging/sdk/utils/numeric.dart';

class RlpString {
  late final Uint8List _value;

  static final Uint8List emptyArray = Uint8List.fromList([]);

  RlpString(Uint8List value) {
    _value = value;
  }

  Uint8List get bytes {
    return _value;
  }

  BigInt asPositiveBigInteger() {
    if (_value.isEmpty) {
      return BigInt.zero;
    }
    return Numeric.bytesToUnsignedInt(_value);
  }

  String asString() {
    return Numeric.bytesToHex(_value);
  }

  static RlpString create(Uint8List value) {
    return RlpString(value);
  }

  static RlpString createByByte(int value) {
    Uint8Buffer uint8buffer = Uint8Buffer();
    uint8buffer.add(value);
    return RlpString(uint8buffer.buffer.asUint8List(0, uint8buffer.length));
  }

  static RlpString createByBigInt(BigInt value) {
    if (value.sign < 1) {
      return RlpString(emptyArray);
    } else {
      Uint8List bytes = Numeric.intToBytes(value);

      if (bytes[0] == 0) {
        return RlpString(bytes.sublist(1, bytes.length));
      }

      return RlpString(bytes);
    }
  }

  static RlpString createByString(String value) {
    return RlpString(Uint8List.fromList(value.codeUnits));
  }
}
