import 'dart:convert';

class TransactionListParam {
  String address;
  int pageNo;
  int pageSize;
  String txType;

  TransactionListParam(this.address, this.pageNo, this.pageSize,
      {this.txType = ""});


  String toJson() {

    Map<String, dynamic> map = {
      "address": address,
      "pageNo": pageNo,
      "pageSize": pageSize,
      "txType": txType,
    };

    return json.encode(map);
  }
}
