import 'dart:ui';

String getTransactionImage(bool isSender, int txType) {
  if (txType == 0) {
    // 转账
    if (isSender) {
      return "images/icon_send_transation.png";
    } else {
      return "images/icon_receive_transaction.png";
    }
  } else if (txType == 1) {
    // EVM合约发布(合约创建)
    return "images/icon_delegate.png";
  } else if (txType == 2) {
    //合约调用(合约执行)
    return "images/icon_delegate.png";
  } else if (txType == 6) {
    //ERC20合约发布(合约创建)
    return "images/icon_delegate.png";
  } else if (txType == 7) {
    //ERC20合约调用(合约执行)
    return "images/icon_delegate.png";
  } else if (txType == 8) {
    //ERC721合约发布(合约创建)
    return "images/icon_delegate.png";
  } else if (txType == 9) {
    //"ERC721合约调用(合约执行)
    return "images/icon_delegate.png";
  } else if (txType == 10) {
    //"ERC1155合约发布(合约创建)
    return "images/icon_delegate.png";
  } else if (txType == 11) {
    //"ERC1155合约调用(合约执行)
    return "images/icon_delegate.png";
  } else if (txType == 1004) {
    //发起委托(委托)
    return "images/icon_delegate.png";
  } else if (txType == 1005) {
    //减持/撤销委托(赎回委托)
    return "images/icon_undelegate.png";
  }
  return "images/icon_delegate.png";
}

String getTransactionType(bool isSender, int txType) {
  if (txType == 0) {
    // 转账
    if (isSender) {
      return "转账";
    } else {
      return "接收";
    }
  } else if (txType == 1) {
    // EVM合约发布(合约创建)
    return "合约创建";
  } else if (txType == 2) {
    //合约调用(合约执行)
    return "合约执行";
  } else if (txType == 6) {
    //ERC20合约发布(合约创建)
    return "合约创建";
  } else if (txType == 7) {
    //ERC20合约调用(合约执行)
    return "合约执行";
  } else if (txType == 8) {
    //ERC721合约发布(合约创建)
    return "合约创建";
  } else if (txType == 9) {
    //"ERC721合约调用(合约执行)
    return "合约执行";
  } else if (txType == 10) {
    //"ERC1155合约发布(合约创建)
    return "合约创建";
  } else if (txType == 11) {
    //"ERC1155合约调用(合约执行)
    return "合约执行";
  } else if (txType == 1004) {
    //发起委托(委托)
    return "委托";
  } else if (txType == 1005) {
    //减持/撤销委托(赎回委托)
    return "赎回委托";
  }else if (txType == 5000) {
    //减持/撤销委托(赎回委托)
    return "领取奖励";
  }


  return "未知状态";
}

Color getTransactionTypeColor(bool isSender, int receiptStatus, int txType) {
  if (receiptStatus == 0 ||
      txType == 1 ||
      txType == 2 ||
      txType == 6 ||
      txType == 7 ||
      txType == 8||
      txType == 9||
      txType == 10||
      txType == 11) {
    return const Color(0xffb6bbd0);
  } else if (txType == 0) {
    if (isSender) {
      return const Color(0xffff3b3b);
    } else {
      return const Color(0xff19a20e);
    }
  } else if (txType == 1005) {
    return const Color(0xff19a20e);
  } else {
    return const Color(0xffff3b3b);
  }
}
