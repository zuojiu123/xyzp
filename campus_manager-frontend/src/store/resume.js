import { getAllResumeList, queryResumeList, getResumeStats, auditResume } from '@/api/resume'

const resume = {
  state: {
    resumeList: [],
    resumeStats: {
      total: 0,
      pending: 0,
      approved: 0,
      rejected: 0
    }
  },

  mutations: {
    SET_RESUME_LIST: (state, value) => {
      state.resumeList = value
    },
    SET_RESUME_STATS: (state, value) => {
      state.resumeStats = value
    }
  },

  actions: {
    // 获取所有简历申请列表
    getAllResumeList({ commit }, params) {
      return getAllResumeList(params).then(res => {
        commit('SET_RESUME_LIST', res.list)
        return res
      })
    },

    // 按条件查询简历申请
    queryResumeList({ commit }, params) {
      return queryResumeList(params).then(res => {
        commit('SET_RESUME_LIST', res.list)
        return res
      })
    },

    // 获取简历统计信息
    getResumeStats({ commit }) {
      return getResumeStats().then(res => {
        commit('SET_RESUME_STATS', res)
        return res
      })
    },

    // 审核简历申请
    auditResume({ commit }, params) {
      return auditResume(params)
    }
  }
}

export default resume
