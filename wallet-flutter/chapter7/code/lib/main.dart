import 'package:digging/app/page/asset_chain_stateful_widget.dart';
import 'package:digging/app/page/main_stateful_widget.dart';
import 'package:digging/app/page/operate_menu_stateless_widget.dart';
import 'package:digging/app/page/send_lat_stateful_widget.dart';
import 'package:digging/app/page/withdraw_delegate_stateful_widget.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/app/util/status_bar_util.dart';
import 'package:digging/sdk/parameters/network_parameters.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:get/get.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

import 'app/page/delegate_detail_stateful_widget.dart';
import 'app/page/send_delegate_stateful_widget.dart';

void main() {
  // 使用测试网
  WidgetsBinding widgetsBinding = WidgetsFlutterBinding.ensureInitialized();

  NetworkParameters.init(BigInt.from(210309), "lat");
  FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);
  /// 加载钱包
  _loadWallet();
}
/// 加载钱包
void _loadWallet() async {
  await WalletManager.loadAllWallet();
  runApp(const HomeStatefulWidget());
  /// 设置状态栏为沉浸式
  SystemUiOverlayStyle systemUiOverlayStyle =
      const SystemUiOverlayStyle(statusBarColor: Colors.transparent);
  SystemChrome.setSystemUIOverlayStyle(systemUiOverlayStyle);
  /// 移除splash
  FlutterNativeSplash.remove();
}

class HomeStatefulWidget extends StatelessWidget {
  const HomeStatefulWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    late String initRouter;
    /// 判断是否有钱包
    if (!WalletManager.isExistWallet()) {
      initRouter = "/import";
    } else {
      initRouter = "/main";
    }
    return GetMaterialApp(
      localizationsDelegates: const [
        RefreshLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalMaterialLocalizations.delegate
      ],
      supportedLocales: const [
        Locale('en'),
        Locale('zh'),
      ],
      initialRoute: initRouter,
      getPages: [
        GetPage(
            name: "/import",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const Material(child:  OperateMenuStatelessWidget(),),
                  value: StatusBarUtil.getDarkOverlayStyle());
            }),
        GetPage(
            name: "/main",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const Material(child: MainStatefulWidget()),
                  value: StatusBarUtil.getLightOverlayStyle());
            }),
        GetPage(
            name: "/assetchain",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const AssetChainStatefulWidget(),
                  value: StatusBarUtil.getLightOverlayStyle());
            }),
        GetPage(
            name: "/sendlat",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const SendLatStatefulWidget(),
                  value: StatusBarUtil.getDarkOverlayStyle());
            }),
        GetPage(
            name: "/senddelegate",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const SendDelegateStatefulWidget(),
                  value: StatusBarUtil.getDarkOverlayStyle());
            }),
        GetPage(
            name: "/withdrawdelegate",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const WithDrawDelegateStatefulWidget(),
                  value: StatusBarUtil.getDarkOverlayStyle());
            }),
        GetPage(
            name: "/delegatedetail",
            page: () {
              return AnnotatedRegion<SystemUiOverlayStyle>(
                  child: const DelegateDetailStatefulWidget(),
                  value: StatusBarUtil.getDarkOverlayStyle());
            }),
      ],
    );
  }
}
