class TimeUtil {
  static String timestampToString(BigInt timestamp) {
    String targetString = "";
    final date =  DateTime.fromMicrosecondsSinceEpoch(timestamp.toInt() * 1000);
    String year = date.year.toString();
    String month = date.month.toString();
    if (date.month <= 9) {
      month = "0" + month;
    }
    String day = date.day.toString();
    if (date.day <= 9) {
      day = "0" + day;
    }
    String hour = date.hour.toString();
    if (date.hour <= 9) {
      hour = "0" + hour;
    }
    String minute = date.minute.toString();
    if (date.minute <= 9) {
      minute = "0" + minute;
    }
    String second = date.second.toString();
    if (date.second <= 9) {
      second = "0" + second;
    }

    targetString = year + "-" + month + "-" + day;

    targetString += " ";

    targetString += hour + ":" + minute + ":" + second;

    return targetString;
  }
}
