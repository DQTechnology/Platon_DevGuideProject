import 'package:digging/app/custom_widget/line_tab_indicator.dart';
import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/page/import_mnemonic_stateful_widget.dart';
import 'package:digging/app/page/import_privatekey_stateful_widget.dart';
import 'package:flutter/material.dart';

import 'import_keystore_stateful_widget.dart';

class ImportStatefulWidget extends StatefulWidget {
  const ImportStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _ImportStatefulWidgetState();
  }
}

/// 缓存页面
class KeepAliveWrapper extends StatefulWidget {
  const KeepAliveWrapper({
    Key? key,
    this.keepAlive = true,
    required this.child,
  }) : super(key: key);
  final bool keepAlive;
  final Widget child;

  @override
  _KeepAliveWrapperState createState() => _KeepAliveWrapperState();
}

class _KeepAliveWrapperState extends State<KeepAliveWrapper>
    with AutomaticKeepAliveClientMixin {
  @override
  Widget build(BuildContext context) {
    super.build(context);
    return widget.child;
  }

  @override
  void didUpdateWidget(covariant KeepAliveWrapper oldWidget) {
    if (oldWidget.keepAlive != widget.keepAlive) {
      // keepAlive 状态需要更新，实现在 AutomaticKeepAliveClientMixin 中
      updateKeepAlive();
    }
    super.didUpdateWidget(oldWidget);
  }

  @override
  bool get wantKeepAlive => widget.keepAlive;
}

class _ImportStatefulWidgetState extends State<ImportStatefulWidget>
    with SingleTickerProviderStateMixin {
  final List<String> _tabs = ["助记词", "钱包文件", "私钥"];

  late TabController _tabController;

  final PageController _pageController = PageController(initialPage: 0);


  @override
  void initState() {

    super.initState();
    _tabController = TabController(length: _tabs.length, vsync: this);
    _tabController.addListener(() {
      // 点击tabBar,修改PageView当前显示的页面
      if (_tabController.indexIsChanging) {
        _onTabPageChange(_tabController.index, p: _pageController);
      }
    });
    // 监听页面变动是事件
    _pageController.addListener(() {
      // 日后tabBar正在滑动中忽略
      if (!_tabController.indexIsChanging) {
        if ((_pageController.page! - _tabController.index).abs() > 1.0) {
          _tabController.index = _pageController.page!.round();
        }
        _tabController.offset =
            (_pageController.page! - _tabController.index).clamp(-1.0, 1.0);
      }
    });
  }

  _onTabPageChange(int index, {PageController? p, TabController? t}) async {
    if (p != null) {
      // 页面正在切换中,不响应动画
      await _pageController.animateToPage(index,
          duration: const Duration(milliseconds: 300), curve: Curves.ease);
    }
  }

  @override
  void dispose() {
    super.dispose();
    _tabController.dispose();
    _pageController.dispose();
  }

  Widget _getPage(int index) {
    if (index == 0) {
      return const ImportMnemonicStatefulWidget();
    } else if (index == 1) {
      return const ImportKeystoreStatefulWidget();
    }
    return const ImportPrivateKeyStatefulWidget();

  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(color: Colors.white),
        child: Flex(
          crossAxisAlignment: CrossAxisAlignment.start,
          direction: Axis.vertical,
          children: [
            PageHeader(title: "导入钱包"),
            Container(
              height: 44,
              decoration: const BoxDecoration(
                  border: Border(
                      bottom: BorderSide(width: 1, color: Color(0xffedeef2)))),
              child: TabBar(
                  controller: _tabController,
                  unselectedLabelColor: Colors.black,
                  labelColor: const Color(0xff105CFF),
                  indicatorSize: TabBarIndicatorSize.label,
                  indicator: const LineTabIndicator(),
                  tabs: List.generate(_tabs.length, (index) {
                    String tabName = _tabs[index];
                    return Tab(
                      child: Align(
                        alignment: Alignment.center,
                        child:
                            Text(tabName, style: const TextStyle(fontSize: 15)),
                      ),
                    );
                  })),
            ),
            Expanded(
              flex: 1,
              child: PageView.builder(
                  itemCount: _tabs.length,
                  controller: _pageController,
                  itemBuilder: (_, index) {
                    return KeepAliveWrapper(
                      child: _getPage(index),
                    );
                  }),
            )
          ],
        ),
      ),
    );
  }
}
