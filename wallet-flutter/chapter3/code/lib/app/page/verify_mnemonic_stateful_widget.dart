import 'package:digging/app/custom_widget/page_header.dart';
import 'package:digging/app/custom_widget/shadow_button.dart';
import 'package:digging/app/service/wallet_manager.dart';
import 'package:digging/sdk/utils/wallet_util.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

class VerifyMnemonicPhraseStatefulWidget extends StatefulWidget {
  const VerifyMnemonicPhraseStatefulWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _VerifyMnemonicPhraseStatefulWidgetState();
  }
}
/// 存储单词选中状态
class WordInfo {
  bool checked;
  String mnemonic; // 助记词
  int waitSelectIndex; // 在待选单词列表的索引
  int showWordIndex;

  WordInfo(this.checked, this.mnemonic, this.waitSelectIndex,
      this.showWordIndex); // 选中单词的索引

}

class _VerifyMnemonicPhraseStatefulWidgetState
    extends State<VerifyMnemonicPhraseStatefulWidget> {
  final Color _hintColor = const Color(0xff959598);

  /// 正确顺序的助记词
  late List<String> _originMnemonicWordList;
  late List<String> _shuffledMnemonicWords;

  /// 待选单词的索引映射
  final Map<int, WordInfo> _waitSelectWordInfoMap = {};

  /// 选中单词索引映射
  final Map<int, WordInfo> _selectWordInfoMap = {};

  int _curEmptyIndex = 0;

  bool _isEnabled = false;

  @override
  void initState() {
    CreateWalletSessionInfo sessionInfo =
        WalletManager.getCreateWalletSession()!;

    _originMnemonicWordList = List.from(sessionInfo.mnemonicWords);

    _shuffledMnemonicWords = List.from(sessionInfo.mnemonicWords);

    _shuffledMnemonicWords.shuffle();

    for (int i = 0; i < _shuffledMnemonicWords.length; ++i) {
      _waitSelectWordInfoMap[i] =
          WordInfo(false, _shuffledMnemonicWords[i], i, -1);
    }

    super.initState();
  }

  String _getWord(int index) {
    WordInfo? wordInfo = _selectWordInfoMap[index];
    if (wordInfo == null) {
      return (index + 1).toString();
    }
    return wordInfo.mnemonic;
  }

  Color _getColor(int index) {
    WordInfo? wordInfo = _selectWordInfoMap[index];
    if (wordInfo != null) {
      return Colors.black;
    }
    return _hintColor;
  }

  VoidCallback? _getSelectWordPress(int index) {
    WordInfo? wordInfo = _selectWordInfoMap[index];

    if (wordInfo != null) {
      return () {
        _onSelectWord(index, false);
      };
    }
    return null;
  }

  String _getWaitSelectWord(int index) {
    return _shuffledMnemonicWords[index];
  }

  void _onSelectWord(int index, bool isWaitSelect) {
    late WordInfo? wordInfo;

    if (isWaitSelect) {
      wordInfo = _waitSelectWordInfoMap[index];
    } else {
      wordInfo = _selectWordInfoMap[index];
    }

    if (wordInfo == null) {
      return;
    }
    if (isWaitSelect) {
      /// 如果点击的是待选单词列表,那每次都取反
      wordInfo.checked = !wordInfo.checked;
    } else {
      if (wordInfo.showWordIndex == -1) {
        return;
      }

      /// 如果点击的是已选单词列表,则每次则认为是取消选中
      wordInfo.checked = false;
    }

    if (!wordInfo.checked) {
      _selectWordInfoMap.remove(wordInfo.showWordIndex);

      /// 如果取消的位置索引比当前的空位置的索引小,则更新
      if (wordInfo.showWordIndex < _curEmptyIndex) {
        _curEmptyIndex = wordInfo.showWordIndex;
      }
      wordInfo.showWordIndex = -1;
    } else {
      wordInfo.showWordIndex = _curEmptyIndex;
      _selectWordInfoMap[wordInfo.showWordIndex] = wordInfo;

      /// 寻找下一个为空的位置
      _findNextEmptyIndex();

      if (_selectWordInfoMap.length == _originMnemonicWordList.length) {
        // 可以创建了
        _isEnabled = true;
      }
    }

    setState(() {});
  }

  void _findNextEmptyIndex() {
    do {
      ///  如果为空则说明当前位置并没有选中单词
      if (_selectWordInfoMap[++_curEmptyIndex] == null) {
        return;
      }
    } while (_curEmptyIndex < _waitSelectWordInfoMap.length);
  }

  VoidCallback? _getUnSelectWordPress(int index) {
    WordInfo wordInfo = _waitSelectWordInfoMap[index]!;

    if (!wordInfo.checked) {
      return () {
        _onSelectWord(index, true);
      };
    }
    return null;
  }

  Color _getUnSelectWordBorderColor(int index) {
    WordInfo wordInfo = _waitSelectWordInfoMap[index]!;
    if (!wordInfo.checked) {
      return const Color(0xff6485CC);
    }
    return const Color(0xffDDE0E7);
  }

  Color _getUnSelectWordBackgroundColor(int index) {
    WordInfo wordInfo = _waitSelectWordInfoMap[index]!;
    if (!wordInfo.checked) {
      return Colors.white;
    }
    return const Color(0xffDCDFE6);
  }

  /// 清空选择的单词
  void _resetSelectWorld() {
    _waitSelectWordInfoMap.forEach((key, value) {
      value.checked = false;
    });
    _curEmptyIndex = 0;
    _selectWordInfoMap.clear();

    setState(() {
      _isEnabled = false;
    });
  }

  void _genPrivateKey() {
    bool bPass = true;
    for (int i = 0; i < _originMnemonicWordList.length; ++i) {
      String originWord = _originMnemonicWordList[i];
      WordInfo? selectWordInfo = _selectWordInfoMap[i];
      if (selectWordInfo == null) {
        bPass = false;
        Fluttertoast.showToast(msg: "助记词顺序不正确, 请重新选择");
        break;
      }
      if (originWord != selectWordInfo.mnemonic) {
        bPass = false;
        Fluttertoast.showToast(msg: "助记词顺序不正确, 请重新选择");
        break;
      }
    }

    if (!bPass) {
      return;
    }

    bool bSucceed = WalletManager.generateWallet();
    if (!bSucceed) {
      Fluttertoast.showToast(msg: "生成钱包失败!");
    } else {
      Fluttertoast.showToast(msg: "生成钱包成功!");
      // 生成成功删除session
      WalletManager.clearCreateWalletSession();
    }
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
              PageHeader(title: "验证助记词"),
              Expanded(
                flex: 1,
                child: Container(
                  padding: const EdgeInsets.only(
                      top: 16, left: 16, right: 16, bottom: 20),
                  child: SizedBox(
                    width: double.infinity,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "请按顺序点击助记词，以确认您已经正确备份。",
                          style: TextStyle(
                            fontSize: 17,
                            fontWeight: FontWeight.bold,
                            color: Colors.black,
                          ),
                        ),
                        const SizedBox(height: 16),
                        SizedBox(
                            width: double.infinity,
                            height: 245,
                            child: GridView.count(
                                padding: EdgeInsets.zero,
                                crossAxisCount: 3,
                                mainAxisSpacing: 10.0,
                                crossAxisSpacing: 10.0,
                                childAspectRatio: 2.4,
                                children: List.generate(
                                  12,
                                  (index) => Container(
                                      decoration: const BoxDecoration(
                                          color: Color(0xfff0f1f5)),
                                      child: Center(
                                          child: TextButton(
                                              onPressed:
                                                  _getSelectWordPress(index),
                                              child: Text(_getWord(index),
                                                  style: TextStyle(
                                                      fontSize: 14,
                                                      color:
                                                          _getColor(index)))))),
                                ))),
                        Wrap(
                            spacing: 8,
                            runSpacing: 0,
                            alignment: WrapAlignment.start,
                            children: List.generate(
                              12,
                              (index) => OutlinedButton(
                                  style: OutlinedButton.styleFrom(
                                      primary: const Color(0xff316DEF),
                                      backgroundColor:
                                          _getUnSelectWordBackgroundColor(
                                              index),
                                      side: BorderSide(
                                          color: _getUnSelectWordBorderColor(
                                              index))),
                                  onPressed: _getUnSelectWordPress(index),
                                  child: Text(
                                    _getWaitSelectWord(index),
                                  )),
                            )),
                        Container(
                            margin: const EdgeInsets.only(top: 20),
                            child: SizedBox(
                              width: double.infinity,
                              height: 44,
                              child: ShadowButton(
                                  isEnable: _isEnabled,
                                  shadowColor: const Color(0xffdddddd),
                                  borderRadius: BorderRadius.circular(44),
                                  onPressed: _genPrivateKey,
                                  child: Text(
                                    "确认",
                                    style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                        color: _isEnabled
                                            ? const Color(0xfff6f6f6)
                                            : const Color(0xffd8d8d8)),
                                  )),
                            )),
                        Center(
                          child: TextButton(
                              onPressed: _resetSelectWorld,
                              child: const Text(
                                "清空",
                                style: TextStyle(fontWeight: FontWeight.bold),
                              )),
                        )
                      ],
                    ),
                  ),
                ),
              )
            ]),
      ),
    );
  }
}
