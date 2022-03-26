class GasProvider {
  late BigInt _gasPrice;
  late  BigInt _gasLimit;

  GasProvider(BigInt gasPrice, BigInt gasLimit) {
    _gasPrice = gasPrice;
    _gasLimit = gasLimit;
  }

  BigInt get gasLimit => _gasLimit;

  set gasLimit(BigInt value) {
    _gasLimit = value;
  }

  BigInt get gasPrice => _gasPrice;

  set gasPrice(BigInt value) {
    _gasPrice = value;
  }
}
