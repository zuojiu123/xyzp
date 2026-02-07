<template>
  <div class="about-container">
    <div class="section-banner">
      <div class="banner-content">
        <h1>关于我们</h1>
        <p>了解校园招聘系统的使命与愿景</p>
      </div>
    </div>

    <div class="main-content">
      <!-- 我们的使命 -->
      <section class="mission-section">
        <div class="container">
          <div class="section-header">
            <h2>我们的使命</h2>
            <div class="divider"></div>
          </div>
          <div class="mission-content">
            <p>校园招聘系统致力于搭建连接高校毕业生与优质企业的桥梁，为求职者提供便捷、高效的求职平台，为企业提供精准、专业的人才招聘解决方案。</p>
            <p>我们相信，每一位毕业生都应该有机会找到适合自己的职业发展道路，每一家企业都应该能够招聘到最匹配的人才。通过技术创新和优质服务，我们旨在简化招聘流程，提高匹配效率，让求职和招聘变得更加简单、透明。</p>
          </div>
        </div>
      </section>

      <!-- 我们的愿景 -->
      <section class="vision-section bg-light">
        <div class="container">
          <div class="section-header">
            <h2>我们的愿景</h2>
            <div class="divider"></div>
          </div>
          <div class="vision-content">
            <p>成为全国领先的校园招聘服务平台，构建一个更加公平、高效、智能的人才生态系统。</p>
            <p>我们将持续创新，不断提升用户体验，拓展服务领域，为高校、企业和求职者创造更大价值，助力中国人力资源市场的健康发展。</p>
          </div>
        </div>
      </section>

      <!-- 核心价值观 -->
      <section class="values-section">
        <div class="container">
          <div class="section-header">
            <h2>核心价值观</h2>
            <div class="divider"></div>
          </div>
          <div class="values-grid">
            <div class="value-item">
              <div class="value-icon">
                <i class="el-icon-s-flag"></i>
              </div>
              <h3>专业</h3>
              <p>以专业的态度和方法，提供高质量的服务和解决方案</p>
            </div>
            <div class="value-item">
              <div class="value-icon">
                <i class="el-icon-s-heart"></i>
              </div>
              <h3>诚信</h3>
              <p>诚实守信，言行一致，建立信任关系</p>
            </div>
            <div class="value-item">
              <div class="value-icon">
                <i class="el-icon-s-marketing"></i>
              </div>
              <h3>创新</h3>
              <p>不断探索新方法、新技术，推动行业发展</p>
            </div>
            <div class="value-item">
              <div class="value-icon">
                <i class="el-icon-s-help"></i>
              </div>
              <h3>服务</h3>
              <p>以用户为中心，提供优质、贴心的服务</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 发展历程 -->
      <section class="history-section bg-light">
        <div class="container">
          <div class="section-header">
            <h2>发展历程</h2>
            <div class="divider"></div>
          </div>
          <div class="timeline">
            <div class="timeline-item">
              <div class="timeline-content">
                <h3>2024年</h3>
                <p>校园招聘系统正式上线，开始为高校毕业生和企业提供服务</p>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-content">
                <h3>2025年</h3>
                <p>系统功能全面升级，增加了智能匹配、在线沟通等功能，用户规模持续增长</p>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-content">
                <h3>未来</h3>
                <p>持续创新，拓展服务领域，成为全国领先的校园招聘服务平台</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: 'About',
  data() {
    return {
    }
  },
  mounted() {
    // 设置页面标题
    document.title = '关于我们 - 校园招聘系统'
  }
}
</script>

<style scoped>
.about-container {
  min-height: 100vh;
  background-color: #f8fafc;
}

/* 横幅区域 */
.section-banner {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
}

.banner-content h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

/* 主内容区域 */
.main-content {
  padding: 60px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 通用区块样式 */
.mission-section,
.vision-section,
.values-section,
.history-section {
  padding: 60px 0;
}

.bg-light {
  background-color: white;
}

/* 区块标题 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 16px;
}

.divider {
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #ff8736 0%, #ff6b00 100%);
  margin: 0 auto;
  border-radius: 2px;
}

/* 使命与愿景内容 */
.mission-content,
.vision-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
  font-size: 16px;
  line-height: 1.8;
  color: #64748b;
}

.mission-content p,
.vision-content p {
  margin-bottom: 20px;
}

/* 核心价值观网格 */
.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.value-item {
  text-align: center;
  padding: 30px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.value-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.value-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #ff8736 0%, #ff6b00 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 32px;
}

.value-item h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
}

.value-item p {
  font-size: 14px;
  line-height: 1.6;
  color: #64748b;
  margin: 0;
}

/* 时间线 */
.timeline {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
  transform: translateX(-50%);
}

.timeline-item {
  position: relative;
  margin-bottom: 40px;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 20px;
  width: 16px;
  height: 16px;
  background: linear-gradient(135deg, #ff8736 0%, #ff6b00 100%);
  border-radius: 50%;
  transform: translateX(-50%);
  box-shadow: 0 0 0 4px rgba(255, 107, 0, 0.1);
}

.timeline-content {
  position: relative;
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  max-width: 400px;
}

.timeline-item:nth-child(odd) .timeline-content {
  margin-left: auto;
}

.timeline-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 10px;
}

.timeline-content p {
  font-size: 14px;
  line-height: 1.6;
  color: #64748b;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-content h1 {
    font-size: 32px;
  }

  .section-header h2 {
    font-size: 24px;
  }

  .timeline::before {
    left: 20px;
  }

  .timeline-item::before {
    left: 20px;
    transform: none;
  }

  .timeline-content {
    margin-left: 60px !important;
    max-width: none;
  }

  .values-grid {
    grid-template-columns: 1fr;
  }
}
</style>