import axios from "axios";

let txRecorcdService = null;

export default {
    SwitchNetwork(browserUrl) {
        txRecorcdService = axios.create({
            baseURL: `${browserUrl}/browser-server`,
            timeout: 30000,
            withCredentials: true
        });

        txRecorcdService.interceptors.response.use(
            response => {
                return response.data;
            },
            error => {
                return Promise.reject(error);
            }
        );
    },
    /**
     * 获取交易记录列表
     * @param {*} params
     * @returns
     */
    GetTransactionListByAddress(params) {
        return txRecorcdService.post("/transaction/transactionListByAddress", params);
    }
};
