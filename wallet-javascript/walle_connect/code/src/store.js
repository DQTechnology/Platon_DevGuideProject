import Vue from "vue";
import Vuex from "vuex";
Vue.use(Vuex);
export default new Vuex.Store({
    state: {
        mnemonic: "", // 保存当前助记词
        isOpenInTab: false,
        connector: null,
 
    },
    getters: {
        GetMnemonic(state) {
            return state.mnemonic;
        }
    },

    mutations: {
        SetMnemonic(state, mnemonic) {
            state.mnemonic = mnemonic;
        },

        SetConnector(state, connector) {
            state.connector = connector;
        },


        SetOpenInTab(state, isOpenInTab) {
            state.isOpenInTab = isOpenInTab;
        }
    }
});
