import axios from "axios";

let validatorService = null;

export default {
    SwitchNetwork(browserUrl) {
        validatorService = axios.create({
            baseURL: `${browserUrl}/browser-server`,
            timeout: 30000,
            withCredentials: true
        });

        validatorService.interceptors.response.use(
            response => {
                return response.data;
            },
            error => {
    
                return Promise.reject(error);
            }
        );
    },

    /**
     * 获取验证节点列表
     * @param {*} params
     * @returns
     */
    GetAliveStakingList(params) {
        return validatorService.post("/staking/aliveStakingList", params);
    }
};
