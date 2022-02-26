class CipherParams {
  String iv;

  CipherParams(this.iv);
}

class KdfParams {}

class Aes128CtrKdfParams implements KdfParams {
  int dklen;
  int c;
  String prf;
  String salt;

  Aes128CtrKdfParams(this.dklen, this.c, this.prf, this.salt);
}

class ScryptKdfParams implements KdfParams {
  int dklen;
  int n;
  int p;
  int r;
  String salt;
  ScryptKdfParams(this.dklen, this.n, this.p, this.r, this.salt);
}

class Crypto {
  String cipher;
  String ciphertext;
  CipherParams cipherparams;
  String kdf;
  KdfParams kdfparams;
  String mac;
  Crypto(this.cipher, this.ciphertext, this.cipherparams, this.kdf,
      this.kdfparams, this.mac);
}

class WalletFile {
  String address;
  Crypto crypto;
  String id;
  int version;

  WalletFile(this.address, this.crypto, this.id, this.version);
}
