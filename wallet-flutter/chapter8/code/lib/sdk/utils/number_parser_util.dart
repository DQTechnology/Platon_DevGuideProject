class NumberParserUtils {
  static String getPrettyBalance(double balance) {
    String fixStr = balance.toStringAsFixed(7);

    if(fixStr.isEmpty) {
      return "0.00";
    }

    fixStr = double.parse(fixStr).toString();

    int dotIndex = fixStr.indexOf(".");

    if(dotIndex == -1) {
        return fixStr + ".00";
    }

    if((fixStr.length - dotIndex) <= 2) {
      fixStr += "0";
    }
    return fixStr;
  }
}
