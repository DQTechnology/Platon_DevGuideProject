import 'package:digging/app/service/network_manager.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/address_format_util.dart';
import 'package:digging/app/util/amount_util.dart';
import 'package:flutter/material.dart';
import '../../sdk/protocol/web3.dart';

class AddressInfo {
  String name = "";
  String address = "";
  BigInt balance = BigInt.zero;
  bool bSelect = false;
}

class SelectAddressStatefulWidget extends StatefulWidget {
  VoidCallback? onSelectCall;

  SelectAddressStatefulWidget({Key? key, this.onSelectCall}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _SelectAddressStatefulWidgetState();
  }
}

class _SelectAddressStatefulWidgetState
    extends State<SelectAddressStatefulWidget> {
  List<AddressInfo> addressInfoList = List.empty(growable: true);

  int _curSelectIndex = 0;

  @override
  void initState() {
    super.initState();

    _loadWallet();
  }

  _loadWallet() async {
    List<String> addressList = WalletManager.getAllAddress();

    String curWalletName = await WalletManager.getCurrentWalletName();

    for (int i = 0; i < addressList.length; ++i) {
      String address = addressList[i];

      AddressInfo addressInfo = AddressInfo();

      addressInfo.address = address;

      addressInfo.name = WalletManager.getWalletNameByAddress(address);

      addressInfo.bSelect = curWalletName == addressInfo.name;

      if (addressInfo.bSelect) {
        _curSelectIndex = i;
      }
      addressInfoList.add(addressInfo);
    }

    _loadBalance();

    setState(() {});
  }

  _loadBalance() async {
    Web3 web3 = NetworkManager.getWeb3();

    List<Future<BigInt>> balanceFutureList = List.empty(growable: true);

    for (AddressInfo addressInfo in addressInfoList) {
      balanceFutureList.add(web3.getBalance(addressInfo.address));
    }

    List<BigInt> balanceList = await Future.wait(balanceFutureList);

    for (int i = 0; i < balanceList.length; ++i) {
      AddressInfo addressInfo = addressInfoList[i];
      addressInfo.balance = balanceList[i];
    }

    setState(() {});
  }

  _onSwitchAddress(int index) async {
    if (index == _curSelectIndex) {
      return;
    }
    addressInfoList[_curSelectIndex].bSelect = false;
    addressInfoList[index].bSelect = true;
    _curSelectIndex = index;

    AddressInfo addressInfo = addressInfoList[index];

    await WalletManager.switchWallet(addressInfo.name);

    setState(()  {

      if (widget.onSelectCall != null) {
        widget.onSelectCall!();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
        child: Container(
      color: const Color(0xfff9fbff),
      padding: const EdgeInsets.only(left: 10, right: 10),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const Text(
          "选择钱包",
          style: TextStyle(
              color: Colors.black, fontSize: 20, fontWeight: FontWeight.bold),
        ),
        const SizedBox(
          height: 10,
        ),
        Expanded(
            flex: 1,
            child: ListView.builder(
              itemBuilder: (ctx, index) {
                return GestureDetector(
                  onTap: () {
                    _onSwitchAddress(index);
                  },
                  child: _AddressItem(addressInfoList[index]),
                );
              },
              itemCount: addressInfoList.length,
            ))
      ]),
    ));
  }
}

class _AddressItem extends StatelessWidget {
  final AddressInfo _addressInfo;

  const _AddressItem(this._addressInfo);

  @override
  Widget build(BuildContext context) {
    // A3C1FF
    return Container(
      padding: const EdgeInsets.all(10),
      alignment: Alignment.centerLeft,
      margin: const EdgeInsets.only(bottom: 10),
      decoration: BoxDecoration(
          color: _addressInfo.bSelect ? const Color(0xffecf3ff) : Colors.white,
          borderRadius: BorderRadius.circular(10),
          border: Border.all(
              color:
                  _addressInfo.bSelect ? const Color(0xffa3c1ff) : Colors.white,
              width: 1)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            _addressInfo.name,
            style: const TextStyle(
                fontWeight: FontWeight.bold, fontSize: 18, color: Colors.black),
          ),
          const SizedBox(
            height: 5,
          ),
          Text(
            AddressFormatUtil.formatAddress(_addressInfo.address),
            style: const TextStyle(fontSize: 14, color: Colors.black),
          ),
          const SizedBox(
            height: 5,
          ),
          Text(
            "${AmountUtil.convertVonToLat(_addressInfo.balance)} LAT",
            style: const TextStyle(fontSize: 12, color: Color(0xff61646e)),
          )
        ],
      ),
    );
  }
}
