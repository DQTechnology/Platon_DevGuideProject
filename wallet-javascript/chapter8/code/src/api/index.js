import txRecord from "./tx-record.js";
import validator from "./validator.js";

export default {
    txRecord,
    validator,
    SwitchNetwork() {
        
        let digging = chrome.extension.getBackgroundPage().window.digging;

        let browserUrl = digging.NetworkManager.GetBrowserUrl();

        txRecord.SwitchNetwork(browserUrl);
        validator.SwitchNetwork(browserUrl);
    }
};
