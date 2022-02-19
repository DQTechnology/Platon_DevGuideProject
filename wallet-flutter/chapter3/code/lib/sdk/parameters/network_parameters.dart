import 'package:digging/sdk/crypto/bech32.dart';

import 'inner_contracts.dart';

class ReservedChainId {
  static final PlatON = BigInt.from(100);
  static final Alaya = BigInt.from(201018);
}

class ReservedHrp {
  static const PlatON = "lat";
  static const Alaya = "atp";
}

class NetworkParameters {

  final BigInt _chainId;

  final String _hrp;

  static final String _alayaNetworkKey =
      ReservedChainId.Alaya.toString() + ":" + ReservedHrp.Alaya;
  static final String _platonNetworkKey =
      ReservedChainId.PlatON.toString() + ":" + ReservedHrp.PlatON;

  static final Map<String, NetworkParameters> networksContainer = {
    _alayaNetworkKey:
        NetworkParameters(ReservedChainId.Alaya, ReservedHrp.Alaya),
    _platonNetworkKey:
        NetworkParameters(ReservedChainId.PlatON, ReservedHrp.PlatON)
  };

  static NetworkParameters currentNetwork =
      networksContainer[_alayaNetworkKey]!;

  //锁仓合约地址
  late String _pposContractAddressOfRestrictingPlan;

  //staking合约地址
  late String _pposContractAddressOfStaking;

  //激励池地址
  late String _pposContractAddressOfIncentivePool;

  //惩罚合约地址
  late String _pposContractAddressOfSlash;

  //治理合约地址
  late String _pposContractAddressOfProposal;

  //委托收益合约地址
  late String _pposContractAddressOfReward;

  NetworkParameters(this._chainId, this._hrp) {
    _pposContractAddressOfRestrictingPlan =
        Bech32.addressEncode(_hrp, InnerContracts.getRestrictingAddr);
    _pposContractAddressOfStaking =
        Bech32.addressEncode(_hrp, InnerContracts.getStakingAddr);
    _pposContractAddressOfIncentivePool =
        Bech32.addressEncode(_hrp, InnerContracts.getRewardManagerPoolAddr);
    _pposContractAddressOfSlash =
        Bech32.addressEncode(_hrp, InnerContracts.getSlashingAddr);
    _pposContractAddressOfProposal =
        Bech32.addressEncode(_hrp, InnerContracts.getGovAddr);
    _pposContractAddressOfReward =
        Bech32.addressEncode(_hrp, InnerContracts.getDelegateRewardPoolAddr);
  }

  static BigInt get getChainId {
    return currentNetwork._chainId;
  }

  static String get getHrp {
    return currentNetwork._hrp;
  }

  BigInt get getCurrentChainId {
    return _chainId;
  }

  String get getCurrentHrp {
    return _hrp;
  }

  static String get getPposContractAddressOfRestrctingPlan {
    return currentNetwork._pposContractAddressOfRestrictingPlan;
  }

  static String get getPposContractAddressOfStaking {
    return currentNetwork._pposContractAddressOfStaking;
  }

  static String get getPposContractAddressOfIncentivePool {
    return currentNetwork._pposContractAddressOfIncentivePool;
  }

  static String get getPposContractAddressOfSlash {
    return currentNetwork._pposContractAddressOfSlash;
  }

  static String get getPposContractAddressOfProposal {
    return currentNetwork._pposContractAddressOfProposal;
  }

  static String get getPposContractAddressOfReward {
    return currentNetwork._pposContractAddressOfReward;
  }

  static void init(BigInt chainId, String hrp) {
    if (networksContainer.containsKey(chainId.toString() + ":" + hrp)) {
      return;
    }
    //if the chainID = 201018L, the hrp should be atp.
    if (chainId == ReservedChainId.Alaya &&
        ReservedHrp.Alaya.compareTo(hrp) == 0) {
      throw Exception("hrp not match to chainID");
    }
    //if the chainID = 100L, the hrp should be lat.
    if (chainId == ReservedChainId.PlatON &&
        ReservedHrp.PlatON.compareTo(hrp) == 0) {
      throw Exception("hrp not match to chainID");
    }

    if (Bech32.verifyHrp(hrp)) {
      NetworkParameters network = NetworkParameters(chainId, hrp);
      networksContainer[chainId.toString() + ":" + hrp] = network;
      currentNetwork = network;
    } else {
      throw Exception("hrp is invalid");
    }
  }

  static void selectNetwork(BigInt chainId, String hrp) {
    if (networksContainer.length == 1) {
      //currentNetwork cannot switch to another if only one network has been initialized.
      return;
    }
    currentNetwork = networksContainer[chainId.toString() + ":" + hrp]!;
  }

  static void selectAlaya() {
    currentNetwork = networksContainer[_alayaNetworkKey]!;
  }

  /// switch to the PlatON network
  static void selectPlatON() {
    currentNetwork = networksContainer[_platonNetworkKey]!;
  }
}
