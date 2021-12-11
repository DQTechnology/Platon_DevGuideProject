import axios from "axios";

const txRecorcdService = axios.create({
    baseURL: "https://devnetscan.platon.network/browser-server",
    timeout: 30000,
    withCredentials: true
});

txRecorcdService.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        Message.error(error.response.data.errMsg);
        return Promise.reject(error)
    }
);

export default {
    /**
     * 获取交易记录列表
     * @param {*} params
     * @returns
     */
    GetTransactionListByAddress(params) {
        return txRecorcdService.post("/transaction/transactionListByAddress", params);
    }
};
