import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './user'
import company from './company'
import info from './info'
import employment from './employment'
import employmentUser from './employmentUser'
import resume from './resume'
import feedback from './feedback'
import enumList from './enum'
import getters from './getters'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    user,
    enumList,
    company,
    employment,
    employmentUser,
    resume,
    feedback,
    info
  },
  getters
})

export default store
