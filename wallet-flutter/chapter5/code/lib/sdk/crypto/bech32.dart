import 'dart:typed_data';
import 'package:typed_data/typed_buffers.dart';
import 'package:digging/sdk/utils/numeric.dart';

class Bech32Data {
  String hrp;
  Uint8List data;

  Bech32Data(this.hrp, this.data);
}

class Bech32 {
  static const String _HRP_LAT = "lat";
  static const int _ADDRESS_SIZE = 160;
  static const String _CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

  static const int _ADDRESS_LENGTH_IN_HEX = _ADDRESS_SIZE >> 2;
  static const _CHARSET_REV = [
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    15,
    -1,
    10,
    17,
    21,
    20,
    26,
    30,
    7,
    5,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    29,
    -1,
    24,
    13,
    25,
    9,
    8,
    23,
    -1,
    18,
    22,
    31,
    27,
    19,
    -1,
    1,
    0,
    3,
    16,
    11,
    28,
    12,
    14,
    6,
    4,
    2,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    29,
    -1,
    24,
    13,
    25,
    9,
    8,
    23,
    -1,
    18,
    22,
    31,
    27,
    19,
    -1,
    1,
    0,
    3,
    16,
    11,
    28,
    12,
    14,
    6,
    4,
    2,
    -1,
    -1,
    -1,
    -1,
    -1
  ];

  static String addressDecodeHex(final String str) {
    Uint8List bytes = addressDecode(str);

    return Numeric.toHexStringWithPrefixZeroPadded(
        Numeric.bytesToInt(bytes), _ADDRESS_LENGTH_IN_HEX);
  }

  static String addressEncodeUint8List(final String hrp, Uint8List bytes) {
    return encode(hrp, _convertBits(bytes, 8, 5, true));
  }

  static String addressEncode(final String hrp, String hexAddress) {
    return addressEncodeUint8List(hrp, Numeric.hexToBytes(hexAddress));
  }

  static String encode(String hrp, final Uint8List bech32Values) {
    assert(hrp.isNotEmpty, "Human-readable part is too short");
    assert(hrp.length <= 83, "Human-readable part is too long");

    hrp = hrp.toLowerCase();

    Uint8List checksum = _createChecksum(hrp, bech32Values);

    Uint8List combined = Uint8List(bech32Values.length + checksum.length);

    List.copyRange(combined, 0, bech32Values, 0, bech32Values.length);

    List.copyRange(combined, bech32Values.length, checksum, 0, checksum.length);

    String result = hrp + "1";
    for (final b in combined) {
      result += _CHARSET[b];
    }
    return result;
  }

  static Uint8List _createChecksum(final String hrp, final Uint8List values) {
    Uint8List hrpExpanded = _expandHrp(hrp);
    Uint8List enc = Uint8List(hrpExpanded.length + values.length + 6);

    List.copyRange(enc, 0, hrpExpanded, 0, hrpExpanded.length);

    List.copyRange(enc, hrpExpanded.length, values, 0, values.length);

    int mod = _ploymod(enc) ^ 1;

    Uint8List ret = Uint8List(6);
    for (int i = 0; i < 6; ++i) {
      ret[i] = ((mod >>> (5 * (5 - i))) & 31);
    }
    return ret;
  }

  static Uint8List addressDecode(final String str) {
    Bech32Data bech32data = decode(str);

    return _convertBits(bech32data.data, 5, 8, false);
  }

  /// Helper for re-arranging bits into groups.
  static Uint8List _convertBits(final Uint8List data, final int fromBits,
      final int toBits, final bool pad) {
    int acc = 0;
    int bits = 0;

    Uint8Buffer out = Uint8Buffer();

    final int maxv = (1 << toBits) - 1;
    final int maxAcc = (1 << (fromBits + toBits - 1)) - 1;
    for (int i = 0; i < data.length; ++i) {
      int value = data[i] & 0xff;
      if ((value >>> fromBits) != 0) {
        throw Exception(
            "Input value '${value.toRadixString(16)}' exceeds '$fromBits' bit size");
      }
      acc = ((acc << fromBits) | value) & maxAcc;
      bits += fromBits;
      while (bits >= toBits) {
        bits -= toBits;
        out.add((acc >>> bits) & maxv);
      }
    }
    if (pad) {
      if (bits > 0) {
        out.add((acc << (toBits - bits)) & maxv);
      }
    } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
      throw Exception("Could not convert bits, invalid padding");
    }
    return out.buffer.asUint8List(0, out.length);
  }

  /// 把地址解析成bech32
  static Bech32Data decode(final String str) {
    final int pos = str.lastIndexOf('1');
    final int dataPartLength = str.length - 1 - pos;
    Uint8List values = Uint8List(dataPartLength);
    for (int i = 0; i < dataPartLength; ++i) {
      final c = str.codeUnitAt(i + pos + 1);
      values[i] = _CHARSET_REV[c];
    }
    String hrp = str.substring(0, pos).toLowerCase();

    if (!_verifyChecksum(hrp, values)) {
      throw Exception();
    }
    //
    Uint8List newValues = Uint8List(values.length - 6);
    List.copyRange(newValues, 0, values, 0, values.length - 6);
    return Bech32Data(hrp, newValues);
  }

  static bool _verifyChecksum(final String hrp, final Uint8List values) {
    Uint8List hrpExpanded = _expandHrp(hrp);
    Uint8List combined = Uint8List(hrpExpanded.length + values.length);

    List.copyRange(combined, 0, hrpExpanded, 0, hrpExpanded.length);

    List.copyRange(combined, hrpExpanded.length, values, 0, values.length);

    return _ploymod(combined) == 1;
  }

  /// Find the polynomial with value coefficients mod the generator as 30-bit.
  static int _ploymod(final Uint8List values) {
    int c = 1;
    for (final vI in values) {
      int c0 = (c >>> 25) & 0xff;

      c = ((c & 0x1ffffff) << 5) ^ (vI & 0xff);
      if ((c0 & 1) != 0) {
        c ^= 0x3b6a57b2;
      }
      if ((c0 & 2) != 0) {
        c ^= 0x26508e6d;
      }
      if ((c0 & 4) != 0) {
        c ^= 0x1ea119fa;
      }
      if ((c0 & 8) != 0) {
        c ^= 0x3d4233dd;
      }
      if ((c0 & 16) != 0) {
        c ^= 0x2a1462b3;
      }
    }

    return c;
  }

  /// Expand a HRP for use in checksum computation.
  static Uint8List _expandHrp(final String hrp) {
    int hrpLength = hrp.length;
    Uint8List ret = Uint8List(hrpLength * 2 + 1);
    for (int i = 0; i < hrpLength; ++i) {
      /// 限制为标准的7位的ASCII
      int c = hrp.codeUnitAt(i) & 0x7f;
      ret[i] = (c >>> 5) & 0x07;
      ret[i + hrpLength + 1] = c & 0x1f;
    }
    ret[hrpLength] = 0;
    return ret;
  }

  static bool verifyHrp(String hrp) {
    if (hrp.isEmpty) {
      return false;
    }
    if (hrp.length != 3) {
      return false;
    }
    for (final ch in hrp.codeUnits) {
      if (ch < 33 || ch > 126) {
        return false;
      }
    }
    return true;
  }
}
