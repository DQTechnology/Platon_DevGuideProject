import 'package:digging/app/page/validator_stateful_widget.dart';
import 'package:flutter/material.dart';

import '../custom_widget/line_tab_indicator.dart';
import '../util/keep_alive_wrapper.dart';
import 'my_delegate_stateful_widget.dart';

class DelegateStatefulWidget extends StatefulWidget {
  const DelegateStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _DelegateStatefulWidgetState();
  }
}

class _DelegateStatefulWidgetState<DelegateStatefulWidget> extends State
    with SingleTickerProviderStateMixin {
  final List<String> _tabs = ["我的委托", "验证节点"];

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

  ///
  Widget _getPage(int index) {
    if (index == 0) {
      /// 获取我的委托页面
      return const MyDelegateStatefulWidget();
    }
    /// 返回验证节点页面
    return const ValidatorStatefulWidget();
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
        child: Scaffold(
      appBar: TabBar(
          controller: _tabController,
          unselectedLabelColor: const Color(0xff898C9C),
          indicatorSize: TabBarIndicatorSize.label,
          isScrollable: true,
          labelStyle:
              const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          unselectedLabelStyle: const TextStyle(
            fontSize: 16,
          ),
          indicator: const LineTabIndicator(
              insets: EdgeInsets.only(left: 2, right: 45),
              borderSide: BorderSide(width: 3.5, color: Color(0xff105CFF))),
          labelColor: const Color(0xff000000),
          tabs: List.generate(_tabs.length, (index) {
            String tabName = _tabs[index];
            return Tab(
              height: 36,
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(tabName),
              ),
            );
          })),
      body: PageView.builder(
          itemCount: _tabs.length,
          controller: _pageController,
          itemBuilder: (_, index) {
            return KeepAliveWrapper(
              child: _getPage(index),
            );
          }),
    ));
  }
}
