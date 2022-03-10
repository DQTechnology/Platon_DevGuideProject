
import 'package:flutter/material.dart';
import 'asset_stateful_widget.dart';

class MainStatefulWidget extends StatefulWidget {
  const MainStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _MainStatefulWidgetState();
  }
}

class _MainStatefulWidgetState extends State {
  static const double _iconSize = 26;
  int _curIndex = 0;

  @override
  void initState() {
    super.initState();
  }

  void _onTabChange(int index) {
    setState(() {
      _curIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      body: const AssetStatefulWidget(),
      bottomNavigationBar: BottomNavigationBar(
        selectedItemColor: const Color(0xff000000),
        unselectedItemColor: const Color(0xffbbbbbb),
        selectedFontSize: 12,
        currentIndex: _curIndex,
        onTap: _onTabChange,
        items: const [
          BottomNavigationBarItem(
              icon: Image(
                width: _iconSize,
                image: AssetImage('images/nav_property_unselected.png'),
                excludeFromSemantics: true, //去除图片语义
                gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
              ),
              activeIcon: Image(
                width: _iconSize,
                image: AssetImage('images/nav_property_selected.png'),
                excludeFromSemantics: true, //去除图片语义
                gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
              ),
              label: "钱包"),
          BottomNavigationBarItem(
              icon: Image(
                width: _iconSize,
                image: AssetImage('images/nav_undelegate.png'),
                excludeFromSemantics: true, //去除图片语义
                gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
              ),
              activeIcon: Image(
                width: _iconSize,
                image: AssetImage('images/nav_delegated.png'),
                excludeFromSemantics: true, //去除图片语义
                gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
              ),
              label: "委托"),
          BottomNavigationBarItem(
              icon: Image(
                width: _iconSize,
                image: AssetImage('images/nav_me_unselected.png'),
                excludeFromSemantics: true, //去除图片语义
                gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
              ),
              activeIcon: Image(
                width: _iconSize,
                image: AssetImage('images/nav_me_selected.png'),
                excludeFromSemantics: true, //去除图片语义
                gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
              ),
              label: "我的")
        ],
      ),
    );
  }
}
