class Unit {
  static int VON = 0;
  static int KVON = 3;
  static int MVON = 6;
  static int GVON = 9;
  static int TVON = 12;
  static int PVON = 15;
  static int KPVON = 18;
  static int MPVON = 21;
  static int GPVON = 24;
  static int TPVON = 27;
}

class Convert {
  static BigInt toVon(String number, int unit) {
    return BigInt.parse(number) * BigInt.from(10).pow(unit);
  }
}
