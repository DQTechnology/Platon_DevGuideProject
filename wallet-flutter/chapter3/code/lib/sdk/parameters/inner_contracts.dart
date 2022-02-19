import 'package:digging/sdk/crypto/bech32.dart';

class InnerContracts {
  static String _RestrictingAddr = "0x1000000000000000000000000000000000000001";
  static String _StakingAddr = "0x1000000000000000000000000000000000000002";
  static String _RewardManagerPoolAddr =
      "0x1000000000000000000000000000000000000003";
  static String _SlashingAddr = "0x1000000000000000000000000000000000000004";
  static String _GovAddr = "0x1000000000000000000000000000000000000005";
  static String _DelegateRewardPoolAddr =
      "0x1000000000000000000000000000000000000006";

  static final List<String> _innerAddrList = List.from([
    _RestrictingAddr,
    _StakingAddr,
    _RewardManagerPoolAddr,
    _SlashingAddr,
    _GovAddr,
    _DelegateRewardPoolAddr
  ]);

  static String get getRestrictingAddr => _RestrictingAddr;

  static String get getStakingAddr => _StakingAddr;

  static String get getRewardManagerPoolAddr => _RewardManagerPoolAddr;

  static String get getSlashingAddr => _SlashingAddr;

  static String get getGovAddr => _GovAddr;

  static String get getDelegateRewardPoolAddr => _DelegateRewardPoolAddr;

  static bool isInnerAddr(String address) {
    if (_innerAddrList.contains(address)) {
      return true;
    } else {
      return _innerAddrList.contains(Bech32.addressDecodeHex(address));
    }
  }
}
