
import 'dart:convert';

class DelegationListByAddressReq {
  int pageNo;
  int pageSize;
  String address;

  DelegationListByAddressReq(this.pageNo, this.pageSize, this.address);


  String toJson() {

    Map<String, dynamic> map = {
      "pageNo": pageNo,
      "pageSize": pageSize,
      "address": address,
    };
    return json.encode(map);
  }

}