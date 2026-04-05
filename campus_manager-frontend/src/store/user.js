import { login } from '@/api/login'
import { getUserList, addUser, updateUser, deleteUser, queryUser, getInfo, getAuthCode, updatePassword } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    userList: [],
    userInfo: {}
  },

  mutations: {
    SET_USERLIST: (state, value) => {
      state.userList = value
    },
    SET_USERINFO(state, value) {
      state.userInfo = value
    }
  },

  actions: {
    // 获取用户列表
    getUserList({ commit }, params) {
      return getUserList(params).then(res => {
        commit('SET_USERLIST', res.list || [])
        return res
      })
    },
    // 添加用户
    addUser({ commit }, params) {
      return addUser(params).then(res => {})
    },
    // 修改用户
    updateUser({ commit }, params) {
      return updateUser(params)
    },
    // 删除用户
    deleteUser({ commit }, params) {
      return deleteUser(params)
    },
    // 按条件获取用户列表
    queryUser({ commit }, params) {
      return queryUser(params).then(res => {
        commit('SET_USERLIST', res.list)
        return res
      }
      )
    },
    // 登录
    login({ commit }, params) {
      return login(params).then(res => {
        if (res && res.token) {
          setToken(res.token)
          // 存储用户信息
          if (res.user) {
            localStorage.setItem('userInfo', JSON.stringify(res.user))
          }
          return res
        } else {
          throw new Error('登录失败，未获取到token')
        }
      })
    },

    // 登出
    logOut({ commit, state }) {
      removeToken()
      localStorage.removeItem('company')
      return Promise.resolve()
    },

    // 前端 登出
    FedLogOut() {
      return new Promise(resolve => {
        removeToken()
        resolve()
      })
    },
    getInfo({ commit }) {
      return getInfo().then(res => {
        commit('SET_USERINFO', res)
        return res
      })
    },

    /* 获取邮箱验证码 */
    getAuthCode({ commit }, params) {
      return getAuthCode(params)
    },

    updatePassword({ commit }, params) {
      return updatePassword(params).then(res => {
      })
    }
  }
}

export default user
