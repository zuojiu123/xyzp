<template>
    <div class="resume-container">
      <!-- 头部区域 -->
      <div class="header-section">
        <div class="header-bg-pattern"></div>
        <div class="header-inner">
          <div class="header-left">
            <div class="logo-mark">
              <svg viewBox="0 0 24 24" fill="white" width="28" height="28">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/>
                <path d="M8 12h8v1.5H8zm0 3h8v1.5H8zm0 3h5v1.5H8z"/>
              </svg>
            </div>
            <div>
              <h1 class="page-title">简历管理中心</h1>
              <p class="page-subtitle">管理您的简历，轻松应对各种求职机会</p>
            </div>
          </div>
          <div class="header-right">
            <div class="quota-card">
              <div class="quota-label">简历配额</div>
              <div class="quota-visual">
                <div class="quota-bar">
                  <div 
                    class="quota-bar-fill" 
                    :style="{ width: (resumeList.length / 3 * 100) + '%' }"
                  ></div>
                </div>
                <div class="quota-dots">
                  <div 
                    v-for="(dot, index) in 3" 
                    :key="index"
                    class="quota-dot"
                    :class="{ 'active': index < resumeList.length }"
                  >
                    <svg v-if="index < resumeList.length" viewBox="0 0 24 24" width="10" height="10" fill="white">
                      <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                    </svg>
                  </div>
                </div>
                <span class="quota-num">{{ resumeList.length }}<span class="quota-divider">/</span>3</span>
              </div>
            </div>
            <button
              class="upload-main-btn"
              @click="handleUploadClick"
              :class="{ 'disabled': resumeList.length >= 3 }"
            >
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M19.35 10.04A7.49 7.49 0 0 0 12 4C9.11 4 6.6 5.64 5.35 8.04A5.994 5.994 0 0 0 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"/>
              </svg>
              <span>上传简历</span>
            </button>
          </div>
        </div>
      </div>
  
      <!-- 工具栏 -->
      <div class="toolbar-section">
        <div class="toolbar-inner">
          <div class="toolbar-left">
            <div class="search-box">
              <svg class="search-icon" viewBox="0 0 24 24" width="18" height="18">
                <path fill="#999" d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
              </svg>
              <input
                v-model="searchKeyword"
                placeholder="搜索简历名称或标签..."
                class="search-input"
                @input="handleSearch"
              />
              <button v-if="searchKeyword" class="search-clear" @click="searchKeyword = ''">
                <svg viewBox="0 0 24 24" width="16" height="16">
                  <path fill="#999" d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7A1 1 0 0 0 5.7 7.11L10.59 12 5.7 16.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.89a1 1 0 0 0 1.41-1.41L13.41 12l4.89-4.89a1 1 0 0 0 0-1.4z"/>
                </svg>
              </button>
            </div>
            <span class="result-count" v-if="searchKeyword">
              找到 <strong>{{ filteredList.length }}</strong> 份简历
            </span>
          </div>
          <div class="toolbar-right">
            <div class="view-toggle">
              <button 
                class="view-btn" 
                :class="{ active: viewMode === 'grid' }" 
                @click="viewMode = 'grid'"
                title="网格视图"
              >
                <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                  <path d="M4 8h4V4H4v4zm6 12h4v-4h-4v4zm-6 0h4v-4H4v4zm0-6h4v-4H4v4zm6 0h4v-4h-4v4zm6-10v4h4V4h-4zm-6 4h4V4h-4v4zm6 6h4v-4h-4v4zm0 6h4v-4h-4v4z"/>
                </svg>
              </button>
              <button 
                class="view-btn" 
                :class="{ active: viewMode === 'list' }" 
                @click="viewMode = 'list'"
                title="列表视图"
              >
                <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                  <path d="M4 14h4v-4H4v4zm0 5h4v-4H4v4zM4 9h4V5H4v4zm5 5h12v-4H9v4zm0 5h12v-4H9v4zM9 5v4h12V5H9z"/>
                </svg>
              </button>
            </div>
            <div class="sort-dropdown">
              <button class="sort-btn" @click="showSortMenu = !showSortMenu">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M3 18h6v-2H3v2zM3 6v2h18V6H3zm0 7h12v-2H3v2z"/>
                </svg>
                <span>{{ sortLabel }}</span>
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor" class="chevron" :class="{ open: showSortMenu }">
                  <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6z"/>
                </svg>
              </button>
              <transition name="dropdown">
                <div v-if="showSortMenu" class="sort-menu">
                  <button 
                    v-for="opt in sortOptions" 
                    :key="opt.value"
                    class="sort-option"
                    :class="{ active: sortBy === opt.value }"
                    @click="sortBy = opt.value; showSortMenu = false"
                  >
                    <svg v-if="sortBy === opt.value" viewBox="0 0 24 24" width="16" height="16" fill="#ff6b00">
                      <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                    </svg>
                    <span>{{ opt.label }}</span>
                  </button>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 主要内容区域 -->
      <div class="content-section">
        <div class="content-inner">
          <!-- 网格视图 -->
          <transition-group 
            v-if="filteredList.length > 0" 
            name="card" 
            tag="div" 
            :class="['resume-grid', viewMode === 'list' ? 'list-view' : '']"
          >
            <div 
              v-for="(resume, index) in filteredList" 
              :key="resume.id"
              class="resume-card"
              :class="{ 'is-default': resume.isDefault }"
              :style="{ animationDelay: index * 0.08 + 's' }"
            >
              <!-- 默认标记 -->
              <div v-if="resume.isDefault" class="default-ribbon">
                <svg viewBox="0 0 24 24" width="12" height="12" fill="white">
                  <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                </svg>
                <span>默认</span>
              </div>
              
              <!-- 文件预览区 -->
              <div class="card-preview-area" @click="previewResume(resume)">
                <div class="preview-doc-mock">
                  <div class="mock-header-line"></div>
                  <div class="mock-line w60"></div>
                  <div class="mock-line w100"></div>
                  <div class="mock-line w85"></div>
                  <div class="mock-line w92"></div>
                  <div class="mock-line w70"></div>
                  <div class="mock-section-line"></div>
                  <div class="mock-line w100"></div>
                  <div class="mock-line w78"></div>
                  <div class="mock-line w90"></div>
                </div>
                <div class="preview-hover-mask">
                  <div class="preview-action">
                    <svg viewBox="0 0 24 24" width="28" height="28" fill="white">
                      <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                    </svg>
                    <span>点击预览</span>
                  </div>
                </div>
                <!-- 文件类型角标 -->
                <div class="file-type-badge" :class="'type-' + resume.type">
                  {{ resume.type === 'word' ? 'DOC' : resume.type.toUpperCase() }}
                </div>
              </div>
  
              <!-- 卡片信息区 -->
              <div class="card-body">
                <h3 class="resume-title">{{ resume.name }}</h3>
                
                <div class="tags-row" v-if="resume.tags && resume.tags.length">
                  <span 
                    v-for="tag in resume.tags.slice(0, 3)" 
                    :key="tag" 
                    class="tag-chip"
                  >{{ tag }}</span>
                  <span v-if="resume.tags.length > 3" class="tag-more">+{{ resume.tags.length - 3 }}</span>
                </div>
  
                <p v-if="resume.note" class="resume-note">{{ resume.note }}</p>
                
                <div class="meta-row">
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" width="14" height="14" fill="#aaa">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/>
                    </svg>
                    {{ formatFileSize(resume.size) }}
                  </span>
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" width="14" height="14" fill="#aaa">
                      <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67V7z"/>
                    </svg>
                    {{ formatDate(resume.updatedAt) }}
                  </span>
                </div>
              </div>
  
              <!-- 操作栏 -->
              <div class="card-footer">
                <button class="action-btn primary" @click="previewResume(resume)" title="预览">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <span>预览</span>
                </button>
                <button class="action-btn" @click="editResume(resume)" title="编辑">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                  </svg>
                </button>
                <button class="action-btn" @click="downloadResume(resume)" title="下载">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
                  </svg>
                </button>
                <button 
                  class="action-btn star" 
                  :class="{ 'is-active': resume.isDefault }"
                  @click="setDefaultResume(resume.id)" 
                  :title="resume.isDefault ? '当前默认' : '设为默认'"
                  :disabled="resume.isDefault"
                >
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                  </svg>
                </button>
                <button class="action-btn danger" @click="confirmDelete(resume.id)" title="删除">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                  </svg>
                </button>
              </div>
            </div>
          </transition-group>
  
          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-illustration">
              <div class="empty-doc">
                <div class="empty-doc-line l1"></div>
                <div class="empty-doc-line l2"></div>
                <div class="empty-doc-line l3"></div>
                <div class="empty-doc-line l4"></div>
              </div>
              <div class="empty-circle"></div>
              <svg class="empty-plus" viewBox="0 0 24 24" width="32" height="32" fill="#ff6b00">
                <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
            </div>
            <h2 class="empty-title">{{ searchKeyword ? '未找到相关简历' : '还没有简历' }}</h2>
            <p class="empty-desc">{{ searchKeyword ? '试试其他关键词吧' : '上传您的第一份简历，开启求职之旅' }}</p>
            <button v-if="!searchKeyword" class="empty-upload-btn" @click="showUploadDialog">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
              上传简历
            </button>
            <button v-else class="empty-clear-btn" @click="searchKeyword = ''">
              清除搜索
            </button>
          </div>
        </div>
      </div>
  
      <!-- ============ 上传/编辑弹窗 ============ -->
      <transition name="modal">
        <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
          <div class="modal-container">
            <div class="modal-header">
              <div class="modal-header-icon" :class="editMode ? 'blue' : 'orange'">
                <svg v-if="!editMode" viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
                  <path d="M19.35 10.04A7.49 7.49 0 0 0 12 4C9.11 4 6.6 5.64 5.35 8.04A5.994 5.994 0 0 0 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
                  <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                </svg>
              </div>
              <h3>{{ editMode ? '编辑简历信息' : '上传新简历' }}</h3>
              <button class="modal-close-btn" @click="dialogVisible = false">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="#666" d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7A1 1 0 0 0 5.7 7.11L10.59 12 5.7 16.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.89a1 1 0 0 0 1.41-1.41L13.41 12l4.89-4.89a1 1 0 0 0 0-1.4z"/>
                </svg>
              </button>
            </div>
  
            <div class="modal-body">
              <!-- 上传区域 -->
              <div class="form-section" v-if="!editMode || formData.file">
                <label class="form-label">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="#ff6b00">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 9V4l6 6h-5a1 1 0 0 1-1-1z"/>
                  </svg>
                  简历文件
                  <span class="required">*</span>
                </label>
                <div 
                  class="drop-zone"
                  :class="{ 'drag-over': isDragging, 'has-file': selectedFile }"
                  @click="$refs.fileInput.click()"
                  @dragover.prevent="isDragging = true"
                  @dragleave="isDragging = false"
                  @drop.prevent="handleDrop"
                >
                  <input 
                    ref="fileInput" 
                    type="file" 
                    class="hidden-input"
                    accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                    @change="handleFileSelect"
                  />
                  <div v-if="!selectedFile" class="drop-zone-content">
                    <div class="drop-zone-icon">
                      <svg viewBox="0 0 24 24" width="36" height="36">
                        <path fill="#ccc" d="M19.35 10.04A7.49 7.49 0 0 0 12 4C9.11 4 6.6 5.64 5.35 8.04A5.994 5.994 0 0 0 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"/>
                      </svg>
                    </div>
                    <p class="drop-zone-text">拖拽文件到这里，或 <em>点击选择</em></p>
                    <p class="drop-zone-hint">支持 PDF / Word / 图片，最大 5MB</p>
                    <div class="format-tags">
                      <span class="fmt-tag">PDF</span>
                      <span class="fmt-tag">DOC</span>
                      <span class="fmt-tag">DOCX</span>
                      <span class="fmt-tag">JPG</span>
                      <span class="fmt-tag">PNG</span>
                    </div>
                  </div>
                  <div v-else class="selected-file-card">
                    <div class="sf-icon" :class="'type-' + getFileType(selectedFile.name)">
                      {{ getFileType(selectedFile.name) === 'word' ? 'DOC' : getFileType(selectedFile.name).toUpperCase() }}
                    </div>
                    <div class="sf-info">
                      <div class="sf-name">{{ selectedFile.name }}</div>
                      <div class="sf-size">{{ formatFileSize(selectedFile.size) }}</div>
                    </div>
                    <button class="sf-remove" @click.stop="removeFile">
                      <svg viewBox="0 0 24 24" width="18" height="18">
                        <path fill="#999" d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7A1 1 0 0 0 5.7 7.11L10.59 12 5.7 16.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.89a1 1 0 0 0 1.41-1.41L13.41 12l4.89-4.89a1 1 0 0 0 0-1.4z"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
  
              <!-- 当前文件（编辑模式） -->
              <div v-if="editMode && !formData.file" class="form-section">
                <label class="form-label">当前文件</label>
                <div class="current-file-display">
                  <div class="sf-icon type-current">
                    <svg viewBox="0 0 24 24" width="18" height="18" fill="white">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/>
                    </svg>
                  </div>
                  <span class="current-file-name">{{ getCurrentFileName() }}</span>
                  <button class="change-file-btn" @click="$refs.fileInput.click()">更换文件</button>
                </div>
              </div>
  
              <!-- 简历名称 -->
              <div class="form-section">
                <label class="form-label">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="#ff6b00">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                  </svg>
                  简历名称
                  <span class="required">*</span>
                </label>
                <input 
                  v-model="formData.name" 
                  type="text" 
                  class="form-input"
                  placeholder="例如：前端工程师简历"
                  maxlength="50"
                />
                <span v-if="formErrors.name" class="form-error">{{ formErrors.name }}</span>
              </div>
  
              <!-- 标签 -->
              <div class="form-section">
                <label class="form-label">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="#ff6b00">
                    <path d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z"/>
                  </svg>
                  标签
                </label>
                <div class="tags-selector">
                  <button 
                    v-for="opt in tagOptions" 
                    :key="opt"
                    class="tag-option"
                    :class="{ selected: formData.tags.includes(opt) }"
                    @click="toggleTag(opt)"
                  >
                    {{ opt }}
                  </button>
                </div>
              </div>
  
              <!-- 备注 -->
              <div class="form-section">
                <label class="form-label">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="#ff6b00">
                    <path d="M21 6h-2v9H6v2c0 .55.45 1 1 1h11l4 4V7c0-.55-.45-1-1-1zm-4 6V3c0-.55-.45-1-1-1H3c-.55 0-1 .45-1 1v14l4-4h10c.55 0 1-.45 1-1z"/>
                  </svg>
                  备注
                </label>
                <textarea 
                  v-model="formData.note" 
                  class="form-textarea"
                  placeholder="添加备注信息..."
                  rows="3"
                  maxlength="200"
                ></textarea>
                <div class="char-count">{{ (formData.note || '').length }}/200</div>
              </div>
            </div>
  
            <div class="modal-footer">
              <button class="modal-btn cancel" @click="dialogVisible = false">取消</button>
              <button class="modal-btn confirm" @click="submitForm" :disabled="submitLoading">
                <svg v-if="submitLoading" class="spin" viewBox="0 0 24 24" width="18" height="18" fill="white">
                  <path d="M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46A7.93 7.93 0 0 0 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74A7.93 7.93 0 0 0 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z"/>
                </svg>
                {{ editMode ? '保存修改' : '确认上传' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
  
      <!-- ============ 删除确认弹窗 ============ -->
      <transition name="modal">
        <div v-if="deleteDialogVisible" class="modal-overlay" @click.self="deleteDialogVisible = false">
          <div class="modal-container modal-sm">
            <div class="modal-header">
              <div class="modal-header-icon red">
                <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
                  <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                </svg>
              </div>
              <h3>删除确认</h3>
              <button class="modal-close-btn" @click="deleteDialogVisible = false">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="#666" d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7A1 1 0 0 0 5.7 7.11L10.59 12 5.7 16.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.89a1 1 0 0 0 1.41-1.41L13.41 12l4.89-4.89a1 1 0 0 0 0-1.4z"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="delete-warning-box">
                <div class="warning-icon-circle">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="#dc2626">
                    <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
                  </svg>
                </div>
                <div>
                  <h4>确定要删除这份简历吗？</h4>
                  <p>此操作不可撤销，文件将被永久删除。</p>
                </div>
              </div>
              <div v-if="deleteTargetResume" class="delete-file-card">
                <div class="sf-icon" :class="'type-' + deleteTargetResume.type">
                  {{ deleteTargetResume.type === 'word' ? 'DOC' : deleteTargetResume.type.toUpperCase() }}
                </div>
                <div class="sf-info">
                  <div class="sf-name">{{ deleteTargetResume.name }}</div>
                  <div class="sf-size">{{ formatFileSize(deleteTargetResume.size) }} · {{ formatDate(deleteTargetResume.updatedAt) }}</div>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="modal-btn cancel" @click="deleteDialogVisible = false">取消</button>
              <button class="modal-btn danger" @click="deleteResume">确认删除</button>
            </div>
          </div>
        </div>
      </transition>
  
      <!-- ============ 预览弹窗 ============ -->
      <transition name="modal">
        <div v-if="previewDialogVisible" class="modal-overlay preview-overlay" @click.self="previewDialogVisible = false">
          <div class="modal-container modal-preview">
            <div class="modal-header">
              <div class="modal-header-icon orange">
                <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
                  <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                </svg>
              </div>
              <h3>{{ previewResumeName }}</h3>
              <button class="modal-close-btn" @click="previewDialogVisible = false">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="#666" d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7A1 1 0 0 0 5.7 7.11L10.59 12 5.7 16.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.89a1 1 0 0 0 1.41-1.41L13.41 12l4.89-4.89a1 1 0 0 0 0-1.4z"/>
                </svg>
              </button>
            </div>
            <div class="preview-body">
              <div v-if="previewType === 'pdf'" class="preview-frame-wrap">
                <iframe :src="previewUrl" frameborder="0"></iframe>
              </div>
              <div v-else-if="previewType === 'image'" class="preview-image-wrap">
                <img :src="previewUrl" alt="简历预览" />
              </div>
              <div v-else class="preview-unsupported">
                <svg viewBox="0 0 24 24" width="56" height="56" fill="#ddd">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/>
                </svg>
                <h3>暂不支持在线预览</h3>
                <p>该格式不支持在线预览，请下载后查看</p>
                <button class="empty-upload-btn" @click="downloadPreviewResume">
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                    <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
                  </svg>
                  下载查看
                </button>
              </div>
            </div>
            <div class="preview-toolbar">
              <button class="toolbar-action" @click="downloadPreviewResume">
                <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                  <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
                </svg>
                下载简历
              </button>
            </div>
          </div>
        </div>
      </transition>
  
      <!-- Toast 提示 -->
      <transition-group name="toast" tag="div" class="toast-container">
        <div v-for="t in toasts" :key="t.id" class="toast-item" :class="t.type">
          <div class="toast-icon-wrap">
            <svg v-if="t.type === 'success'" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
            </svg>
            <svg v-else-if="t.type === 'error'" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7A1 1 0 0 0 5.7 7.11L10.59 12 5.7 16.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.89a1 1 0 0 0 1.41-1.41L13.41 12l4.89-4.89a1 1 0 0 0 0-1.4z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
            </svg>
          </div>
          <span>{{ t.message }}</span>
        </div>
      </transition-group>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ResumeManager',
    data() {
      return {
        resumeList: [],
        searchKeyword: '',
        sortBy: 'updatedAt',
        viewMode: 'grid',
        showSortMenu: false,
        dialogVisible: false,
        deleteDialogVisible: false,
        previewDialogVisible: false,
        editMode: false,
        currentResumeId: null,
        submitLoading: false,
        isDragging: false,
        selectedFile: null,
        previewUrl: '',
        previewType: '',
        previewResumeName: '简历预览',
        toasts: [],
        formData: {
          name: '',
          tags: [],
          note: '',
          file: null
        },
        formErrors: {},
        tagOptions: ['前端', '后端', '全栈', '移动端', 'UI/UX', '产品', '运营', '实习', '全职'],
        sortOptions: [
          { label: '最新上传', value: 'createdAt' },
          { label: '最近修改', value: 'updatedAt' },
          { label: '默认优先', value: 'isDefault' }
        ]
      }
    },
  
    computed: {
      filteredList() {
        let result = [...this.resumeList]
        if (this.searchKeyword) {
          const kw = this.searchKeyword.toLowerCase()
          result = result.filter(r =>
            r.name.toLowerCase().includes(kw) ||
            (r.tags && r.tags.some(t => t.toLowerCase().includes(kw)))
          )
        }
        result.sort((a, b) => {
          if (this.sortBy === 'isDefault') return (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0)
          return new Date(b[this.sortBy]) - new Date(a[this.sortBy])
        })
        return result
      },
      sortLabel() {
        const opt = this.sortOptions.find(o => o.value === this.sortBy)
        return opt ? opt.label : '排序'
      },
      deleteTargetResume() {
        return this.resumeList.find(r => r.id === this.currentResumeId) || null
      }
    },
  
    mounted() {
      this.loadResumes()
      document.addEventListener('click', this.closeSortMenu)
    },
    beforeDestroy() {
      document.removeEventListener('click', this.closeSortMenu)
    },
  
    methods: {
      loadResumes() {
        try {
          const data = localStorage.getItem('userResumes')
          if (data) this.resumeList = JSON.parse(data)
        } catch (e) { /* ignore */ }
      },
      saveResumes() {
        localStorage.setItem('userResumes', JSON.stringify(this.resumeList))
      },
      closeSortMenu(e) {
        if (!e.target.closest('.sort-dropdown')) this.showSortMenu = false
      },
  
      // Toast
      showToast(message, type = 'info') {
        const id = Date.now() + Math.random()
        this.toasts.push({ id, message, type })
        setTimeout(() => {
          this.toasts = this.toasts.filter(t => t.id !== id)
        }, 3000)
      },
  
      // Upload
      handleUploadClick() {
        if (this.resumeList.length >= 3) {
          this.showToast('最多只能上传 3 份简历', 'error')
          return
        }
        this.showUploadDialog()
      },
      showUploadDialog() {
        this.editMode = false
        this.currentResumeId = null
        this.selectedFile = null
        this.formData = { name: '', tags: [], note: '', file: null }
        this.formErrors = {}
        this.dialogVisible = true
      },
      handleFileSelect(e) {
        const file = e.target.files[0]
        if (file) this.processFile(file)
      },
      handleDrop(e) {
        this.isDragging = false
        const file = e.dataTransfer.files[0]
        if (file) this.processFile(file)
      },
      processFile(file) {
        const valid = /\.(pdf|doc|docx|jpg|jpeg|png)$/i.test(file.name)
        if (!valid) {
          this.showToast('不支持的文件格式', 'error')
          return
        }
        if (file.size > 5 * 1024 * 1024) {
          this.showToast('文件大小不能超过 5MB', 'error')
          return
        }
        this.selectedFile = file
        this.formData.file = file
        if (!this.formData.name) {
          this.formData.name = file.name.replace(/\.[^/.]+$/, '')
        }
      },
      removeFile() {
        this.selectedFile = null
        this.formData.file = null
        if (this.$refs.fileInput) this.$refs.fileInput.value = ''
      },
      toggleTag(tag) {
        const idx = this.formData.tags.indexOf(tag)
        if (idx > -1) this.formData.tags.splice(idx, 1)
        else this.formData.tags.push(tag)
      },
  
      // Submit
      submitForm() {
        this.formErrors = {}
        if (!this.formData.name.trim()) {
          this.formErrors.name = '请输入简历名称'
          return
        }
        if (!this.editMode && !this.formData.file) {
          this.showToast('请选择文件', 'error')
          return
        }
        this.submitLoading = true
        if (this.editMode) this.updateResume()
        else this.uploadFile()
      },
      uploadFile() {
        const file = this.formData.file
        const reader = new FileReader()
        reader.onload = (e) => {
          const resume = {
            id: Date.now().toString(),
            name: this.formData.name.trim(),
            type: this.getFileType(file.name),
            size: file.size,
            content: e.target.result,
            tags: [...this.formData.tags],
            note: this.formData.note.trim(),
            isDefault: this.resumeList.length === 0,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          }
          this.resumeList.push(resume)
          this.saveResumes()
          this.dialogVisible = false
          this.submitLoading = false
          this.showToast('简历上传成功', 'success')
        }
        reader.onerror = () => {
          this.showToast('文件读取失败', 'error')
          this.submitLoading = false
        }
        reader.readAsDataURL(file)
      },
  
      // Edit
      editResume(resume) {
        this.editMode = true
        this.currentResumeId = resume.id
        this.selectedFile = null
        this.formData = {
          name: resume.name,
          tags: [...(resume.tags || [])],
          note: resume.note || '',
          file: null
        }
        this.formErrors = {}
        this.dialogVisible = true
      },
      updateResume() {
        const idx = this.resumeList.findIndex(r => r.id === this.currentResumeId)
        if (idx === -1) { this.submitLoading = false; return }
  
        const updated = {
          ...this.resumeList[idx],
          name: this.formData.name.trim(),
          tags: [...this.formData.tags],
          note: this.formData.note.trim(),
          updatedAt: new Date().toISOString()
        }
  
        if (this.formData.file) {
          const file = this.formData.file
          const reader = new FileReader()
          reader.onload = (e) => {
            updated.type = this.getFileType(file.name)
            updated.size = file.size
            updated.content = e.target.result
            this.$set(this.resumeList, idx, updated)
            this.saveResumes()
            this.dialogVisible = false
            this.submitLoading = false
            this.showToast('简历更新成功', 'success')
          }
          reader.onerror = () => {
            this.showToast('文件读取失败', 'error')
            this.submitLoading = false
          }
          reader.readAsDataURL(file)
        } else {
          this.$set(this.resumeList, idx, updated)
          this.saveResumes()
          this.dialogVisible = false
          this.submitLoading = false
          this.showToast('简历更新成功', 'success')
        }
      },
      getCurrentFileName() {
        if (!this.currentResumeId) return ''
        const r = this.resumeList.find(r => r.id === this.currentResumeId)
        return r ? r.name + '.' + this.getFileExtension(r.type) : ''
      },
  
      // Preview
      previewResume(resume) {
        this.previewUrl = resume.content
        this.previewType = resume.type
        this.previewResumeName = resume.name
        this.currentResumeId = resume.id
        this.previewDialogVisible = true
      },
  
      // Download
      downloadResume(resume) {
        const link = document.createElement('a')
        link.href = resume.content
        link.download = resume.name + '.' + this.getFileExtension(resume.type)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        this.showToast('开始下载...', 'info')
      },
      downloadPreviewResume() {
        const resume = this.resumeList.find(r => r.id === this.currentResumeId)
        if (resume) this.downloadResume(resume)
      },
  
      // Default
      setDefaultResume(id) {
        this.resumeList.forEach(r => { r.isDefault = false })
        const r = this.resumeList.find(r => r.id === id)
        if (r) {
          r.isDefault = true
          this.saveResumes()
          this.showToast('已设为默认简历', 'success')
        }
      },
  
      // Delete
      confirmDelete(id) {
        this.currentResumeId = id
        this.deleteDialogVisible = true
      },
      deleteResume() {
        const idx = this.resumeList.findIndex(r => r.id === this.currentResumeId)
        if (idx !== -1) {
          const wasDefault = this.resumeList[idx].isDefault
          this.resumeList.splice(idx, 1)
          if (wasDefault && this.resumeList.length > 0) {
            this.resumeList[0].isDefault = true
          }
          this.saveResumes()
          this.deleteDialogVisible = false
          this.showToast('简历已删除', 'success')
        }
      },
  
      // Utils
      getFileType(filename) {
        const ext = filename.split('.').pop().toLowerCase()
        if (ext === 'pdf') return 'pdf'
        if (['doc', 'docx'].includes(ext)) return 'word'
        if (['jpg', 'jpeg', 'png'].includes(ext)) return 'image'
        return 'other'
      },
      getFileExtension(type) {
        const map = { pdf: 'pdf', word: 'docx', image: 'png' }
        return map[type] || 'file'
      },
      formatFileSize(size) {
        if (!size) return '0 B'
        if (size < 1024) return size + ' B'
        if (size < 1048576) return (size / 1024).toFixed(1) + ' KB'
        return (size / 1048576).toFixed(1) + ' MB'
      },
      formatDate(str) {
        if (!str) return '-'
        const d = new Date(str)
        return d.toLocaleString('zh-CN', {
          month: '2-digit', day: '2-digit',
          hour: '2-digit', minute: '2-digit'
        })
      },
      handleSearch() { /* computed handles it */ }
    }
  }
  </script>
  
  <style scoped>
  /* =============================================
     CSS Variables & Reset
     ============================================= */
  * { margin: 0; padding: 0; box-sizing: border-box; }
  
  .resume-container {
    --c-primary: #ff6b00;
    --c-primary-light: #ff8a33;
    --c-primary-dark: #e05f00;
    --c-primary-bg: #fff7f0;
    --c-primary-bg2: #fff0e0;
    --c-danger: #dc2626;
    --c-danger-bg: #fef2f2;
    --c-success: #16a34a;
    --c-success-bg: #f0fdf4;
    --c-blue: #2563eb;
    --c-blue-bg: #eff6ff;
    --c-text: #1a1a2e;
    --c-text2: #6b7280;
    --c-text3: #9ca3af;
    --c-bg: #f6f7f9;
    --c-white: #ffffff;
    --c-border: #e5e7eb;
    --c-border-light: #f0f1f3;
    --radius-sm: 8px;
    --radius-md: 12px;
    --radius-lg: 16px;
    --radius-xl: 20px;
    --shadow-sm: 0 1px 3px rgba(0,0,0,0.05);
    --shadow-md: 0 4px 20px rgba(0,0,0,0.08);
    --shadow-lg: 0 8px 40px rgba(0,0,0,0.12);
    --shadow-primary: 0 4px 20px rgba(255,107,0,0.25);
    --transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  
    min-height: 100vh;
    background: var(--c-bg);
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
    color: var(--c-text);
  }
  
  /* =============================================
     Header
     ============================================= */
  .header-section {
    background: linear-gradient(135deg, var(--c-primary) 0%, var(--c-primary-light) 60%, #ff9e56 100%);
    position: relative;
    overflow: hidden;
  }
  
  .header-bg-pattern {
    position: absolute;
    inset: 0;
    background-image:
      radial-gradient(circle at 20% 50%, rgba(255,255,255,0.08) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255,255,255,0.06) 0%, transparent 40%),
      radial-gradient(circle at 60% 80%, rgba(255,255,255,0.04) 0%, transparent 40%);
  }
  
  .header-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 36px 32px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;
    z-index: 1;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 18px;
  }
  
  .logo-mark {
    width: 52px;
    height: 52px;
    background: rgba(255,255,255,0.18);
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.15);
  }
  
  .page-title {
    font-size: 28px;
    font-weight: 800;
    color: white;
    letter-spacing: -0.5px;
    line-height: 1.2;
  }
  
  .page-subtitle {
    font-size: 14px;
    color: rgba(255,255,255,0.8);
    margin-top: 4px;
    font-weight: 400;
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 18px;
  }
  
  /* Quota Card */
  .quota-card {
    background: rgba(255,255,255,0.15);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,0.2);
    border-radius: var(--radius-md);
    padding: 12px 20px;
  }
  
  .quota-label {
    font-size: 11px;
    color: rgba(255,255,255,0.7);
    margin-bottom: 8px;
    text-transform: uppercase;
    letter-spacing: 1px;
    font-weight: 600;
  }
  
  .quota-visual {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .quota-bar {
    width: 60px;
    height: 4px;
    background: rgba(255,255,255,0.2);
    border-radius: 2px;
    overflow: hidden;
  }
  
  .quota-bar-fill {
    height: 100%;
    background: white;
    border-radius: 2px;
    transition: width 0.5s ease;
  }
  
  .quota-dots {
    display: flex;
    gap: 6px;
  }
  
  .quota-dot {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: rgba(255,255,255,0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: var(--transition);
  }
  
  .quota-dot.active {
    background: white;
    box-shadow: 0 0 0 3px rgba(255,255,255,0.25);
  }
  
  .quota-num {
    color: white;
    font-size: 18px;
    font-weight: 800;
  }
  
  .quota-divider {
    opacity: 0.5;
    margin: 0 1px;
    font-weight: 400;
  }
  
  /* Upload Main Button */
  .upload-main-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 28px;
    background: white;
    color: var(--c-primary);
    border: none;
    border-radius: var(--radius-md);
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    transition: var(--transition);
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  }
  
  .upload-main-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 24px rgba(0,0,0,0.15);
  }
  
  .upload-main-btn:active {
    transform: translateY(0);
  }
  
  .upload-main-btn.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  .upload-main-btn.disabled:hover {
    transform: none;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  }
  
  /* =============================================
     Toolbar
     ============================================= */
  .toolbar-section {
    background: var(--c-white);
    border-bottom: 1px solid var(--c-border-light);
    position: sticky;
    top: 0;
    z-index: 50;
    box-shadow: var(--shadow-sm);
  }
  
  .toolbar-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 14px 32px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
  }
  
  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 16px;
    flex: 1;
  }
  
  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  /* Search */
  .search-box {
    position: relative;
    width: 320px;
  }
  
  .search-icon {
    position: absolute;
    left: 14px;
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
  }
  
  .search-input {
    width: 100%;
    padding: 10px 40px 10px 42px;
    border: 1.5px solid var(--c-border);
    border-radius: 50px;
    font-size: 14px;
    color: var(--c-text);
    outline: none;
    transition: var(--transition);
    background: var(--c-bg);
    font-family: inherit;
  }
  
  .search-input:focus {
    border-color: var(--c-primary);
    background: var(--c-white);
    box-shadow: 0 0 0 3px rgba(255,107,0,0.1);
  }
  
  .search-input::placeholder { color: var(--c-text3); }
  
  .search-clear {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: var(--transition);
  }
  
  .search-clear:hover { background: var(--c-border-light); }
  
  .result-count {
    font-size: 13px;
    color: var(--c-text3);
    white-space: nowrap;
  }
  
  .result-count strong { color: var(--c-primary); font-weight: 700; }
  
  /* View Toggle */
  .view-toggle {
    display: flex;
    background: var(--c-bg);
    border-radius: var(--radius-sm);
    padding: 3px;
  }
  
  .view-btn {
    width: 34px;
    height: 34px;
    border: none;
    background: transparent;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: var(--c-text3);
    transition: var(--transition);
  }
  
  .view-btn.active {
    background: var(--c-white);
    color: var(--c-primary);
    box-shadow: var(--shadow-sm);
  }
  
  .view-btn:hover:not(.active) { color: var(--c-text2); }
  
  /* Sort Dropdown */
  .sort-dropdown {
    position: relative;
  }
  
  .sort-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 9px 16px;
    border: 1.5px solid var(--c-border);
    border-radius: var(--radius-sm);
    background: var(--c-white);
    font-size: 13px;
    font-weight: 600;
    color: var(--c-text2);
    cursor: pointer;
    transition: var(--transition);
    font-family: inherit;
  }
  
  .sort-btn:hover {
    border-color: var(--c-primary);
    color: var(--c-primary);
  }
  
  .chevron { transition: transform 0.2s ease; }
  .chevron.open { transform: rotate(180deg); }
  
  .sort-menu {
    position: absolute;
    top: calc(100% + 6px);
    right: 0;
    background: var(--c-white);
    border: 1px solid var(--c-border-light);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-md);
    min-width: 160px;
    padding: 6px;
    z-index: 100;
  }
  
  .sort-option {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
    padding: 10px 14px;
    border: none;
    background: transparent;
    border-radius: var(--radius-sm);
    font-size: 14px;
    color: var(--c-text2);
    cursor: pointer;
    transition: var(--transition);
    text-align: left;
    font-family: inherit;
  }
  
  .sort-option:hover { background: var(--c-bg); }
  .sort-option.active { color: var(--c-primary); font-weight: 600; background: var(--c-primary-bg); }
  
  /* Dropdown transition */
  .dropdown-enter-active, .dropdown-leave-active { transition: all 0.2s ease; }
  .dropdown-enter, .dropdown-leave-to { opacity: 0; transform: translateY(-8px); }
  
  /* =============================================
     Content
     ============================================= */
  .content-section {
    padding: 32px 0 80px;
  }
  
  .content-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 32px;
  }
  
  /* =============================================
     Resume Grid
     ============================================= */
  .resume-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
  
  .resume-grid.list-view {
    grid-template-columns: 1fr;
  }
  
  .resume-grid.list-view .resume-card {
    display: grid;
    grid-template-columns: 200px 1fr auto;
    grid-template-rows: 1fr;
  }
  
  .resume-grid.list-view .card-preview-area {
    height: 100%;
    min-height: 140px;
    border-radius: var(--radius-lg) 0 0 var(--radius-lg);
  }
  
  .resume-grid.list-view .card-footer {
    flex-direction: column;
    border-top: none;
    border-left: 1px solid var(--c-border-light);
    padding: 16px;
    justify-content: center;
  }
  
  /* Card transition */
  .card-enter-active, .card-leave-active { transition: all 0.4s ease; }
  .card-enter, .card-leave-to { opacity: 0; transform: translateY(20px); }
  
  /* =============================================
     Resume Card
     ============================================= */
  .resume-card {
    background: var(--c-white);
    border-radius: var(--radius-lg);
    overflow: hidden;
    border: 1.5px solid var(--c-border-light);
    transition: var(--transition);
    position: relative;
    animation: cardAppear 0.5s ease backwards;
  }
  
  @keyframes cardAppear {
    from { opacity: 0; transform: translateY(24px); }
    to { opacity: 1; transform: translateY(0); }
  }
  
  .resume-card:hover {
    transform: translateY(-6px);
    box-shadow: var(--shadow-lg);
    border-color: rgba(255,107,0,0.15);
  }
  
  .resume-card.is-default {
    border-color: var(--c-primary);
    box-shadow: 0 0 0 1px var(--c-primary), var(--shadow-sm);
  }
  
  .resume-card.is-default:hover {
    box-shadow: 0 0 0 1px var(--c-primary), var(--shadow-lg);
  }
  
  /* Default Ribbon */
  .default-ribbon {
    position: absolute;
    top: 14px;
    right: 14px;
    z-index: 10;
    background: var(--c-primary);
    color: white;
    padding: 4px 12px;
    border-radius: 50px;
    font-size: 11px;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 4px;
    box-shadow: var(--shadow-primary);
  }
  
  /* Card Preview Area */
  .card-preview-area {
    height: 200px;
    background: linear-gradient(180deg, #f8f9fb 0%, #f0f1f4 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    cursor: pointer;
    overflow: hidden;
  }
  
  .preview-doc-mock {
    width: 110px;
    background: white;
    border-radius: 4px;
    padding: 14px 12px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.08);
    transition: var(--transition);
    position: relative;
    z-index: 1;
  }
  
  .resume-card:hover .preview-doc-mock {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  }
  
  .mock-header-line {
    height: 5px;
    width: 55%;
    background: var(--c-primary);
    opacity: 0.7;
    border-radius: 3px;
    margin-bottom: 8px;
  }
  
  .mock-section-line {
    height: 4px;
    width: 40%;
    background: var(--c-primary);
    opacity: 0.4;
    border-radius: 2px;
    margin: 8px 0 6px;
  }
  
  .mock-line {
    height: 3px;
    background: #e8e8e8;
    border-radius: 2px;
    margin-bottom: 4px;
  }
  
  .mock-line.w60 { width: 60%; }
  .mock-line.w70 { width: 70%; }
  .mock-line.w78 { width: 78%; }
  .mock-line.w85 { width: 85%; }
  .mock-line.w90 { width: 90%; }
  .mock-line.w92 { width: 92%; }
  .mock-line.w100 { width: 100%; }
  
  /* Preview Hover Mask */
  .preview-hover-mask {
    position: absolute;
    inset: 0;
    background: rgba(255,107,0,0.85);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: var(--transition);
    z-index: 2;
    backdrop-filter: blur(4px);
  }
  
  .card-preview-area:hover .preview-hover-mask { opacity: 1; }
  
  .preview-action {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    color: white;
    font-size: 13px;
    font-weight: 600;
  }
  
  /* File Type Badge */
  .file-type-badge {
    position: absolute;
    bottom: 10px;
    left: 10px;
    z-index: 3;
    padding: 3px 10px;
    border-radius: 50px;
    font-size: 10px;
    font-weight: 800;
    letter-spacing: 0.5px;
  }
  
  .file-type-badge.type-pdf {
    background: #fef2f2;
    color: #dc2626;
  }
  
  .file-type-badge.type-word {
    background: #eff6ff;
    color: #2563eb;
  }
  
  .file-type-badge.type-image {
    background: #f0fdf4;
    color: #16a34a;
  }
  
  /* Card Body */
  .card-body {
    padding: 18px 20px 14px;
  }
  
  .resume-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--c-text);
    margin-bottom: 10px;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .tags-row {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 10px;
  }
  
  .tag-chip {
    padding: 3px 10px;
    background: var(--c-primary-bg);
    color: var(--c-primary);
    border-radius: 50px;
    font-size: 11px;
    font-weight: 600;
  }
  
  .tag-more {
    padding: 3px 8px;
    background: var(--c-bg);
    color: var(--c-text3);
    border-radius: 50px;
    font-size: 11px;
    font-weight: 600;
  }
  
  .resume-note {
    font-size: 13px;
    color: var(--c-text3);
    line-height: 1.5;
    margin-bottom: 10px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .meta-row {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: var(--c-text3);
  }
  
  /* Card Footer */
  .card-footer {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-top: 1px solid var(--c-border-light);
    gap: 6px;
  }
  
  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    padding: 8px 12px;
    border: none;
    border-radius: var(--radius-sm);
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: var(--transition);
    background: var(--c-bg);
    color: var(--c-text2);
    font-family: inherit;
  }
  
  .action-btn:hover {
    background: var(--c-border-light);
  }
  
  .action-btn.primary {
    background: var(--c-primary-bg);
    color: var(--c-primary);
    flex: 1;
  }
  
  .action-btn.primary:hover {
    background: var(--c-primary);
    color: white;
    box-shadow: var(--shadow-primary);
  }
  
  .action-btn.star {
    color: var(--c-text3);
  }
  
  .action-btn.star.is-active {
    color: var(--c-primary);
    background: var(--c-primary-bg);
    cursor: default;
  }
  
  .action-btn.star:not(.is-active):hover {
    color: #f59e0b;
    background: #fffbeb;
  }
  
  .action-btn.danger:hover {
    background: var(--c-danger-bg);
    color: var(--c-danger);
  }
  
  /* =============================================
     Empty State
     ============================================= */
  .empty-state {
    text-align: center;
    padding: 80px 20px;
    background: var(--c-white);
    border-radius: var(--radius-xl);
    border: 1px solid var(--c-border-light);
  }
  
  .empty-illustration {
    position: relative;
    width: 120px;
    height: 120px;
    margin: 0 auto 28px;
  }
  
  .empty-doc {
    position: absolute;
    top: 10px;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 76px;
    background: white;
    border: 2px solid var(--c-border);
    border-radius: 6px;
    padding: 12px 8px;
  }
  
  .empty-doc-line {
    height: 4px;
    border-radius: 2px;
    background: var(--c-border);
    margin-bottom: 5px;
  }
  
  .empty-doc-line.l1 { width: 50%; background: var(--c-primary); opacity: 0.5; }
  .empty-doc-line.l2 { width: 100%; }
  .empty-doc-line.l3 { width: 80%; }
  .empty-doc-line.l4 { width: 60%; }
  
  .empty-circle {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 100px;
    height: 100px;
    background: var(--c-primary-bg);
    border-radius: 50%;
    z-index: -1;
  }
  
  .empty-plus {
    position: absolute;
    bottom: 5px;
    right: 8px;
    animation: pulse 2s infinite;
  }
  
  @keyframes pulse {
    0%, 100% { transform: scale(1); opacity: 1; }
    50% { transform: scale(1.15); opacity: 0.7; }
  }
  
  .empty-title {
    font-size: 22px;
    font-weight: 700;
    color: var(--c-text);
    margin-bottom: 8px;
  }
  
  .empty-desc {
    font-size: 15px;
    color: var(--c-text3);
    margin-bottom: 28px;
  }
  
  .empty-upload-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 32px;
    background: linear-gradient(135deg, var(--c-primary), var(--c-primary-light));
    color: white;
    border: none;
    border-radius: var(--radius-md);
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    transition: var(--transition);
    box-shadow: var(--shadow-primary);
    font-family: inherit;
  }
  
  .empty-upload-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 28px rgba(255,107,0,0.35);
  }
  
  .empty-clear-btn {
    padding: 10px 24px;
    background: var(--c-bg);
    color: var(--c-text2);
    border: 1px solid var(--c-border);
    border-radius: var(--radius-md);
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: var(--transition);
    font-family: inherit;
  }
  
  .empty-clear-btn:hover { border-color: var(--c-primary); color: var(--c-primary); }
  
  /* =============================================
     Modal
     ============================================= */
  .modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.5);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    backdrop-filter: blur(4px);
  }
  
  .preview-overlay { background: rgba(0,0,0,0.7); }
  
  .modal-container {
    background: var(--c-white);
    border-radius: var(--radius-xl);
    width: 100%;
    max-width: 560px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: var(--shadow-lg);
  }
  
  .modal-container.modal-sm { max-width: 440px; }
  
  .modal-container.modal-preview {
    max-width: 860px;
    max-height: 92vh;
    display: flex;
    flex-direction: column;
  }
  
  /* Modal transition */
  .modal-enter-active, .modal-leave-active { transition: all 0.3s ease; }
  .modal-enter, .modal-leave-to { opacity: 0; }
  .modal-enter .modal-container, .modal-leave-to .modal-container {
    transform: translateY(24px) scale(0.97);
  }
  
  .modal-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 22px 26px 18px;
    border-bottom: 1px solid var(--c-border-light);
  }
  
  .modal-header h3 {
    font-size: 19px;
    font-weight: 700;
    color: var(--c-text);
    flex: 1;
  }
  
  .modal-header-icon {
    width: 40px;
    height: 40px;
    border-radius: var(--radius-sm);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .modal-header-icon.orange { background: var(--c-primary-bg); color: var(--c-primary); }
  .modal-header-icon.blue { background: var(--c-blue-bg); color: var(--c-blue); }
  .modal-header-icon.red { background: var(--c-danger-bg); color: var(--c-danger); }
  
  .modal-close-btn {
    width: 36px;
    height: 36px;
    border: none;
    background: var(--c-bg);
    border-radius: var(--radius-sm);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: var(--transition);
  }
  
  .modal-close-btn:hover { background: var(--c-danger-bg); }
  .modal-close-btn:hover svg path { fill: var(--c-danger); }
  
  .modal-body {
    padding: 24px 26px;
  }
  
  .modal-footer {
    padding: 16px 26px 22px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    border-top: 1px solid var(--c-border-light);
  }
  
  /* Modal Buttons */
  .modal-btn {
    padding: 11px 26px;
    border: none;
    border-radius: var(--radius-sm);
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    transition: var(--transition);
    display: flex;
    align-items: center;
    gap: 6px;
    font-family: inherit;
  }
  
  .modal-btn.cancel {
    background: var(--c-bg);
    color: var(--c-text2);
  }
  
  .modal-btn.cancel:hover { background: var(--c-border); }
  
  .modal-btn.confirm {
    background: linear-gradient(135deg, var(--c-primary), var(--c-primary-light));
    color: white;
    box-shadow: var(--shadow-primary);
  }
  
  .modal-btn.confirm:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 24px rgba(255,107,0,0.35);
  }
  
  .modal-btn.confirm:disabled {
    opacity: 0.6;
    cursor: wait;
  }
  
  .modal-btn.danger {
    background: linear-gradient(135deg, #dc2626, #ef4444);
    color: white;
    box-shadow: 0 4px 16px rgba(220,38,38,0.25);
  }
  
  .modal-btn.danger:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 24px rgba(220,38,38,0.35);
  }
  
  /* =============================================
     Form Styles
     ============================================= */
  .form-section {
    margin-bottom: 22px;
  }
  
  .form-label {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 700;
    color: var(--c-text);
    margin-bottom: 10px;
  }
  
  .required { color: var(--c-danger); }
  
  .form-input {
    width: 100%;
    padding: 12px 16px;
    border: 1.5px solid var(--c-border);
    border-radius: var(--radius-sm);
    font-size: 14px;
    color: var(--c-text);
    outline: none;
    transition: var(--transition);
    font-family: inherit;
  }
  
  .form-input:focus {
    border-color: var(--c-primary);
    box-shadow: 0 0 0 3px rgba(255,107,0,0.1);
  }
  
  .form-textarea {
    width: 100%;
    padding: 12px 16px;
    border: 1.5px solid var(--c-border);
    border-radius: var(--radius-sm);
    font-size: 14px;
    color: var(--c-text);
    outline: none;
    transition: var(--transition);
    resize: vertical;
    min-height: 80px;
    font-family: inherit;
  }
  
  .form-textarea:focus {
    border-color: var(--c-primary);
    box-shadow: 0 0 0 3px rgba(255,107,0,0.1);
  }
  
  .form-error {
    display: block;
    font-size: 12px;
    color: var(--c-danger);
    margin-top: 6px;
  }
  
  .char-count {
    text-align: right;
    font-size: 12px;
    color: var(--c-text3);
    margin-top: 4px;
  }
  
  /* Tags Selector */
  .tags-selector {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .tag-option {
    padding: 6px 16px;
    border: 1.5px solid var(--c-border);
    border-radius: 50px;
    font-size: 13px;
    font-weight: 600;
    color: var(--c-text2);
    background: var(--c-white);
    cursor: pointer;
    transition: var(--transition);
    font-family: inherit;
  }
  
  .tag-option:hover {
    border-color: var(--c-primary);
    color: var(--c-primary);
    background: var(--c-primary-bg);
  }
  
  .tag-option.selected {
    border-color: var(--c-primary);
    background: var(--c-primary);
    color: white;
  }
  
  /* =============================================
     Drop Zone
     ============================================= */
  .hidden-input { display: none; }
  
  .drop-zone {
    border: 2px dashed var(--c-border);
    border-radius: var(--radius-md);
    transition: var(--transition);
    cursor: pointer;
    background: var(--c-bg);
    overflow: hidden;
  }
  
  .drop-zone:hover, .drop-zone.drag-over {
    border-color: var(--c-primary);
    background: var(--c-primary-bg);
  }
  
  .drop-zone.has-file {
    border-style: solid;
    border-color: var(--c-primary);
    background: var(--c-primary-bg);
    cursor: default;
  }
  
  .drop-zone-content {
    padding: 32px 20px;
    text-align: center;
  }
  
  .drop-zone-icon { margin-bottom: 8px; }
  
  .drop-zone:hover .drop-zone-icon svg path,
  .drop-zone.drag-over .drop-zone-icon svg path {
    fill: var(--c-primary);
  }
  
  .drop-zone-text {
    font-size: 14px;
    color: var(--c-text2);
    margin-bottom: 4px;
  }
  
  .drop-zone-text em {
    color: var(--c-primary);
    font-weight: 700;
    font-style: normal;
  }
  
  .drop-zone-hint {
    font-size: 12px;
    color: var(--c-text3);
  }
  
  .format-tags {
    display: flex;
    justify-content: center;
    gap: 6px;
    margin-top: 14px;
  }
  
  .fmt-tag {
    padding: 2px 10px;
    background: var(--c-white);
    border: 1px solid var(--c-border);
    border-radius: 50px;
    font-size: 11px;
    font-weight: 700;
    color: var(--c-text3);
  }
  
  /* Selected File Card */
  .selected-file-card {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 16px 20px;
  }
  
  .sf-icon {
    width: 44px;
    height: 44px;
    border-radius: var(--radius-sm);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 0.5px;
    flex-shrink: 0;
  }
  
  .sf-icon.type-pdf { background: #fef2f2; color: #dc2626; }
  .sf-icon.type-word { background: #eff6ff; color: #2563eb; }
  .sf-icon.type-image { background: #f0fdf4; color: #16a34a; }
  .sf-icon.type-other { background: var(--c-bg); color: var(--c-text3); }
  .sf-icon.type-current { background: var(--c-primary); color: white; }
  
  .sf-info { flex: 1; min-width: 0; }
  .sf-name { font-size: 14px; font-weight: 600; color: var(--c-text); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .sf-size { font-size: 12px; color: var(--c-text3); margin-top: 2px; }
  
  .sf-remove {
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: var(--transition);
  }
  
  .sf-remove:hover { background: var(--c-danger-bg); }
  .sf-remove:hover svg path { fill: var(--c-danger); }
  
  /* Current File (Edit Mode) */
  .current-file-display {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 18px;
    background: var(--c-bg);
    border-radius: var(--radius-sm);
    border: 1px solid var(--c-border-light);
  }
  
  .current-file-name {
    flex: 1;
    font-size: 14px;
    color: var(--c-text);
    font-weight: 500;
  }
  
  .change-file-btn {
    padding: 6px 14px;
    border: 1px solid var(--c-primary);
    border-radius: var(--radius-sm);
    background: transparent;
    color: var(--c-primary);
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: var(--transition);
    font-family: inherit;
  }
  
  .change-file-btn:hover {
    background: var(--c-primary);
    color: white;
  }
  
  /* =============================================
     Delete Confirm
     ============================================= */
  .delete-warning-box {
    display: flex;
    gap: 14px;
    padding: 18px;
    background: var(--c-danger-bg);
    border-radius: var(--radius-md);
    border: 1px solid #fecaca;
    margin-bottom: 18px;
  }
  
  .warning-icon-circle {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    background: #fee2e2;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  
  .delete-warning-box h4 {
    font-size: 15px;
    font-weight: 700;
    color: #991b1b;
    margin-bottom: 4px;
  }
  
  .delete-warning-box p {
    font-size: 13px;
    color: #b91c1c;
    line-height: 1.5;
  }
  
  .delete-file-card {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 18px;
    background: var(--c-bg);
    border-radius: var(--radius-sm);
    border: 1px solid var(--c-border-light);
  }
  
  /* =============================================
     Preview
     ============================================= */
  .preview-body {
    flex: 1;
    overflow: auto;
    background: #f0f0f0;
  }
  
  .preview-frame-wrap {
    width: 100%;
    height: 70vh;
  }
  
  .preview-frame-wrap iframe {
    width: 100%;
    height: 100%;
  }
  
  .preview-image-wrap {
    display: flex;
    justify-content: center;
    padding: 24px;
  }
  
  .preview-image-wrap img {
    max-width: 100%;
    max-height: 70vh;
    border-radius: 4px;
    box-shadow: 0 2px 16px rgba(0,0,0,0.1);
  }
  
  .preview-unsupported {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
  }
  
  .preview-unsupported h3 {
    font-size: 18px;
    color: var(--c-text2);
    margin: 16px 0 6px;
  }
  
  .preview-unsupported p {
    font-size: 14px;
    color: var(--c-text3);
    margin-bottom: 24px;
  }
  
  .preview-toolbar {
    padding: 14px 26px;
    border-top: 1px solid var(--c-border-light);
    display: flex;
    justify-content: flex-end;
  }
  
  .toolbar-action {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 22px;
    background: linear-gradient(135deg, var(--c-primary), var(--c-primary-light));
    color: white;
    border: none;
    border-radius: var(--radius-sm);
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    transition: var(--transition);
    box-shadow: var(--shadow-primary);
    font-family: inherit;
  }
  
  .toolbar-action:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 24px rgba(255,107,0,0.35);
  }
  
  /* =============================================
     Toast
     ============================================= */
  .toast-container {
    position: fixed;
    top: 20px;
    right: 24px;
    z-index: 2000;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .toast-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 14px 20px;
    background: var(--c-white);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-lg);
    font-size: 14px;
    font-weight: 500;
    min-width: 260px;
    border-left: 4px solid;
  }
  
  .toast-item.success { border-color: var(--c-success); color: #166534; }
  .toast-item.error { border-color: var(--c-danger); color: #991b1b; }
  .toast-item.info { border-color: var(--c-primary); color: var(--c-primary-dark); }
  
  .toast-icon-wrap {
    width: 26px;
    height: 26px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  
  .toast-item.success .toast-icon-wrap { background: var(--c-success-bg); color: var(--c-success); }
  .toast-item.error .toast-icon-wrap { background: var(--c-danger-bg); color: var(--c-danger); }
  .toast-item.info .toast-icon-wrap { background: var(--c-primary-bg); color: var(--c-primary); }
  
  /* Toast transition */
  .toast-enter-active { transition: all 0.4s ease; }
  .toast-leave-active { transition: all 0.3s ease; }
  .toast-enter { opacity: 0; transform: translateX(60px); }
  .toast-leave-to { opacity: 0; transform: translateX(60px); }
  
  /* Spin animation */
  @keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
  
  .spin { animation: spin 1s linear infinite; }
  
  /* =============================================
     Responsive
     ============================================= */
  @media (max-width: 1024px) {
    .resume-grid { grid-template-columns: repeat(2, 1fr); }
  }
  
  @media (max-width: 768px) {
    .header-inner {
      flex-direction: column;
      align-items: flex-start;
      gap: 20px;
      padding: 28px 20px;
    }
  
    .header-right {
      width: 100%;
      justify-content: space-between;
    }
  
    .toolbar-inner {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;
      padding: 12px 20px;
    }
  
    .toolbar-left { flex-direction: column; }
    .search-box { width: 100%; }
    .toolbar-right { justify-content: space-between; }
  
    .content-inner { padding: 0 16px; }
  
    .resume-grid { grid-template-columns: 1fr; gap: 16px; }
    .resume-grid.list-view .resume-card {
      grid-template-columns: 1fr;
    }
    .resume-grid.list-view .card-preview-area {
      border-radius: var(--radius-lg) var(--radius-lg) 0 0;
    }
    .resume-grid.list-view .card-footer {
      border-left: none;
      border-top: 1px solid var(--c-border-light);
      flex-direction: row;
    }
  
    .modal-container { margin: 0 8px; }
  
    .page-title { font-size: 24px; }
  }
  
  @media (max-width: 480px) {
    .page-title { font-size: 20px; }
    .page-subtitle { font-size: 13px; }
  
    .quota-card { padding: 8px 14px; }
    .quota-bar { display: none; }
  
    .upload-main-btn {
      padding: 10px 18px;
      font-size: 14px;
    }
  
    .upload-main-btn span { display: none; }
  
    .card-footer {
      flex-wrap: wrap;
    }
  
    .action-btn {
      padding: 8px 10px;
      font-size: 12px;
    }
  
    .action-btn span { display: none; }
  }
  </style>