import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    token: null
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    }
  },
  actions: {
    initAuth({ commit }) {
      try {
        const token = localStorage.getItem('token')
        const userInfo = localStorage.getItem('userInfo')
        
        console.log('初始化认证状态:', { token, userInfo })
        
        if (token) {
          commit('SET_TOKEN', token)
        }
        if (userInfo) {
          try {
            const parsedUser = JSON.parse(userInfo)
            commit('SET_USER', parsedUser)
          } catch (e) {
            console.error('解析用户信息错误:', e)
            localStorage.removeItem('userInfo')
          }
        }
      } catch (error) {
        console.error('初始化认证错误:', error)
      }
    },
    login({ commit }, { user, token }) {
      commit('SET_USER', user)
      commit('SET_TOKEN', token)
    },
    logout({ commit }) {
      commit('SET_USER', null)
      commit('SET_TOKEN', null)
      localStorage.removeItem('userInfo')
    }
  }
})