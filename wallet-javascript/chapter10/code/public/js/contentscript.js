
// 向页面注入JS
function injectCustomJs(jsPath) {
    // 因为content_script是不能直接访问网页的中的变量的
    // 因此需要给网页注入要一个js文件让其可以访问,
    jsPath = jsPath || 'js/inject.js';
    let scriptDom = document.createElement('script');
    scriptDom.setAttribute('type', 'text/javascript');
    scriptDom.src = chrome.extension.getURL(jsPath);
    let origin = ""
    // inject.js文件加载完成后,执行如下代码
    scriptDom.onload = () => {
        
        /**
         * 将消息发送给background.js
         */
        function sendMsgToBackground(data) {
            return new Promise((resolve, reject) => {

                chrome.runtime.sendMessage(data, (response) => {
                    resolve(response);
                });
            });
        }

        /**
         * background.js回复消息给inject.js
         * @param {}} id 
         * @param {*} data 
         */
        function sendMsgBackToInject(id, data) {
            window.postMessage({
                to: "diggingInject",
                type: "ack",
                id,
                data
            }, origin);
        }

        /**
         * 监听来自inject的消息
         */
        window.addEventListener("message", async function (e) {
            let data = e.data;
            if (data.to !== 'diggingConent') {
                return;
            }
            origin = data.origin;
            /**
             * 发型消息给后台
             */
            if (data.type === "send") {

                let res = await sendMsgToBackground(data);
                sendMsgBackToInject(data.id, res);
            } 
        }, false);


        /**
         * 接受来自后台的消息
         */
        chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
            return true;
        });

    }
    
    document.body.appendChild(scriptDom);
}
// 网页加载后执行代码
document.addEventListener("DOMContentLoaded", () => {
    injectCustomJs();
});


