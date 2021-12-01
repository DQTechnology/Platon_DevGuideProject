import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);
export default new Vuex.Store({
    state: {
        mnemonic: "",

    },
    getters: {
        GetMnemonic(state) {
            return state.mnemonic;
        },

    },

    mutations: {
        SetMnemonic(state, mnemonic) {
            state.mnemonic = mnemonic;
        },

    }
});