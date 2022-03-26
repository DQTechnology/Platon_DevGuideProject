class AddressFormatUtil {

  static String formatAddress(String address) {
    Pattern regex = RegExp("(\\w{10})(\\w*)(\\w{10})");

    return address.replaceAllMapped(regex, (m)=>"${m[1]}....${m[3]}");
  }

}