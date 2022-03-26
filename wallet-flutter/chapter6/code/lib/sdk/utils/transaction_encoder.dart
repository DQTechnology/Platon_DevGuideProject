import 'dart:ffi';
import 'dart:typed_data';

import 'package:digging/sdk/crypto/bech32.dart';
import 'package:digging/sdk/crypto/credentials.dart';
import 'package:digging/sdk/crypto/raw_transaction.dart';
import 'package:digging/sdk/crypto/sign.dart';
import 'package:digging/sdk/rlp/rlp_string.dart';
import 'package:digging/sdk/rlp/rlp_encoder.dart';
import 'package:digging/sdk/utils/numeric.dart';
import 'bytes.dart';

class TransactionEncoder {
  static final BigInt _LOWER_REAL_V = BigInt.from(27);
  static final BigInt _CHAIN_ID_INC = BigInt.from(35);

  static Uint8List signMessage(
      RawTransaction rawTransaction, Credentials credentials) {
    Uint8List encodedTransaction = encode(rawTransaction);
    SignatureData signatureData =
        Sign.signMessage(encodedTransaction, credentials.ecKeyPair);
    return encodeWithSignatureData(rawTransaction, signatureData);
  }

  static Uint8List signMessageWithChainId(
      RawTransaction rawTransaction, Credentials credentials, BigInt chainId) {
    Uint8List encodedTransaction = encodeWithChainId(rawTransaction, chainId);

    SignatureData signatureData =
        Sign.signMessage(encodedTransaction, credentials.ecKeyPair);

    SignatureData eip155SignatureData =
        createEip155SignatureData(signatureData, chainId);

    return encodeWithSignatureData(rawTransaction, eip155SignatureData);
  }

  static SignatureData createEip155SignatureData(
      SignatureData signatureData, BigInt chainId) {
    BigInt v = Numeric.bytesToInt(signatureData.V);
    v = v - _LOWER_REAL_V;
    v = v + (chainId * BigInt.from(2));
    v = v + _CHAIN_ID_INC;

    return SignatureData(
        Numeric.unsignedIntToBytes(v), signatureData.R, signatureData.S);
  }

  static Uint8List encodeWithChainId(
      RawTransaction rawTransaction, BigInt chainId) {
    final Uint8List emptyArray = Uint8List.fromList([]);

    SignatureData signatureData =
        SignatureData(_bigIntToBytes(chainId), emptyArray, emptyArray);

    return encodeWithSignatureData(rawTransaction, signatureData);
  }

  static Uint8List _bigIntToBytes(BigInt chainId) {
    Uint8List bytes = Numeric.intToBytes(chainId);

    int paddingSize = 8 - bytes.length;

    if (paddingSize <= 0) {
      return bytes;
    }

    return Uint8List.fromList(List.filled(paddingSize, 0) + bytes);
  }

  static Uint8List encode(RawTransaction rawTransaction) {
    return _encode(rawTransaction, null);
  }

  static Uint8List encodeWithSignatureData(
      RawTransaction rawTransaction, SignatureData signature) {
    return _encode(rawTransaction, signature);
  }

  static Uint8List _encode(
      RawTransaction rawTransaction, SignatureData? signatureData) {
    List<dynamic> rlpList = asRlpValues(rawTransaction, signatureData);

    return RlpEncoder.encode(rlpList);
  }

  static List<dynamic> asRlpValues(
      RawTransaction rawTransaction, SignatureData? signatureData) {
    List<dynamic> result = List.empty(growable: true);

    result.add(RlpString.createByBigInt(rawTransaction.nonce));
    result.add(RlpString.createByBigInt(rawTransaction.gasPrice));
    result.add(RlpString.createByBigInt(rawTransaction.gasLimit));

    // an empty to address (contract creation) should not be encoded as a numeric 0 value
    String to = rawTransaction.to;
    if (to.isNotEmpty) {
      // addresses that start with zeros should be encoded with the zeros included, not
      // as numeric values
      result.add(RlpString.create(Bech32.addressDecode(to)));
    } else {
      result.add(RlpString.createByString(""));
    }
    result.add(RlpString.createByBigInt(rawTransaction.value));

    // value field will already be hex encoded, so we need to convert into binary first
    Uint8List data = Numeric.hexToBytes(rawTransaction.data);
    result.add(RlpString.create(data));

    if (signatureData != null) {
      result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.V)));
      result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.R)));
      result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.S)));
    }

    return result;
  }
}
