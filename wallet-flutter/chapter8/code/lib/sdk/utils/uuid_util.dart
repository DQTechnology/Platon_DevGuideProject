import 'dart:typed_data';

import 'package:digging/sdk/utils/numeric.dart';
import 'package:uuid/uuid.dart';

class UUIDUtil {

  static  const  _uuid = Uuid();


  /// Formats the [uuid] bytes as an uuid.
  static  String formatUuid(List<int> uuid) => Uuid.unparse(uuid);

  /// Generates a v4 uuid.
  static  Uint8List generateUuidV4() {
    final buffer = Uint8List(16);
    _uuid.v4buffer(buffer);
    return buffer;
  }

  static  Uint8List parseUuid(String uuid) {
    // Unfortunately, package:uuid is to strict when parsing uuids, the example
    // ids don't work
    String withoutDashes = uuid.replaceAll('-', '');

    return Numeric.hexToBytes(withoutDashes);
  }



}