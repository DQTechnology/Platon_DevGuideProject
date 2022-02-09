
import 'number_parser_util.dart';

class AmountUtil {
   // static const String _value1E18 = "1e18";

   static final BigInt _value1E18 = BigInt.from(10).pow(18);


   static String convertVonToLat(BigInt balance) {

     double lat = balance / _value1E18;

     return NumberParserUtils.getPrettyBalance(lat);
   }


}