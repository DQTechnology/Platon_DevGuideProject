class NumberParserUtils {
  static String getPrettyBalance(double balance) {
    String bigDecimalStr = balance.toStringAsFixed(8);

    if (bigDecimalStr.isEmpty) {
      return "0.00";
    }

    if (!bigDecimalStr.contains(".")) {
      return bigDecimalStr + ".00";
    }

    return bigDecimalStr;
  }
}
