class Digging {
    /**
     * 回调函数的map
     */
    static callbackMap = {};
    /**
     * 给每一个请求分配一个唯一的id
     */
    static msgId = 1;

    /**
     * 获取指定钱包的地址
     * @param {} walletAddress
     * @returns
     */
    static async GetBalanceOf(walletAddress) {
       
        /**
         * 这里把参数都打包成一个obj
         */
        return Digging.SendMsg("GetBalanceOf", walletAddress);
    }

    /**
     * 发送LAT到指定钱包
     * @param {} toAddress
     * @param {*} lat
     * @returns
     */
    static async SendLat(lat, account, toAddress) {
        /**
         * 这里把参数都打包成一个obj
         */
        return Digging.SendMsg("SendLat", {
            lat, account, toAddress
        });
    }

    /**
     * 发型消息给content script
     * @param {} msgType
     * @param {*} data
     * @returns
     */
    static SendMsg(msgType, data) {
      
        return new Promise(resolve => {
            let id = ++Digging.msgId;
            Digging.callbackMap[id] = resolve;
            // 发送给contentscript
            window.postMessage(
                {
                    to: "diggingConent",
                    type: "send",
                    id,
                    msgType,
                    origin: '*',
                    data
                },
               '*'
            );
        });
    }
}

window.Digging = Digging;
/**
 * 监听content script返回来的消息
 */
window.addEventListener(
    "message",
    function(e) {
        let data = e.data;
        if (data.to !== "diggingInject") {
            return;
        }

        if (data.type === "ack") {
            let resolve = Digging.callbackMap[data.id];
            if (!resolve) {
                return;
            }
            delete Digging.callbackMap[data.id];
            resolve(data.data);
            return;
        }
    },
    false
);
