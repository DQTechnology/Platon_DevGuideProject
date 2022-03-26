
import 'dart:convert';

class AliveStakingListReq {
  String key;
  int pageNo;
  int pageSize;
  String queryStatus;

  AliveStakingListReq(this.pageNo, this.pageSize,
      {this.key = "", this.queryStatus = "all"});

  String toJson() {

    Map<String, dynamic> map = {
      "key": key,
      "pageNo": pageNo,
      "pageSize": pageSize,
      "queryStatus": queryStatus,
    };
    return json.encode(map);
  }
}