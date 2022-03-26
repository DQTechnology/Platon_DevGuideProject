import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/service/network_manager.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class NetworkSettingStatefulWidget extends StatefulWidget {
  const NetworkSettingStatefulWidget({Key? key}) : super(key: key);
  @override
  State<StatefulWidget> createState() {
    return _NetworkSettingStatefulWidgetState();
  }
}
class _NetworkSettingStatefulWidgetState
    extends State<NetworkSettingStatefulWidget> {

  _onSwitchNetwork(int index) async {
    await NetworkManager.switchNetwork(index);
    setState(() {

    });
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          PageHeader(title: "节点设置"),
          Expanded(
              flex: 1,
              child: MediaQuery.removePadding(
                  context: context,
                  removeTop: true,
                  child: ListView.builder(
                    itemBuilder: (ctx, index) {
                      return GestureDetector(
                          onTap: () {
                            _onSwitchNetwork(index);
                          },
                          child: _NetworkItem(
                              NetworkManager.getNetworkInfoByIndex(index)));
                    },
                    itemCount: NetworkManager.getNetworkNum(),
                    itemExtent: 80,
                  )))
        ],
      ),
    );
  }
}

class _NetworkItem extends StatelessWidget {
  final NetworkInfo _networkInfo;

  _NetworkItem(this._networkInfo);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(left: 16, right: 16),
      padding: const EdgeInsets.only(top: 16, bottom: 16),
      decoration: const BoxDecoration(
          border:
              Border(bottom: BorderSide(width: 0.5, color: Color(0xffe4e7f3)))),
      child: Row(
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                "${_networkInfo.netName}(${_networkInfo.hrp})",
                style: const TextStyle(
                    color: Colors.black,
                    fontWeight: FontWeight.bold,
                    fontSize: 14),
              ),
              const SizedBox(
                height: 10,
              ),
              Text(
                _networkInfo.nodeUrl,
                style: const TextStyle(color: Color(0xff898c9e), fontSize: 13),
              ),
            ],
          ),
          const Spacer(
            flex: 1,
          ),
          Visibility(
            visible: _networkInfo.isSelected,
              child: const Image(
                  width: 20,
                  height: 20,
                  image: AssetImage("images/icon_hook_m.png")))
        ],
      ),
    );
  }
}
