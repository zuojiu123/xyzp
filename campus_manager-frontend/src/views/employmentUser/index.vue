<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.title" placeholder="请输入姓名" style="width: 200px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
      </el-button>
    </div>
    <el-table v-loading="listLoading" style="margin-top:20px" :data="employmentUserList" element-loading-text="Loading" border fit highlight-current-row>
      <el-table-column align="center" label="姓名" prop="name" width="100" />
      <el-table-column align="center" label="手机" prop="phone" width="120" />
      <el-table-column align="center" label="职位" prop="employmentModel.title" min-width="150" show-overflow-tooltip />
      <el-table-column align="center" label="简历操作" width="240">
        <template slot-scope="{row}">
          <div class="resume-buttons">
            <el-button type="info" size="mini" @click="viewResume(row.userResume)">
              在线查看
            </el-button>
            <el-button type="primary" size="mini" @click="preview(row.userResume)">
              预览
            </el-button>
            <el-button type="success" size="mini" @click="down(row.userResume)">
              下载
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="申请时间" width="150">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="回复时间" width="150">
        <template slot-scope="{row}">
          <span>{{ row.replyTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="回复人" prop="replyUser" width="100" />
      <el-table-column align="center" label="状态" width="120">
        <template slot-scope="{row}">
          <el-tag v-if="row.replyStatus === 'Wait_For_Reply'" type="warning">
            待回复
          </el-tag>
          <el-tag v-else-if="row.replyStatus === 'Agree_With_Induction'" type="success">
            已通过
          </el-tag>
          <el-tag v-else type="info">
            {{ row.replyStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="120">
        <template slot-scope="row">
          <el-button type="info" size="mini" @click="viewDetail(row.row)">
            查看
          </el-button>
          <el-popconfirm title="确定要删除此条申请记录吗？" @confirm="handleDelete(row)">
            <el-button slot="reference" size="mini" type="danger">
              删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getEmploymentUserList" />

    <!-- 查看详情弹框 -->
    <el-dialog :visible.sync="detailDialogVisible" title="申请详情" width="600px">
      <div v-if="currentDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">
            {{ currentDetail.name }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ currentDetail.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="申请职位" :span="2">
            {{ currentDetail.employmentModel ? currentDetail.employmentModel.title : '未知职位' }}
          </el-descriptions-item>
          <el-descriptions-item label="申请时间" :span="2">
            {{ currentDetail.createTime | parseTime }}
          </el-descriptions-item>
          <el-descriptions-item label="回复时间" :span="2">
            {{ currentDetail.replyTime | parseTime }}
          </el-descriptions-item>
          <el-descriptions-item label="回复人">
            {{ currentDetail.replyUser || '未回复' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="currentDetail.replyStatus === 'Wait_For_Reply'" type="warning">
              待回复
            </el-tag>
            <el-tag v-else-if="currentDetail.replyStatus === 'Agree_With_Induction'" type="success">
              已通过
            </el-tag>
            <el-tag v-else type="info">
              {{ currentDetail.replyStatus }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="回复内容" :span="2">
            <div class="reply-content">
              {{ currentDetail.replyContent || '无' }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">
          关闭
        </el-button>
      </div>
    </el-dialog>

    <!-- PDF查看弹窗 -->
    <el-dialog :visible.sync="pdfDialogVisible" title="简历查看" width="80%" top="5vh">
      <div class="pdf-container">
        <iframe v-if="currentPdfUrl" :src="currentPdfUrl" width="100%" height="600px" frameborder="0" />
        <div v-else-if="currentResume && !currentPdfUrl" class="download-prompt">
          <i class="el-icon-download" />
          <h3>不支持在线预览</h3>
          <p>该简历格式 ({{ currentResume.extension }}) 不支持在线预览</p>
          <p>请下载后使用相应软件打开</p>
          <el-button type="primary" size="large" @click="downloadCurrentResume">
            <i class="el-icon-download" />
            下载简历
          </el-button>
        </div>
        <div v-else class="no-pdf">
          <i class="el-icon-document" />
          <p>暂无可预览的简历</p>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="pdfDialogVisible = false">
          关闭
        </el-button>
        <el-button v-if="currentResume" type="primary" @click="downloadCurrentResume">
          下载简历
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import { parseTime } from '@/utils/time'
import { saveAs } from 'file-saver'
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
        title: ''
      },
      listLoading: true,
      ExamName: '',
      detailDialogVisible: false,
      currentDetail: null,
      visible: false,
      total: 0,
      pdfDialogVisible: false,
      currentPdfUrl: '',
      currentResume: null
    }
  },
  computed: {
    ...mapState({
      employmentUserList: state => state.employmentUser.employmentUserList,
      employmentReplyStatus: state => state.enumList.employmentReplyStatus,
      employmentUserStatus: state => state.enumList.employmentUserStatus
    })
  },
  mounted () {
    this.getEmploymentUserList()
    this.$store.dispatch('getEmploymentReplyStatus', 'EmploymentReplyStatus')
    this.$store.dispatch('getEmploymentUserStatus', 'EmploymentUserStatus')
    // this.getRoleList()
  },
  methods: {
    // 获取用户角色枚举
    // getRoleList() {
    //   this.$store.dispatch('getExamRoleList', 'ExamRoleEnum')
    // },

    // 获取用户列表
    getEmploymentUserList () {
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        params: {
          showAll: true // 管理端显示所有数据
        }
      }
      this.$store.dispatch('getEmploymentUserList', params).then(res => {
        this.total = res.total
        this.listLoading = false
      }).catch(err => {
        console.log(err)
        this.listLoading = false
      })
    },

    // 查看详情
    viewDetail (row) {
      this.currentDetail = row
      this.detailDialogVisible = true
    },

    // 删除申请记录
    handleDelete (row) {
      this.$store.dispatch('deleteEmploymentUser', row.row.id).then(res => {
        this.getEmploymentUserList()
        this.$notify({
          title: 'Success',
          message: '删除成功',
          type: 'success',
          duration: 2000
        })
      }).catch(err => {
        this.$message.error(err)
      })
    },

    // 按条件查询用户
    handleQuery () {
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        condition: {
          name: this.listQuery.title,
          showAll: true
        }
      }
      this.$store.dispatch('queryEmploymentUser', params).then(res => {
        this.total = res.total
      })
    },
    down (item) {
      if (item.extension === '.pdf') {
        var oReq = new XMLHttpRequest()
        var URLToPDF = item.resumeUrl
        oReq.open('GET', URLToPDF, true)
        oReq.responseType = 'blob'
        oReq.onload = function () {
          var file = new Blob([oReq.response], {
            type: 'application/pdf'
          })
          // 保存文件设置文件名
          saveAs(file, item.resumeName)
        }
        oReq.send()
      }
      if (item.extension === '.doc' || item.extension === '.docx') {
        const x = new XMLHttpRequest()
        x.open('GET', item.type, true)
        x.responseType = 'blob'
        x.onload = function () {
          const url = window.URL.createObjectURL(x.response)
          const a = document.createElement('a')
          a.href = url
          a.download = item.resumeName // 名字可自定义
          a.click()
        }
        x.send()
      }
    },
    // 在线查看简历
    viewResume (item) {
      if (!item || !item.resumeUrl) {
        this.$message.warning('该申请者未上传简历')
        return
      }

      this.currentResume = item

      if (item.extension === '.pdf') {
        // PDF文件直接在iframe中显示
        this.currentPdfUrl = item.resumeUrl
        this.pdfDialogVisible = true
      } else if (item.extension === '.doc' || item.extension === '.docx') {
        // Word文档使用在线预览服务
        const previewUrl = `https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(item.resumeUrl)}`
        this.currentPdfUrl = previewUrl
        this.pdfDialogVisible = true
      } else {
        // 不支持的格式，显示下载选项
        this.$confirm(`该简历格式 (${item.extension}) 不支持在线预览，是否下载查看？`, '提示', {
          confirmButtonText: '下载',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.down(item)
        }).catch(() => {
          // 用户取消
        })
      }
    },

    // 下载当前查看的简历
    downloadCurrentResume () {
      if (this.currentResume) {
        this.down(this.currentResume)
      }
    },

    preview (item) {
      /* TODO */
      if (item.extension === '.doc' || item.extension === '.docx') {
        const href = 'http://localhost:3030/' + item.resumeUrl
        window.open(href, '_blank')
      }
      if (item.extension === '.pdf') {
        window.open(item.resumeUrl, '_blank')
      }
    }
  }

}
</script>

<style scoped>
.pdf-container {
  width: 100%;
  height: 600px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.no-pdf {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.no-pdf i {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-pdf p {
  font-size: 16px;
  margin: 0;
}

.download-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #606266;
  text-align: center;
}

.download-prompt i {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 20px;
}

.download-prompt h3 {
  font-size: 20px;
  margin: 0 0 16px 0;
  color: #303133;
}

.download-prompt p {
  font-size: 14px;
  margin: 8px 0;
  color: #909399;
}

.download-prompt .el-button {
  margin-top: 20px;
}

.resume-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
  flex-wrap: wrap;
}

.resume-buttons .el-button {
  margin: 0;
  padding: 5px 8px;
  font-size: 12px;
}

.status-text {
  color: #909399;
  font-size: 12px;
}

.el-table {
  font-size: 13px;
}

.el-table .el-table__cell {
  padding: 8px 0;
}

.reply-content {
  max-height: 100px;
  overflow-y: auto;
  line-height: 1.6;
  word-break: break-all;
}
</style>
