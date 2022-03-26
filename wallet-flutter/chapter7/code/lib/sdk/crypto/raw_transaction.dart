class RawTransaction {
  final BigInt _nonce;
  final BigInt _gasPrice;
  final BigInt _gasLimit;
  final String _to;
  final BigInt _value;
  final String _data;

  RawTransaction(this._nonce, this._gasPrice, this._gasLimit, this._to,
      this._value, this._data);

  String get data => _data;

  BigInt get value => _value;

  String get to => _to;

  BigInt get gasLimit => _gasLimit;

  BigInt get gasPrice => _gasPrice;

  BigInt get nonce => _nonce;
}
