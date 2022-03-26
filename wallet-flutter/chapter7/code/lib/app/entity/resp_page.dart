class RespPage<T> {
  String errMsg = "";

  int code = 0;

  BigInt totalCount = BigInt.zero;

  BigInt displayTotalCount = BigInt.zero;

  BigInt totalPages = BigInt.zero;

  late List<T> data = [];

  RespPage({
    this.errMsg = "",
    this.code = 0,
  });

  static RespPage<T> fromMap<T>(Map<String, dynamic> jsonMap) {
    RespPage<T> rsp = RespPage<T>();
    rsp.errMsg = jsonMap["errMsg"];
    rsp.code = jsonMap["code"];
    rsp.totalCount = BigInt.from(jsonMap["totalCount"]);
    rsp.displayTotalCount = BigInt.from(jsonMap["displayTotalCount"]);
    rsp.totalPages = BigInt.from(jsonMap["totalPages"]);
    return rsp;
  }
}
