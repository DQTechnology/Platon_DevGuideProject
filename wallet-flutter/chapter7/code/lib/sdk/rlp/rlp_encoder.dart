import 'dart:typed_data';
import 'package:digging/sdk/rlp/rlp_decoder.dart';
import 'package:digging/sdk/rlp/rlp_string.dart';

class RlpEncoder {
  static Uint8List encodeList(List values) {
    if (values.isEmpty) {
      return _encode(Uint8List.fromList([]), RlpDecoder.offsetShortList);
    } else {
      List<int> result = List.empty(growable: true);
      for (final entry in values) {
        result.addAll(encode(entry));
      }
      return _encode(Uint8List.fromList(result), RlpDecoder.offsetShortList);
    }
  }

  static Uint8List _encode(Uint8List bytesValue, int offset) {
    if (bytesValue.length == 1 &&
        offset == RlpDecoder.offsetShortString &&
        bytesValue[0] >= 0x00 &&
        bytesValue[0] <= 0x7f) {
      return bytesValue;
    } else if (bytesValue.length <= 55) {
      Uint8List result = Uint8List(bytesValue.length + 1);
      result[0] = offset + bytesValue.length;
      List.copyRange(result, 1, bytesValue, 0, bytesValue.length);
      return result;
    } else {
      Uint8List encodedStringLength = _toMinimalByteArray(bytesValue.length);

      Uint8List result =
          Uint8List(bytesValue.length + encodedStringLength.length + 1);

      result[0] = (offset + 0x37) + encodedStringLength.length;

      List.copyRange(
          result, 1, encodedStringLength, 0, encodedStringLength.length);
      List.copyRange(result, encodedStringLength.length + 1, bytesValue, 0,
          bytesValue.length);

      return result;
    }
  }

  static Uint8List _toMinimalByteArray(int value) {
    Uint8List encoded = _toByteArray(value);

    for (int i = 0; i < encoded.length; i++) {
      if (encoded[i] != 0) {
        return encoded.sublist(i, encoded.length);
      }
    }
    return Uint8List.fromList([]);
  }

  static Uint8List _toByteArray(int value) {
    return Uint8List.fromList([
      ((value >> 24) & 0xff),
      ((value >> 16) & 0xff),
      ((value >> 8) & 0xff),
      (value & 0xff)
    ]);
  }

  static Uint8List _encodeString(RlpString value) {
    return _encode(value.bytes, RlpDecoder.offsetShortString);
  }

  static Uint8List encode(dynamic value) {
    if (value is RlpString) {
      return _encodeString(value);
    } else if (value is List) {
      return encodeList(value);
    }
    throw UnsupportedError('$value cannot be rlp-encoded');
  }
}
