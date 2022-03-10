import 'package:digging/sdk/utils/numeric.dart';

class PlatonBlockInfo {
  late String number;

  late String hash;

  late String parentHash;

  late String nonce;

  late String logsBloom;

  late String transactionsRoot;

  late String stateRoot;

  late String miner;

  late String extraData;

  late String size;

  late String gasLimit;

  late String gasUsed;

  late String timestamp;

  PlatonBlockInfo(
      this.number,
      this.hash,
      this.parentHash,
      this.nonce,
      this.logsBloom,
      this.transactionsRoot,
      this.stateRoot,
      this.miner,
      this.extraData,
      this.size,
      this.gasLimit,
      this.gasUsed,
      this.timestamp);

  static PlatonBlockInfo fromJson(Map<String, dynamic> json) {
    String number = Numeric.decodeQuantity(json["number"]).toString();

    String size = Numeric.decodeQuantity(json["size"]).toString();

    String gasLimit = Numeric.decodeQuantity(json["gasLimit"]).toString();

    String gasUsed = Numeric.decodeQuantity(json["gasUsed"]).toString();

    String timestamp = Numeric.decodeQuantity(json["timestamp"]).toString();
    PlatonBlockInfo platonBlockInfo = PlatonBlockInfo(
        number,
        json["hash"],
        json["parentHash"],
        json["nonce"],
        json["logsBloom"],
        json["transactionsRoot"],
        json["stateRoot"],
        json["miner"],
        json["extraData"],
        size,
        gasLimit,
        gasUsed,
        timestamp);

    return platonBlockInfo;
  }
}
