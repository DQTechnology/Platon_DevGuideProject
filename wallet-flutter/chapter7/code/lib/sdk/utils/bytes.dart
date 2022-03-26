import 'dart:typed_data';

class Bytes {
  static Uint8List trimLeadingBytes(Uint8List bytes, int b) {
    int offset = 0;
    for (; offset < bytes.length - 1; offset++) {
      if (bytes[offset] != b) {
        break;
      }
    }

    return bytes.sublist(offset, bytes.length);
  }

  static Uint8List trimLeadingZeroes(Uint8List bytes) {
    return trimLeadingBytes(bytes, 0);
  }
}
