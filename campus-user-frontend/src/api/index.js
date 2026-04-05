import axios from 'axios'

const API_BASE_URL = 'http://localhost:3030'

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const { data } = response
    if (data.flag) {
      return data.data
    } else {
      throw new Error(data.msg || '请求失败')
    }
  },
  error => Promise.reject(error)
)

export default {
  // 认证相关
  auth: {
    login: (data) => api.post('/auth/login', data)
  },

  // 用户相关
  user: {
    register: (data) => api.post('/user/registeredUser', data),
    getCurrentUser: () => api.get('/user/username'),
    getUserList: (pageNum, pageSize, params) => api.post(`/user/${pageNum}/${pageSize}`, params),
    getUserById: (id) => api.get(`/user/${id}`),
    updateUser: (data) => api.put('/user', data),
    patchUser: (data) => api.patch('/user/patch', data),
    updatePassword: (data) => api.post('/user/password', data),
    deleteUser: (id) => api.delete(`/user/${id}`)
  },

  // 公司相关
  company: {
    getCurrentCompany: () => api.get('/company/user/name'),
    getCompanyList: (pageNum, pageSize, params) => api.post(`/company/${pageNum}/${pageSize}`, params),
    getCompanyById: (id) => api.get(`/company/${id}`),
    getCompanyDetail: (id) => api.get(`/company/${id}`), // 企业详情
    createCompany: (data) => api.post('/company', data),
    updateCompany: (data) => api.put('/company', data),
    deleteCompany: (id) => api.delete(`/company/${id}`),
    auditCompany: (data) => api.put('/company/audit', data)
  },

  // 职位相关
  employment: {
    getUserJobs: (pageNum, pageSize) => api.get(`/employment/username/${pageNum}/${pageSize}`),
    getJobList: (pageNum, pageSize, params) => api.post(`/employment/${pageNum}/${pageSize}`, params),
    getJobById: (id) => api.get(`/employment/${id}`),
    createJob: (data) => api.post('/employment', data),
    updateJob: (data) => api.put('/employment', data),
    deleteJob: (id) => api.delete(`/employment/${id}`),
    auditJob: (data) => api.put('/employment/audit', data),
    collectJob: (id) => api.post('/employment/collect', { id }),
    uncollectJob: (id) => api.post('/employment/uncollect', { id })
  },

  // 求职申请相关
  employmentUser: {
    getCompanyApplications: (pageNum, pageSize, params) => api.post(`/employmentUser/company/${pageNum}/${pageSize}`, params),
    getUserApplications: (username) => api.get(`/employmentUser/user/${username}`),
    getApplicationById: (id) => api.get(`/employmentUser/${id}`),
    applyJob: (data) => api.post('/employmentUser', data),
    updateApplication: (data) => api.put('/employmentUser', data),
    deleteApplication: (id) => api.delete(`/employmentUser/${id}`)
  },

  // 文章相关
  article: {
    getUserArticles: (pageNum, pageSize, type) => api.get(`/article/user/${pageNum}/${pageSize}`, { params: { type } }),
    getArticleList: (pageNum, pageSize, params = {}) => api.post(`/article/${pageNum}/${pageSize}`, params),
    getArticleById: (id) => api.get(`/article/${id}`),
    createArticle: (data) => api.post('/article', data),
    updateArticle: (data) => api.put('/article', data),
    deleteArticle: (id) => api.delete(`/article/${id}`),
    likeArticle: (id) => api.post(`/article/like/${id}`),
    unlikeArticle: (id) => api.post(`/article/unlike/${id}`),
    collectArticle: (id) => api.post(`/article/collect/${id}`),
    uncollectArticle: (id) => api.post(`/article/uncollect/${id}`)
  },

  // 评论相关
  comments: {
    getCommentsByArticle: (articleId) => api.get(`/comments/list/article/${articleId}`),
    createComment: (data) => api.post('/comments', data),
    updateComment: (data) => api.put('/comments', data),
    deleteComment: (id) => api.delete(`/comments/${id}`)
  },

  // 邮件相关
  email: {
    getAuthCode: (receiver) => api.get('/email/getAuthCode', { params: { receiver } })
  },

  // 反馈相关
  feedback: {
    getFeedbackList: (pageNum, pageSize, params) => api.post(`/feedback/${pageNum}/${pageSize}`, params),
    createFeedback: (data) => api.post('/feedback', data),
    updateFeedback: (data) => api.put('/feedback', data)
  },

  // 统计相关
  statistics: {
    getDashboardStats: () => api.get('/statistics/dashboard'),
    getUserStats: () => api.get('/statistics/user'),
    getCompanyStats: () => api.get('/statistics/company'),
    getEmploymentStats: () => api.get('/statistics/employment')
  },

  // 简历相关
  resume: {
    uploadResume: (file) => {
      const formData = new FormData()
      formData.append('file', file)
      return api.post('/resume/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
    },
    getUserResumeList: (pageNum, pageSize) => api.get(`/resume/user/${pageNum}/${pageSize}`),
    getAllResumeList: (pageNum, pageSize) => api.get(`/resume/all/${pageNum}/${pageSize}`),
    deleteResume: (id) => api.delete(`/resume/${id}`)
  },

  // 枚举相关
  enum: {
    getEnumList: (enumType) => api.get(`/enum/${enumType}`)
  },

  // 地区相关
  area: {
    getAreaInfo: () => api.get('/areaSelection/area'),
    getAreaList: (parentCode, level) => api.get('/areaSelection/list', { params: { parentCode, level } }),
    getHotCities: () => api.get('/areaSelection/hot'),
    getAreaPage: (pageNum, pageSize, params) => api.post(`/areaSelection/${pageNum}/${pageSize}`, params),
    getAreaById: (id) => api.get(`/areaSelection/${id}`),
    createArea: (data) => api.post('/areaSelection', data),
    updateArea: (data) => api.put('/areaSelection', data),
    deleteArea: (id) => api.delete(`/areaSelection/${id}`)
  },

  // 健康检查相关
  health: {
    checkHealth: () => api.get('/health/check'),
    getSystemInfo: () => api.get('/health/info')
  },

  // 个人中心相关
  profile: {
    getUserInfo: () => api.get('/user/username'),
    updateUserInfo: (data) => api.put('/user', data),
    getApplications: () => api.get('/employmentUser/user/1/100')
  },

  // 用户简历相关
  userResume: {
    getUserResumeList: () => api.get('/userResume/user'),
    getResumeList: (pageNum, pageSize, params) => api.post(`/userResume/${pageNum}/${pageSize}`, params),
    getResumeById: (id) => api.get(`/userResume/${id}`),
    uploadResume: (file) => {
      const fd = new FormData()
      fd.append('file', file)
      return api.post('/userResume/upload', fd)
    },
    createResume: (data) => api.post('/userResume/add', data),
    updateResume: (data) => api.put('/userResume', data),
    deleteResume: (id) => api.delete(`/userResume/${id}`)
  },

  // 回复信息相关
  replyInfo: {
    getReplyList: (pageNum, pageSize, params) => api.post(`/replyInfo/${pageNum}/${pageSize}`, params),
    getReplyById: (id) => api.get(`/replyInfo/${id}`),
    createReply: (data) => api.post('/replyInfo', data),
    updateReply: (data) => api.put('/replyInfo', data),
    deleteReply: (id) => api.delete(`/replyInfo/${id}`)
  },

  // 评论回复关系相关
  commentReplyRelation: {
    getRelationList: (pageNum, pageSize, params) => api.post(`/commentReplyRelation/${pageNum}/${pageSize}`, params),
    getRelationById: (id) => api.get(`/commentReplyRelation/${id}`),
    createRelation: (data) => api.post('/commentReplyRelation', data),
    updateRelation: (data) => api.put('/commentReplyRelation', data),
    deleteRelation: (id) => api.delete(`/commentReplyRelation/${id}`)
  }
}