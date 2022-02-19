import 'package:digging/app/page/operate_menu_stateless_widget.dart';
import 'package:digging/app/util/status_bar_util.dart';
import 'package:digging/sdk/parameters/network_parameters.dart';

import 'package:flutter/material.dart';

void main() {
  // 使用测试网

  WidgetsFlutterBinding.ensureInitialized();

  StatusBarUtil.setDark();

  NetworkParameters.init(BigInt.from(210309), "lat");

  runApp( const HomeStatelessWidget());
}

class HomeStatelessWidget extends StatelessWidget {
  const HomeStatelessWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {

    return const MaterialApp(home: OperateMenuStatelessWidget());
  }

}


// class MyApp extends StatelessWidget {
//   const MyApp({Key? key}) : super(key: key);
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       title: 'Digging',
//       theme: ThemeData(
//         primarySwatch: Colors.blue,
//       ),
//       // home: const MyHomePage(title: 'Digging')
//       home: PaperWidget(),
//     );
//   }
// }

// class ItemBox extends StatelessWidget {
//   final int index;
//
//   const ItemBox({
//     Key? key,
//     required this.index,
//   }) : super(key: key);
//
//   Color get color => Colors.blue.withOpacity((index % 10) * 0.1);
//
//   @override
//   Widget build(BuildContext context) {
//     return Container(
//       alignment: Alignment.center,
//       color: color,
//       height: 56,
//       child: Text(
//         '第 $index 个',
//         style: const TextStyle(fontSize: 20),
//       ),
//     );
//   }
// }

// class PaperWidget extends StatefulWidget {
//   @override
//   State<StatefulWidget> createState() {
//     return _PageState();
//   }
// }
//
// class _PageState extends State<PaperWidget> {
//   bool _open = false;
//
//   void _toggle() {
//     setState(() {
//       _open = !_open;
//     });
//   }
//
//   // Widget _getToggleChild() {
//   //   if (_open) {
//   //     return ;
//   //   } else {
//   //     return MaterialButton(onPressed: () {}, child: const Text("Toggle Two"));
//   //   }
//   // }
//
//   List<Widget> childWidget = List.from([
//     const Text("Toggle One"),
//     MaterialButton(onPressed: () {}, child: const Text("Toggle Two"))
//   ]);
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(title: const Text("Sample App")),
//       body: Container(
//         child: childWidget[_open ? 1 : 0],
//       ),
//       floatingActionButton: FloatingActionButton(
//         onPressed: () {
//           setState(() {
//             _open = !_open;
//           });
//         },
//         tooltip: "Update Text",
//         child: const Icon(Icons.update),
//       ),
//     );
//   }
// }
//
// class PaperPainter extends CustomPainter {
//   @override
//   void paint(Canvas canvas, Size size) {
//     final Paint paint = Paint();
//
//     paint.color = const Color(0xff009A44);
//     canvas.drawCircle(const Offset(100, 100), 50, paint..invertColors = false);
//     canvas.drawCircle(
//         const Offset(100 + 120.0, 100), 50, paint..invertColors = true);
//     // // 绘制圆
//     // canvas.drawCircle(const Offset(150, 150), 30, paint);
//     //
//     // paint.strokeWidth = 10;
//     // paint.strokeCap = StrokeCap.round;
//     // paint.isAntiAlias = true;
//     //
//     //
//     // canvas.drawLine(const Offset(100, 100), const Offset(200, 200), paint);
//     //
//     // canvas.rotate(0.6);
//     //
//     // canvas.drawLine(const Offset(100, 100), const Offset(200, 200), paint);
//   }
//
//   @override
//   bool shouldRepaint(covariant CustomPainter oldDelegate) {
//     return false;
//   }
// }
//
// class MyHomePage extends StatefulWidget {
//   const MyHomePage({Key? key, required this.title}) : super(key: key);
//
//   final String title;
//
//   @override
//   State<MyHomePage> createState() => _MyHomePageState();
// }
//
// class _MyHomePageState extends State<MyHomePage> {
//   // 创建web3对象
//   final Web3 _web3 = Web3.build("http://35.247.155.162:6789");
//
//   String _address = "lat19ydcsmfnxlqw4640u93r3chn2pk0c8shsn3lhg";
//   String _lat = "0";
//
//   int _curIndex = 0;
//
//   static const double _iconSize = 26;
//
//   void _generateWallet() {
//     // 创建钱包
//     ECKeyPair ecKeyPair = WalletUtil.generatePlatONBip39Wallet();
//
//     Credentials credentials = Credentials.createByECKeyPair(ecKeyPair);
//
//     setState(() {
//       _address = credentials.address;
//     });
//   }
//
//   void _getBalance() async {
//     // 创建钱包
//     BigInt balance = await _web3.getBalance(_address);
//
//     setState(() {
//       _lat = AmountUtil.convertVonToLat(balance);
//     });
//   }
//
//   void _onTabChange(int index) {
//     setState(() {
//       _curIndex = index;
//     });
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: Center(
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: <Widget>[
//             Text(_address), // 显示钱包地址
//             ElevatedButton(
//               child: const Text("生成钱包地址"),
//               onPressed: () {
//                 _generateWallet();
//               },
//             ),
//             Text(_lat), // 显示lat
//             ElevatedButton(
//               child: const Text("获取钱包余额"),
//               onPressed: () {
//                 _getBalance();
//               },
//             ),
//           ],
//         ),
//       ),
//       bottomNavigationBar: BottomNavigationBar(
//         selectedItemColor: const Color(0xff000000),
//         unselectedItemColor: const Color(0xffbbbbbb),
//         selectedFontSize: 12,
//         currentIndex: _curIndex,
//         onTap: _onTabChange,
//         items: const [
//           BottomNavigationBarItem(
//               icon: Image(
//                 width: _iconSize,
//                 image: AssetImage('images/nav_property_unselected.png'),
//                 excludeFromSemantics: true, //去除图片语义
//                 gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
//               ),
//               activeIcon: Image(
//                 width: _iconSize,
//                 image: AssetImage('images/nav_property_selected.png'),
//                 excludeFromSemantics: true, //去除图片语义
//                 gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
//               ),
//               label: "钱包"),
//           BottomNavigationBarItem(
//               icon: Image(
//                 width: _iconSize,
//                 image: AssetImage('images/nav_undelegate.png'),
//                 excludeFromSemantics: true, //去除图片语义
//                 gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
//               ),
//               activeIcon: Image(
//                 width: _iconSize,
//                 image: AssetImage('images/nav_delegated.png'),
//                 excludeFromSemantics: true, //去除图片语义
//                 gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
//               ),
//               label: "委托"),
//           BottomNavigationBarItem(
//               icon: Image(
//                 width: _iconSize,
//                 image: AssetImage('images/nav_me_unselected.png'),
//                 excludeFromSemantics: true, //去除图片语义
//                 gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
//               ),
//               activeIcon: Image(
//                 width: _iconSize,
//                 image: AssetImage('images/nav_me_selected.png'),
//                 excludeFromSemantics: true, //去除图片语义
//                 gaplessPlayback: true, //重新加载图片的过程中，原图片的展示是否保留
//               ),
//               label: "我的")
//         ],
//       ),
//     );
//   }
// }
