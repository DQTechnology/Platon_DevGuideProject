

import 'dart:math';

import 'package:digging/sdk/crypto/random_bridge.dart';
import 'package:pointycastle/api.dart';

class SecureRandomUtil {

  static final SecureRandom _SECURE_RANDOM = RandomBridge(Random.secure());



  static SecureRandom secureRandom() {
    return _SECURE_RANDOM;
  }


}