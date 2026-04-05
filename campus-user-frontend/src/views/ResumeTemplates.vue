<template>
    <div class="resume-app">
      <!-- View 1: 模板选择大厅 (Gallery View) -->
      <div v-if="currentView === 'gallery'" class="gallery-view">
        <!-- Header -->
        <div class="header-section">
          <div class="header-bg-pattern"></div>
          <div class="header-inner">
            <div class="header-left">
              <div class="logo-mark">
                <i class="el-icon-document text-white" style="font-size: 24px; color: white;"></i>
              </div>
              <div>
                <h1 class="page-title">简历模板中心</h1>
                <p class="page-subtitle">选择一款心仪的模板，开启你的职业生涯</p>
              </div>
            </div>
            <div class="header-right">
              <el-button plain icon="el-icon-back" @click="goBack">返回管理</el-button>
            </div>
          </div>
        </div>

        <div class="template-hint-banner">
          <i class="el-icon-info"></i>
          <span>完成编辑后请导出 PDF，再到「简历管理」上传，以便投递职位时使用与后台一致的简历文件。</span>
        </div>
  
        <!-- Filters & Grid -->
        <div class="main-content">
          <div class="container">
            <!-- Filter Bar -->
            <div class="filter-bar">
              <div class="filter-tabs">
                <span 
                  v-for="tab in categories" 
                  :key="tab.id" 
                  :class="['filter-tab', { active: activeCategory === tab.id }]"
                  @click="activeCategory = tab.id"
                >
                  {{ tab.name }}
                </span>
              </div>
              <div class="filter-search">
                <el-input placeholder="搜索模板..." prefix-icon="el-icon-search" v-model="searchQuery" size="small"></el-input>
              </div>
            </div>
  
            <!-- Templates Grid -->
            <div class="templates-grid">
              <div v-for="tpl in filteredTemplates" :key="tpl.id" class="template-card">
                <div class="card-image-wrapper">
                  <!-- 模拟缩略图 -->
                  <div :class="['mock-thumbnail', tpl.styleClass]">
                    <div class="mock-header"></div>
                    <div class="mock-body">
                      <div class="mock-line w-40"></div>
                      <div class="mock-line w-70"></div>
                      <div class="mock-block"></div>
                      <div class="mock-block"></div>
                    </div>
                  </div>
                  <div class="card-overlay">
                    <el-button type="primary" round icon="el-icon-edit" @click="selectTemplate(tpl)">使用此模板</el-button>
                  </div>
                </div>
                <div class="card-info">
                  <div class="card-header-row">
                    <h3 class="template-title">{{ tpl.name }}</h3>
                    <el-tag size="mini" :type="tpl.tagType">{{ tpl.category }}</el-tag>
                  </div>
                  <p class="template-desc">{{ tpl.description }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- View 2: 分屏编辑器 (Editor View) -->
      <div v-else class="editor-view">
        <!-- Editor Toolbar -->
        <div class="editor-header">
          <div class="editor-header-left">
            <el-button type="text" icon="el-icon-arrow-left" @click="currentView = 'gallery'">更换模板</el-button>
            <span class="divider">|</span>
            <span class="current-tpl-name">正在编辑: {{ selectedTemplate.name }}</span>
          </div>
          <div class="editor-header-right">
            <div class="theme-picker">
              <span>主题色:</span>
              <el-color-picker v-model="resumeConfig.themeColor" size="mini"></el-color-picker>
            </div>
            <el-button type="primary" size="small" icon="el-icon-download" @click="exportPDF" :loading="exporting">导出 PDF</el-button>
          </div>
        </div>
  
        <div class="editor-body">
          <!-- Left: Form Panel -->
          <div class="editor-panel scrollbar-custom">
            <el-collapse v-model="activeCollapse">
              <!-- 1. 基本信息 -->
              <el-collapse-item title="基本信息" name="1">
                <div class="form-grid">
                  <el-input v-model="resumeData.base.name" placeholder="姓名" size="small"><template slot="prepend">姓名</template></el-input>
                  <el-input v-model="resumeData.base.job" placeholder="求职意向" size="small"><template slot="prepend">职位</template></el-input>
                  <el-input v-model="resumeData.base.phone" placeholder="电话" size="small"><template slot="prepend">电话</template></el-input>
                  <el-input v-model="resumeData.base.email" placeholder="邮箱" size="small"><template slot="prepend">邮箱</template></el-input>
                  <el-input v-model="resumeData.base.summary" type="textarea" :rows="3" placeholder="个人简介/优势" class="full-width"></el-input>
                </div>
              </el-collapse-item>
  
              <!-- 2. 工作经历 -->
              <el-collapse-item title="工作经历" name="2">
                <div v-for="(job, idx) in resumeData.experience" :key="idx" class="dynamic-item">
                  <div class="item-header">
                    <span>经历 {{ idx + 1 }}</span>
                    <el-button type="text" class="text-danger" @click="removeItem('experience', idx)">删除</el-button>
                  </div>
                  <div class="form-grid">
                    <el-input v-model="job.company" placeholder="公司名称" size="small"></el-input>
                    <el-input v-model="job.post" placeholder="职位" size="small"></el-input>
                    <el-input v-model="job.time" placeholder="时间段" size="small"></el-input>
                    <el-input v-model="job.desc" type="textarea" placeholder="工作内容与成就" class="full-width"></el-input>
                  </div>
                </div>
                <el-button type="dashed" class="add-btn" icon="el-icon-plus" @click="addItem('experience')">添加工作经历</el-button>
              </el-collapse-item>
  
              <!-- 3. 教育背景 -->
              <el-collapse-item title="教育背景" name="3">
                <div v-for="(edu, idx) in resumeData.education" :key="idx" class="dynamic-item">
                  <div class="item-header">
                    <span>教育 {{ idx + 1 }}</span>
                    <el-button type="text" class="text-danger" @click="removeItem('education', idx)">删除</el-button>
                  </div>
                  <div class="form-grid">
                    <el-input v-model="edu.school" placeholder="学校" size="small"></el-input>
                    <el-input v-model="edu.major" placeholder="专业" size="small"></el-input>
                    <el-input v-model="edu.degree" placeholder="学历" size="small"></el-input>
                    <el-input v-model="edu.time" placeholder="时间" size="small"></el-input>
                  </div>
                </div>
                <el-button type="dashed" class="add-btn" icon="el-icon-plus" @click="addItem('education')">添加教育背景</el-button>
              </el-collapse-item>
            </el-collapse>
          </div>
  
          <!-- Right: Live Preview Panel -->
          <div class="preview-panel">
            <div class="preview-toolbar">
              <el-radio-group v-model="resumeConfig.fontSize" size="mini">
                <el-radio-button label="small">小字号</el-radio-button>
                <el-radio-button label="normal">标准</el-radio-button>
                <el-radio-button label="large">大字号</el-radio-button>
              </el-radio-group>
            </div>
            
            <div class="preview-scroller">
              <!-- A4 Paper Container -->
              <div id="resume-preview-area" class="a4-paper" :class="[selectedTemplate.styleClass, resumeConfig.fontSize]" :style="{'--theme-color': resumeConfig.themeColor}">
                
                <!-- HEADER -->
                <div class="resume-header">
                  <h1 class="r-name">{{ resumeData.base.name || '您的姓名' }}</h1>
                  <p class="r-job">{{ resumeData.base.job || '求职意向' }}</p>
                  <div class="r-contacts">
                    <span v-if="resumeData.base.phone">{{ resumeData.base.phone }}</span>
                    <span v-if="resumeData.base.email"> | {{ resumeData.base.email }}</span>
                  </div>
                </div>
  
                <!-- BODY -->
                <div class="resume-body">
                  <!-- Summary -->
                  <section v-if="resumeData.base.summary" class="r-section">
                    <h3 class="r-title">个人简介</h3>
                    <p class="r-text">{{ resumeData.base.summary }}</p>
                  </section>
  
                  <!-- Experience -->
                  <section class="r-section" v-if="resumeData.experience.length">
                    <h3 class="r-title">工作经历</h3>
                    <div v-for="(job, i) in resumeData.experience" :key="'job'+i" class="r-item">
                      <div class="r-item-head">
                        <span class="r-bold">{{ job.company }}</span>
                        <span class="r-right">{{ job.time }}</span>
                      </div>
                      <div class="r-sub">{{ job.post }}</div>
                      <p class="r-desc">{{ job.desc }}</p>
                    </div>
                  </section>
  
                  <!-- Education -->
                  <section class="r-section" v-if="resumeData.education.length">
                    <h3 class="r-title">教育背景</h3>
                    <div v-for="(edu, i) in resumeData.education" :key="'edu'+i" class="r-item">
                      <div class="r-item-head">
                        <span class="r-bold">{{ edu.school }}</span>
                        <span class="r-right">{{ edu.time }}</span>
                      </div>
                      <div class="r-sub">{{ edu.major }} - {{ edu.degree }}</div>
                    </div>
                  </section>
                </div>
  
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import html2canvas from 'html2canvas'
  import { jsPDF } from 'jspdf'

  export default {
    name: 'ResumeBuilder',
    data() {
      return {
        currentView: 'gallery', // gallery | editor
        activeCategory: 'all',
        searchQuery: '',
        exporting: false,
        activeCollapse: ['1', '2', '3'],
        
        // 用户数据
        resumeData: {
          base: { name: '张三', job: '高级前端工程师', phone: '13800138000', email: 'zhangsan@example.com', summary: '拥有5年Vue开发经验，擅长组件库建设与性能优化。' },
          experience: [
            { company: '科技创新有限公司', post: '前端负责人', time: '2021.06 - 至今', desc: '负责公司核心SaaS平台的前端架构设计，带领5人团队重构旧系统，提升加载速度50%。' }
          ],
          education: [
            { school: '清华大学', major: '计算机科学与技术', degree: '本科', time: '2015.09 - 2019.06' }
          ]
        },
  
        // 外观配置
        resumeConfig: {
          themeColor: '#409EFF',
          fontSize: 'normal'
        },
  
        // 模板库
        selectedTemplate: null,
        categories: [
          { id: 'all', name: '全部' },
          { id: 'classic', name: '经典通用' },
          { id: 'creative', name: '创意设计' },
          { id: 'professional', name: '商务专业' }
        ],
        templates: [
          { id: 1, name: '极简经典', category: '经典通用', styleClass: 'tpl-classic', description: '清晰的黑白排版，适合大多数行业，强调内容本身。', tagType: 'info' },
          { id: 2, name: '现代侧边', category: '创意设计', styleClass: 'tpl-modern', description: '左侧个人信息栏，右侧经历详情，极具现代感。', tagType: 'success' },
          { id: 3, name: '商务精英', category: '商务专业', styleClass: 'tpl-business', description: '带有强调色的标题栏，展现专业与稳重。', tagType: 'warning' },
          { id: 4, name: '艺术留白', category: '创意设计', styleClass: 'tpl-art', description: '大量留白与独特字体，适合设计师与媒体人。', tagType: 'danger' }
        ]
      }
    },
    computed: {
      filteredTemplates() {
        return this.templates.filter(t => {
          const matchCat = this.activeCategory === 'all' || t.category === this.categories.find(c => c.id === this.activeCategory).name;
          const matchSearch = t.name.includes(this.searchQuery);
          return matchCat && matchSearch;
        });
      }
    },
    methods: {
      goBack() {
        // 这里的路由根据你实际项目调整
        this.$router.push('/resume'); 
      },
      selectTemplate(tpl) {
        this.selectedTemplate = tpl;
        // 根据模板预设颜色（可选）
        if (tpl.styleClass === 'tpl-classic') this.resumeConfig.themeColor = '#333333';
        if (tpl.styleClass === 'tpl-business') this.resumeConfig.themeColor = '#1e3a8a';
        this.currentView = 'editor';
      },
      addItem(type) {
        if (type === 'experience') {
          this.resumeData.experience.push({ company: '', post: '', time: '', desc: '' });
        } else {
          this.resumeData.education.push({ school: '', major: '', degree: '', time: '' });
        }
      },
      removeItem(type, index) {
        this.$confirm('确定删除此条记录吗?', '提示', { type: 'warning' }).then(() => {
          this.resumeData[type].splice(index, 1);
        }).catch(() => {});
      },
      async exportPDF() {
        const el = document.getElementById('resume-preview-area')
        if (!el) {
          this.$message.error('未找到简历预览区域')
          return
        }
        this.exporting = true
        try {
          const scroller = el.closest('.preview-scroller')
          if (scroller) scroller.scrollTop = 0

          const canvas = await html2canvas(el, {
            scale: 2,
            useCORS: true,
            logging: false,
            backgroundColor: '#ffffff',
            scrollX: 0,
            scrollY: 0
          })

          const imgData = canvas.toDataURL('image/png', 1.0)
          const pdf = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' })
          const pdfWidth = pdf.internal.pageSize.getWidth()
          const pdfHeight = pdf.internal.pageSize.getHeight()
          const imgWidth = pdfWidth
          const imgHeight = (canvas.height * pdfWidth) / canvas.width

          let heightLeft = imgHeight
          let position = 0

          pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
          heightLeft -= pdfHeight

          while (heightLeft > 0) {
            position -= pdfHeight
            pdf.addPage()
            pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
            heightLeft -= pdfHeight
          }

          const baseName = (this.resumeData.base.name || '简历').replace(/[/\\?%*:|"<>]/g, '')
          pdf.save(`${baseName}_简历.pdf`)
          this.$message.success('PDF 已生成并开始下载')
        } catch (e) {
          console.error(e)
          this.$message.error('导出失败，请稍后重试')
        } finally {
          this.exporting = false
        }
      }
    }
  }
  </script>
  
  <style scoped>
  /* ================= 通用布局 ================= */
  .resume-app {
    min-height: 100vh;
    background-color: #f3f4f6;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    color: #1f2937;
    --primary: #ff6b00;
  }
  
  /* ================= 1. Gallery View Styles ================= */
  .header-section {
    background: linear-gradient(135deg, #ff6b00 0%, #ff9e56 100%);
    color: white;
    padding: 40px 0;
    position: relative;
    overflow: hidden;
  }
  
  .header-bg-pattern {
    position: absolute;
    top:0; left:0; right:0; bottom:0;
    opacity: 0.1;
    background-image: radial-gradient(#fff 2px, transparent 2px);
    background-size: 30px 30px;
  }
  
  .header-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 1;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  
  .logo-mark {
    width: 48px; height: 48px;
    background: rgba(255,255,255,0.2);
    border-radius: 12px;
    display: flex; align-items: center; justify-content: center;
  }
  
  .page-title {
    font-size: 28px;
    margin: 0 0 5px 0;
    font-weight: 700;
    color: white;
  }
  
  .page-subtitle {
    margin: 0;
    opacity: 0.9;
    color: white;
  }
  
  .container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
  
  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 30px 0;
  }
  
  .filter-tabs {
    display: flex;
    gap: 8px;
    background: white;
    padding: 4px;
    border-radius: 8px;
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  }
  
  .filter-tab {
    padding: 8px 20px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s;
    color: #6b7280;
  }
  
  .filter-tab:hover { color: #ff6b00; }
  .filter-tab.active { background: #ff6b00; color: white; }

  .template-hint-banner {
    max-width: 1100px;
    margin: 0 auto;
    padding: 10px 24px 0;
    display: flex;
    align-items: flex-start;
    gap: 8px;
    font-size: 13px;
    color: #6b7280;
    line-height: 1.5;
  }
  .template-hint-banner .el-icon-info {
    color: #ff6b00;
    margin-top: 2px;
    flex-shrink: 0;
  }
  
  .templates-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 30px;
    padding-bottom: 60px;
  }
  
  .template-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    border: 1px solid #e5e7eb;
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .template-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
  }
  
  .card-image-wrapper {
    height: 320px;
    background: #f9fafb;
    position: relative;
    overflow: hidden;
    display: flex; justify-content: center; padding-top: 20px;
  }
  
  .template-card:hover .card-overlay {
    opacity: 1;
  }
  
  /* CSS Mockups for thumbnails */
  .mock-thumbnail {
    width: 220px; height: 320px;
    background: white;
    box-shadow: 0 4px 10px rgba(0,0,0,0.05);
    padding: 15px;
  }
  
  .mock-thumbnail.tpl-classic .mock-header { height: 30px; background: #333; margin-bottom: 10px; }
  .mock-thumbnail.tpl-modern { border-left: 30px solid #e0f2fe; }
  .mock-thumbnail.tpl-business .mock-header { border-bottom: 2px solid #1e3a8a; }
  
  .mock-header { height: 20px; background: #e5e7eb; margin-bottom: 10px; border-radius: 2px; }
  .mock-line { height: 6px; background: #f3f4f6; margin-bottom: 6px; border-radius: 2px; }
  .mock-block { height: 40px; background: #f3f4f6; margin-top: 15px; border-radius: 2px; }
  .w-40 { width: 40%; }
  .w-70 { width: 70%; }
  
  .card-overlay {
    position: absolute; inset: 0;
    background: rgba(255,255,255,0.85);
    backdrop-filter: blur(2px);
    display: flex; align-items: center; justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
  }
  
  .card-info {
    padding: 16px;
    border-top: 1px solid #e5e7eb;
  }
  
  .card-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
  .template-title { margin: 0; font-size: 16px; font-weight: 600; color: #333; }
  .template-desc { margin: 0; font-size: 12px; color: #6b7280; line-height: 1.5; }
  
  /* ================= 2. Editor View Styles ================= */
  .editor-view {
    height: 100vh;
    display: flex;
    flex-direction: column;
  }
  
  .editor-header {
    height: 60px;
    background: white;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    flex-shrink: 0;
    z-index: 10;
  }
  
  .editor-header-left {
    display: flex; align-items: center; gap: 10px;
  }
  .editor-header-left .divider { color: #e5e7eb; }
  .editor-header-left .current-tpl-name { font-size: 14px; color: #4b5563; font-weight: 500; }
  
  .editor-header-right {
    display: flex; align-items: center; gap: 20px;
  }
  
  .editor-header-right .theme-picker {
    display: flex; align-items: center; gap: 8px; font-size: 12px; color: #6b7280;
  }
  
  .editor-body {
    flex: 1;
    display: flex;
    overflow: hidden;
  }
  
  /* Left Panel */
  .editor-panel {
    width: 400px;
    background: white;
    border-right: 1px solid #e5e7eb;
    overflow-y: auto;
    padding: 20px;
    flex-shrink: 0;
  }
  
  .form-grid {
    display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 10px;
  }
  .full-width { grid-column: span 2; }
  
  .dynamic-item {
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 12px;
  }
  
  .item-header {
    display: flex; justify-content: space-between; margin-bottom: 8px;
    font-size: 12px; font-weight: 600; color: #6b7280;
  }
  
  .add-btn { width: 100%; border-style: dashed !important; }
  .text-danger { color: #ef4444; }
  
  /* Right Panel */
  .preview-panel {
    flex: 1;
    background: #525659; /* Acrobat Reader grey */
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    overflow-y: auto;
    position: relative;
  }
  
  .preview-toolbar {
    background: rgba(0,0,0,0.5);
    padding: 8px 16px;
    border-radius: 20px;
    margin-bottom: 20px;
    position: sticky;
    top: 0;
    z-index: 5;
  }
  
  /* A4 Paper Simulation */
  .a4-paper {
    width: 210mm;
    min-height: 297mm;
    background: white;
    box-shadow: 0 0 20px rgba(0,0,0,0.3);
    padding: 40px;
    box-sizing: border-box;
    color: #333;
    transition: all 0.3s;
    
    /* Default Variable */
    --theme-color: #409EFF;
  }
  
  /* ================= 3. Template Styles (Inside A4) ================= */
  
  /* Common Typography */
  .r-name { font-size: 24px; margin: 0 0 5px; color: var(--theme-color); }
  .r-job { font-size: 16px; margin: 0 0 10px; color: #666; }
  .r-contacts { font-size: 14px; color: #666; }
  
  .r-title { 
    font-size: 16px; 
    border-bottom: 2px solid var(--theme-color); 
    padding-bottom: 4px; 
    margin: 20px 0 12px; 
    color: #333;
    text-transform: uppercase;
  }
  
  .r-item { margin-bottom: 12px; }
  .r-item-head { display: flex; justify-content: space-between; margin-bottom: 2px; }
  .r-bold { font-weight: 700; }
  .r-right { color: #666; font-size: 14px; }
  .r-sub { color: #444; font-weight: 500; font-size: 14px; margin-bottom: 4px; }
  .r-desc { font-size: 13px; color: #555; line-height: 1.5; margin: 0; white-space: pre-line; }
  .r-text { font-size: 14px; line-height: 1.6; color: #444; }
  
  /* Font Size Modifiers */
  .a4-paper.small { font-size: 12px; }
  .a4-paper.large { font-size: 16px; }
  
  /* Specific Template Overrides */
  
  /* Classic */
  .tpl-classic { text-align: left; }
  .tpl-classic .r-name { color: #000; }
  .tpl-classic .r-title { border-bottom: 1px solid #999; color: #000; border-color: #000; }
  
  /* Modern */
  .tpl-modern {
    display: grid;
    grid-template-columns: 30% 70%;
    padding: 0;
  }
  .tpl-modern .resume-header {
    grid-column: span 2;
    background: var(--theme-color);
    padding: 30px 40px;
  }
  .tpl-modern .r-name, 
  .tpl-modern .r-job, 
  .tpl-modern .r-contacts,
  .tpl-modern .r-contacts span { color: white; }
  
  .tpl-modern .resume-body {
    grid-column: span 2;
    padding: 30px 40px;
  }
  
  /* Business */
  .tpl-business .r-name { text-align: center; }
  .tpl-business .r-job { text-align: center; }
  .tpl-business .r-contacts { text-align: center; margin-bottom: 30px; }
  .tpl-business .r-title { background: #f3f4f6; padding: 6px 10px; border: none; border-left: 4px solid var(--theme-color); }
  
  /* Scrollbar Custom */
  .scrollbar-custom::-webkit-scrollbar { width: 6px; }
  .scrollbar-custom::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 3px; }
  </style>