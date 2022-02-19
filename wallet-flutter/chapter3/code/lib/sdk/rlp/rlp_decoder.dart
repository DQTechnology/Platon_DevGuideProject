import 'dart:typed_data';

import 'package:digging/sdk/rlp/rlp_string.dart';

class RlpDecoder {
  /// [0x80]
  /// If a string is 0-55 bytes long, the RLP encoding consists of a single
  /// byte with value 0x80 plus the length of the string followed by the
  /// string. The range of the first byte is thus [0x80, 0xb7].
  static int offsetShortString = 0x80;

  /// [0xb7]
  /// If a string is more than 55 bytes long, the RLP encoding consists of a
  /// single byte with value 0xb7 plus the length of the length of the string
  /// in binary form, followed by the length of the string, followed by the
  /// string. For example, a length-1024 string would be encoded as
  /// \xb9\x04\x00 followed by the string. The range of the first byte is thus
  /// [0xb8, 0xbf].
  static int offsetLongString = 0xb7;

  /// [0xc0]
  /// If the total payload of a list (i.e. the combined length of all its
  /// items) is 0-55 bytes long, the RLP encoding consists of a single byte
  /// with value 0xc0 plus the length of the list followed by the concatenation
  /// of the RLP encodings of the items. The range of the first byte is thus
  /// [0xc0, 0xf7].
  static int offsetShortList = 0xc0;

  /// [0xf7]
  /// If the total payload of a list is more than 55 bytes long, the RLP
  /// encoding consists of a single byte with value 0xf7 plus the length of the
  /// length of the list in binary form, followed by the length of the list,
  /// followed by the concatenation of the RLP encodings of the items. The
  /// range of the first byte is thus [0xf8, 0xff].
  static int offsetLongList = 0xf7;

  static List<dynamic> decode(Uint8List rlpEncoded) {
    List<dynamic> rlpList = List.empty(growable: true);

    _traverse(rlpEncoded, 0, rlpEncoded.length, rlpList);

    return rlpList;
  }

  static void _traverse(
      Uint8List data, int startPos, int endPos, List<dynamic> rlpList) {
    try {
      if (data.isEmpty) {
        return;
      }

      while (startPos < endPos) {
        int prefix = data[startPos] & 0xff;

        if (prefix < offsetShortString) {
          // 1. the data is a string if the range of the
          // first byte(i.e. prefix) is [0x00, 0x7f],
          // and the string is the first byte itself exactly;

          Uint8List rplData = Uint8List.fromList([prefix]);
          rlpList.add(rplData);
          startPos += 1;
        } else if (prefix == offsetShortString) {
          // null
          rlpList.add(RlpString.create(Uint8List(0)));
          startPos += 1;
        } else if (prefix > offsetShortString && prefix <= offsetLongString) {
          // 2. the data is a string if the range of the
          // first byte is [0x80, 0xb7], and the string
          // which length is equal to the first byte minus 0x80
          // follows the first byte;

          int strLen = prefix - offsetShortString;
          Uint8List rplData = Uint8List(strLen);
          //
          List.copyRange(rplData, 0, data, startPos + 1, strLen);

          rlpList.add(rplData);

          startPos += (1 + strLen);
        } else if (prefix > offsetLongString && prefix <= offsetShortList) {
          // 3. the data is a string if the range of the
          // first byte is [0xb8, 0xbf], and the length of the
          // string which length in bytes is equal to the
          // first byte minus 0xb7 follows the first byte,
          // and the string follows the length of the string;

          int lenOfStrLen = prefix - offsetLongString;

          int strLen = _calcLength(lenOfStrLen, data, startPos);

          Uint8List rplData = Uint8List(strLen);

          List.copyRange(rplData, 0, data, startPos + lenOfStrLen + 1, strLen);

          rlpList.add(rplData);

          startPos += lenOfStrLen + strLen + 1;
        } else if (prefix > offsetShortList && prefix <= offsetLongList) {
          // 4. the data is a list if the range of the
          // first byte is [0xc0, 0xf7], and the concatenation of
          // the RLP encodings of all items of the list which the
          // total payload is equal to the first byte minus 0xc0 follows the first byte;

          int listLen = prefix - offsetShortList;

          List<dynamic> newLevelList = List.empty(growable: true);

          _traverse(data, startPos + 1, startPos + listLen + 1, newLevelList);

          rlpList.add(newLevelList);

          startPos += 1 + listLen;
        } else if (prefix > offsetLongList) {
          // 5. the data is a list if the range of the
          // first byte is [0xf8, 0xff], and the total payload of the
          // list which length is equal to the
          // first byte minus 0xf7 follows the first byte,
          // and the concatenation of the RLP encodings of all items of
          // the list follows the total payload of the list;

          int lenOfListLen = prefix - offsetLongList;

          int listLen = _calcLength(lenOfListLen, data, startPos);

          List<dynamic> newLevelList = List.empty(growable: true);

          _traverse(data, startPos + lenOfListLen + 1,
              startPos + lenOfListLen + listLen + 1, newLevelList);

          rlpList.add(newLevelList);

          startPos += lenOfListLen + listLen + 1;
        }
      }
    } catch (e) {
      throw Exception("RLP wrong encoding: ${e.toString()}");
    }
  }

  static int _calcLength(int lengthOfLength, Uint8List data, int pos) {
    int pow = (lengthOfLength - 1);
    int length = 0;
    for (int i = 1; i <= lengthOfLength; ++i) {
      length += (data[pos + i] & 0xff) << (8 * pow);
      pow--;
    }
    return length;
  }
}
