<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.total }}
            </div>
            <div class="stats-label">
              总申请数
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card pending">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.pending }}
            </div>
            <div class="stats-label">
              待处理
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card approved">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.approved }}
            </div>
            <div class="stats-label">
              已通过
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card rejected">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.rejected }}
            </div>
            <div class="stats-label">
              已拒绝
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选条件 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" placeholder="申请人姓名" style="width: 150px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-input v-model="listQuery.phone" placeholder="手机号" style="width: 150px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-select v-model="listQuery.replyStatus" placeholder="申请状态" clearable style="width: 120px;" class="filter-item">
        <el-option label="待回复" value="Wait_For_Reply" />
        <el-option label="已通过" value="Agree_With_Induction" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="resetQuery">
        重置
      </el-button>
    </div>

    <!-- 简历申请列表 -->
    <el-table v-loading="listLoading" :data="resumeList" border fit highlight-current-row>
      <el-table-column align="center" label="申请人" prop="name" width="100" />
      <el-table-column align="center" label="手机号" prop="phone" width="120" />
      <el-table-column align="center" label="应聘职位" prop="employmentModel.title" width="150" show-overflow-tooltip />
      <el-table-column align="center" label="申请时间" width="150">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="申请状态" width="100">
        <template slot-scope="{row}">
          <el-tag v-if="row.replyStatus === 'Wait_For_Reply'" type="warning">
            待处理
          </el-tag>
          <el-tag v-if="row.replyStatus === 'Agree_With_Induction'" type="success">
            已通过
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="150">
        <template slot-scope="{row}">
          <el-button type="info" size="mini" @click="viewDetail(row)">
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getResumeList" />
  </div>
</template>

<script>
import { mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import { parseTime } from '@/utils/time'

export default {
  components: { Pagination },
  filters: {
    parseTime (val) {
      return parseTime(val, '{y}-{m}-{d} {h}:{i}') || '--'
    }
  },
  data () {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        phone: '',
        replyStatus: null
      },
      listLoading: true,
      total: 0,
      resumeStats: {
        total: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      }
    }
  },
  computed: {
    ...mapState({
      resumeList: state => state.employmentUser.employmentUserList
    })
  },
  mounted () {
    this.getResumeList()
  },
  methods: {
    // 获取简历申请列表
    getResumeList () {
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        params: {
          showAll: true, // 管理端显示所有数据
          name: this.listQuery.name,
          phone: this.listQuery.phone,
          replyStatus: this.listQuery.replyStatus
        }
      }

      this.$store.dispatch('getEmploymentUserList', params).then(res => {
        this.total = res.total
        this.listLoading = false
      }).catch(err => {
        console.error('获取简历列表失败：', err)
        this.listLoading = false
      })
    },

    // 查询
    handleQuery () {
      this.listQuery.pageNum = 1
      this.getResumeList()
    },

    // 重置
    resetQuery () {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        name: '',
        phone: '',
        replyStatus: null
      }
      this.getResumeList()
    },

    // 查看详情
    viewDetail (row) {
      console.log('查看详情：', row)
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;

  &.pending {
    border-left: 4px solid #e6a23c;
  }

  &.approved {
    border-left: 4px solid #67c23a;
  }

  &.rejected {
    border-left: 4px solid #f56c6c;
  }
}

.stats-content {
  .stats-number {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
  }

  .stats-label {
    font-size: 14px;
    color: #909399;
    margin-top: 5px;
  }
}
</style>
