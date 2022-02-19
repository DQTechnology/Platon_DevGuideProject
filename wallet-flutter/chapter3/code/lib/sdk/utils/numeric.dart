import 'dart:typed_data';
import 'package:convert/convert.dart';
import 'package:pointycastle/src/utils.dart' as p_utils;

class Numeric {
  static const String _hexPrefix = "0x";

  /// 删除0x
  static String cleanHexPrefix(String hex) {
    if (hex.startsWith(_hexPrefix)) {
      return hex.substring(2);
    }
    return hex;
  }

  static String prependHexPrefix(String hex) {
    return !containsHexPrefix(hex) ? _hexPrefix + hex : hex;
  }

  static bool containsHexPrefix(String hex) {
    return hex.isNotEmpty && hex.length > 1 && hex[0] == '0' && hex[1] == 'x';
  }

  static String toHexStringWithPrefixZeroPadded(BigInt value, int size) {
    return _toHexStringZeroPadded(value, size, true);
  }

  static String _toHexStringZeroPadded(
      BigInt value, int size, bool withPrefix) {
    String result = toHexStringNoPrefix(value);
    int length = result.length;

    if (length > size) {
      throw Exception("value $result is larger then length $size");
    } else if (value.sign < 0) {
      throw Exception("Value cannot be negative");
    }

    if (length < size) {
      result = "0" * (size - length) + result;
    }
    if (withPrefix) {
      return _hexPrefix + result;
    }
    return result;
  }

  static String toHexStringNoPrefix(BigInt value) {
    return value.toRadixString(16);
  }

  /// 字节转换为16进制
  ///
  ///
  static String bytesToHex(List<int> bytes,
      {bool withPrefix = false,
      int? forcePadLength,
      bool padToEvenLength = false}) {
    var encoded = hex.encode(bytes);

    // 计算要填充的长度
    if (forcePadLength != null) {
      assert(forcePadLength >= encoded.length);
      final padding = forcePadLength - encoded.length;
      encoded = ('0' * padding) + encoded;
    }
    // 填充为偶数个
    if (padToEvenLength && encoded.length % 2 != 0) {
      encoded = '0$encoded';
    }
    return (withPrefix ? _hexPrefix : '') + encoded;
  }

  /// 16进制在缓缓为字节数组
  ///
  static Uint8List hexToBytes(String hexStr) {
    final bytes = hex.decode(cleanHexPrefix(hexStr));
    if (bytes is Uint8List) {
      return bytes;
    }
    return Uint8List.fromList(bytes);
  }

  static Uint8List unsignedIntToBytes(BigInt number) {
    assert(!number.isNegative);
    return p_utils.encodeBigIntAsUnsigned(number);
  }

  static BigInt bytesToUnsignedInt(Uint8List bytes) {
    return p_utils.decodeBigIntWithSign(1, bytes);
  }

  static BigInt bytesToInt(List<int> bytes) => p_utils.decodeBigInt(bytes);

  static Uint8List intToBytes(BigInt number) => p_utils.encodeBigInt(number);

  static BigInt toBigInt(String hex) {
    return BigInt.parse(cleanHexPrefix(hex), radix: 16);
  }

  static int hexToDartInt(String hex) {
    return int.parse(cleanHexPrefix(hex), radix: 16);
  }

  static bool _isValidHexQuantity(String value) {
    if (value.isEmpty) {
      return false;
    }
    if (value.length < 3) {
      return false;
    }
    if (!value.startsWith(_hexPrefix)) {
      return false;
    }

    return true;
  }

  static BigInt decodeQuantity(String value) {
    if (!_isValidHexQuantity(value)) {
      throw Exception("Value must be in format 0x[1-9]+[0-9]* or 0x0");
    }
    try {
      return BigInt.parse(value.substring(2), radix: 16);
    } catch (e) {
      throw Exception("Negative : ${e.toString()}");
    }
  }

  static String encodeQuantity(BigInt value) {
    if (value.sign != -1) {
      return _hexPrefix + value.toRadixString(16);
    } else {
      throw Exception("Negative values are not supported");
    }
  }
}
