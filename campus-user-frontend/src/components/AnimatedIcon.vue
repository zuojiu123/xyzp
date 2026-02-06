<template>
  <div class="animated-icon" :class="animationClass">
    <i :class="iconClass" :style="iconStyle"></i>
  </div>
</template>

<script>
export default {
  name: 'AnimatedIcon',
  props: {
    icon: {
      type: String,
      default: 'el-icon-star-on'
    },
    animation: {
      type: String,
      default: 'bounce'
    },
    color: {
      type: String,
      default: 'var(--primary-color)'
    },
    size: {
      type: String,
      default: '24px'
    }
  },
  computed: {
    iconClass() {
      return this.icon
    },
    animationClass() {
      return `icon-${this.animation}`
    },
    iconStyle() {
      return {
        color: this.color,
        fontSize: this.size
      }
    }
  }
}
</script>

<style scoped>
.animated-icon {
  display: inline-block;
  transition: all 0.3s ease;
}

.icon-bounce:hover {
  animation: bounce 0.6s ease;
}

.icon-pulse {
  animation: pulse 2s infinite;
}

.icon-float {
  animation: float 3s ease-in-out infinite;
}

@keyframes bounce {
  0%, 20%, 60%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  80% {
    transform: translateY(-10px);
  }
}
</style>