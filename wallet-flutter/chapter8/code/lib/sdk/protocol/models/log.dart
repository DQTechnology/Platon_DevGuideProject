class Log {
  bool removed = false;
  String logIndex = "";
  String transactionIndex = "";
  String transactionHash = "";
  String blockHash = "";
  String blockNumber = "";
  String address = "";
  String data = "";
  String type = "";
  List<String> topics = List.empty();


  static List<Log> fromList(List<dynamic> dataList) {
    List<Log> logs = List.empty(growable: true);

    for (Map<String, dynamic> jsonMap in dataList) {
      Log log = fromMap(jsonMap);

      logs.add(log);
    }

    return logs;
  }

  static Log fromMap(Map<String, dynamic> jsonMap) {
    Log log = Log();
    return log;
  }

}
