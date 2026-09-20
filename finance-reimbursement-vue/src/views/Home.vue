<template>
  <div class="home" :class="{ 'dark-mode': darkMode }">

    <div class="sidebar">

      <div class="sidebar-top">

        <div class="system-title">
          FRS
        </div>

        <div class="system-name">
          财务管理系统
        </div>

      </div>

      <div class="menu">

        <div
            class="menu-item"
            :class="{ active: $route.path === '/home' }"
            @click="$router.push('/home')"
        >
          <span>系统首页</span>
        </div>


        <div
            class="menu-item"
            :class="{ active: $route.path === '/home/reimbursement' }"
            @click="$router.push('/home/reimbursement')"
        >
    <span>
      {{ loginUser.role === 'EMPLOYEE' ? '我的报销' : '报销管理' }}
    </span>
        </div>
        <div
            v-if="loginUser.role === 'ADMIN' || loginUser.role === 'FINANCE'"
            class="menu-item"
            :class="{ active: $route.path === '/home/audit' }"
            @click="$router.push('/home/audit')"
        >
          <span>审核管理</span>
        </div>
        <div
            v-if="loginUser.role === 'ADMIN'"
            class="menu-item"
            :class="{ active: $route.path === '/home/budget' }"
            @click="$router.push('/home/budget')"
        >
          <span>预算管理</span>
        </div>
        <div
            v-if="loginUser.role === 'ADMIN' || loginUser.role === 'FINANCE'"
            class="menu-item"
            :class="{ active: $route.path === '/home/warning' }"
            @click="$router.push('/home/warning')"
        >
          <span>预算预警</span>
        </div>
        <div
            v-if="loginUser.role === 'ADMIN'"
            class="menu-item"
            :class="{ active: $route.path === '/home/user' }"
            @click="$router.push('/home/user')"
        >
          <span>用户管理</span>
        </div>
      </div>

      <div class="sidebar-bottom">

        <div
            class="settings-button"
            @click="toggleSettings"
        >
          <span class="settings-icon">⚙</span>
          <span>设置</span>
        </div>

      </div>

    </div>

    <div class="main">

      <div class="topbar">

        <div>
          <h2>{{ greeting() }}</h2>
          <p>{{ todayText() }} · 今日专注，高效完成每一笔事务</p>
        </div>

        <div class="user-info">

          <div class="user-name">
            {{ loginUser.realName || loginUser.username }}
          </div>

          <div class="user-role">
            {{ getRoleText(loginUser.role) }}
          </div>

        </div>

      </div>

      <div class="content">
        <router-view></router-view>
      </div>

    </div>

    <div
        v-if="showSettings"
        class="settings-mask"
        @click.self="closeSettings"
    >

      <div class="settings-panel">

        <div class="settings-header">

          <div>
            <h3>设置</h3>
            <p>调整系统显示方式</p>
          </div>

          <button
              class="close-button"
              @click="closeSettings"
          >
            ×
          </button>

        </div>

        <div class="setting-section">

          <div class="section-title">
            外观
          </div>

          <div class="theme-options">

            <div
                class="theme-option"
                :class="{ selected: !darkMode }"
                @click="setTheme(false)"
            >

              <div class="theme-preview light-preview">
                <div class="preview-sidebar"></div>
                <div class="preview-content"></div>
              </div>

              <div class="theme-name">
                浅色模式
              </div>

              <div
                  v-if="!darkMode"
                  class="selected-text"
              >
                当前使用
              </div>

            </div>

            <div
                class="theme-option"
                :class="{ selected: darkMode }"
                @click="setTheme(true)"
            >

              <div class="theme-preview dark-preview">
                <div class="preview-sidebar"></div>
                <div class="preview-content"></div>
              </div>

              <div class="theme-name">
                深色模式
              </div>

              <div
                  v-if="darkMode"
                  class="selected-text"
              >
                当前使用
              </div>

            </div>

          </div>

        </div>

        <div class="setting-section account-section">

          <div class="section-title">
            当前账号
          </div>

          <div class="account-info">

            <div class="account-avatar">
              {{ getAvatarText() }}
            </div>

            <div class="account-detail">

              <div class="account-name">
                {{ loginUser.realName || loginUser.username }}
              </div>

              <div class="account-role">
                {{ getRoleText(loginUser.role) }}
              </div>

            </div>

          </div>

        </div>

        <button
            class="logout-button"
            @click="logout"
        >
          退出登录
        </button>

      </div>

    </div>

  </div>
</template>

<script>
export default {
  name: 'Home',

  data() {
    return {
      loginUser: {},
      showSettings: false,
      darkMode: false
    }
  },

  created() {
    this.getLoginUser()
    this.loadTheme()
  },
  mounted() {

    window.addEventListener(
        'storage',
        this.getLoginUser
    )

  },
  beforeDestroy() {

    window.removeEventListener(
        'storage',
        this.getLoginUser
    )

  },
  methods: {

    todayText() {
      const date = new Date()
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
    },

    greeting() {
      const hour = new Date().getHours()
      if (hour < 12) return '早上好，开始今天的工作吧'
      if (hour < 18) return '下午好，工作进展顺利'
      return '晚上好，辛苦了'
    },

    getLoginUser() {

      const user = localStorage.getItem('loginUser')

      if (user) {

        try {
          this.loginUser = JSON.parse(user)
        } catch (error) {
          console.error('读取登录信息失败', error)
        }

      }

    },

    getRoleText(role) {

      if (role === 'ADMIN') {
        return '管理员'
      }

      if (role === 'FINANCE') {
        return '财务人员'
      }

      if (role === 'EMPLOYEE') {
        return '普通员工'
      }

      return '用户'

    },

    getAvatarText() {

      if (this.loginUser.realName) {
        return this.loginUser.realName.substring(0, 1)
      }

      if (this.loginUser.username) {
        return this.loginUser.username
            .substring(0, 1)
            .toUpperCase()
      }

      return 'U'

    },

    getPageTitle() {

      if (this.$route.path === '/home') {
        return '系统首页'
      }

      if (this.$route.path === '/home/reimbursement') {
        if (this.loginUser.role === 'EMPLOYEE') {
          return '我的报销'
        }
        return '报销管理'
      }

      if (this.$route.path === '/home/audit') {
        return '审核管理'
      }

      if (this.$route.path === '/home/budget') {
        return '预算管理'
      }

      if (this.$route.path === '/home/warning') {
        return '预算预警'
      }

      if (this.$route.path === '/home/user') {
        return '用户管理'
      }

      return '系统首页'

    },

    toggleSettings() {
      this.showSettings = !this.showSettings
    },

    closeSettings() {
      this.showSettings = false
    },

    setTheme(isDark) {

      this.darkMode = isDark

      localStorage.setItem(
          'theme',
          isDark ? 'dark' : 'light'
      )

    },

    loadTheme() {

      const theme = localStorage.getItem('theme')

      if (theme === 'dark') {
        this.darkMode = true
      } else {
        this.darkMode = false
      }

    },

    logout() {
      localStorage.removeItem('loginUser')

      this.showSettings = false

      this.$router.push('/')

    }

  }
}
</script>

<style scoped>

.home {
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: #f5f7fa;
  transition: background 0.25s ease;
}

.sidebar {
  width: 220px;
  height: 100vh;
  background: #17375e;
  color: white;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  position: relative;
  z-index: 2000;
  opacity: 1;
  isolation: isolate;
}

.sidebar-top {
  flex-shrink: 0;
}

.system-title {
  padding-top: 28px;
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  letter-spacing: 1px;
}

.system-name {
  padding: 8px 0 30px;
  text-align: center;
  color: #b9cbe0;
  font-size: 13px;
}

.menu {
  flex: 1;
  overflow-y: auto;
  padding-top: 2px;
}

.menu::-webkit-scrollbar {
  width: 4px;
}

.menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.18);
  border-radius: 4px;
}

.menu-item {
  height: 50px;
  display: flex;
  align-items: center;
  padding-left: 35px;
  color: #d8e4f0;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.menu-item:hover {
  background: #285684;
}

.menu-item.active {
  background: #397bc1;
  color: white;
}

.sidebar-bottom {
  flex-shrink: 0;
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: #17375e;
}

.settings-button {
  height: 44px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: 23px;
  border-radius: 7px;
  color: #d8e4f0;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.settings-button:hover {
  background: #285684;
  color: white;
}

.settings-icon {
  font-size: 17px;
}

.main {
  flex: 1;
  min-width: 0;
  height: 100vh;
  overflow: hidden;
  background: #f5f7fa;
  transition: background 0.25s ease;
}

.topbar {
  height: 75px;
  padding: 0 35px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 50;
  transition: background 0.25s ease;
}

.topbar h2 {
  margin: 0;
  color: #25364d;
  font-size: 21px;
}

.topbar p {
  margin: 6px 0 0;
  color: #9099a8;
  font-size: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #526277;
  font-size: 13px;
}

.user-name {
  font-weight: 600;
  color: #25364d;
}

.user-role {
  padding: 5px 10px;
  border-radius: 14px;
  background: #eef5ff;
  color: #397bc1;
  font-size: 12px;
}

.content {
  height: calc(100vh - 75px);
  padding: 30px;
  overflow-y: auto;

  scrollbar-width: thin;
  scrollbar-color: #d6dbe3 transparent;
}


.content::-webkit-scrollbar {
  width: 6px;
}


.content::-webkit-scrollbar-track {
  background: transparent;
}


.content::-webkit-scrollbar-thumb {
  background: #d6dbe3;
  border-radius: 10px;
}


.content::-webkit-scrollbar-thumb:hover {
  background: #b8c0cc;
}

.settings-mask {
  position: fixed;
  left: 220px;
  top: 0;
  right: 0;
  bottom: 0;
  z-index: 1500;
  background: rgba(15, 23, 42, 0.22);
  pointer-events: auto;
}

.settings-panel {
  position: absolute;
  left: 12px;
  bottom: 18px;
  width: 360px;
  padding: 22px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.18);
  animation: settings-show 0.18s ease;
}

@keyframes settings-show {

  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }

}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.settings-header h3 {
  margin: 0;
  color: #25364d;
  font-size: 18px;
}

.settings-header p {
  margin: 6px 0 0;
  color: #9099a8;
  font-size: 12px;
}

.close-button {
  width: 30px;
  height: 30px;
  border: none;
  background: transparent;
  color: #9aa4b2;
  font-size: 24px;
  line-height: 30px;
  cursor: pointer;
  border-radius: 6px;
}

.close-button:hover {
  background: #f1f3f5;
  color: #526277;
}

.setting-section {
  margin-top: 24px;
}

.section-title {
  margin-bottom: 12px;
  color: #526277;
  font-size: 13px;
  font-weight: 600;
}

.theme-options {
  display: flex;
  gap: 12px;
}

.theme-option {
  flex: 1;
  padding: 10px;
  border: 1px solid #e1e6ed;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.theme-option:hover {
  border-color: #9bbce7;
}

.theme-option.selected {
  border-color: #397bc1;
  background: #f5f9ff;
}

.theme-preview {
  height: 72px;
  display: flex;
  overflow: hidden;
  border-radius: 5px;
  border: 1px solid #e1e6ed;
  margin-bottom: 10px;
}

.preview-sidebar {
  width: 27%;
}

.preview-content {
  flex: 1;
}

.light-preview {
  background: #f5f7fa;
}

.light-preview .preview-sidebar {
  background: #17375e;
}

.light-preview .preview-content {
  background: #ffffff;
}

.dark-preview {
  background: #1f2937;
}

.dark-preview .preview-sidebar {
  background: #111827;
}

.dark-preview .preview-content {
  background: #374151;
}

.theme-name {
  color: #374151;
  font-size: 13px;
}

.selected-text {
  margin-top: 4px;
  color: #397bc1;
  font-size: 11px;
}

.account-section {
  padding-top: 18px;
  border-top: 1px solid #edf0f4;
}

.account-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.account-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #397bc1;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
}

.account-name {
  color: #25364d;
  font-size: 14px;
  font-weight: 600;
}

.account-role {
  margin-top: 4px;
  color: #9099a8;
  font-size: 12px;
}

.logout-button {
  width: 100%;
  height: 42px;
  margin-top: 22px;
  border: 1px solid #f0caca;
  border-radius: 7px;
  background: #fff7f7;
  color: #d9534f;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s;
}

.logout-button:hover {
  background: #fff0f0;
  border-color: #e6aaaa;
}

.dark-mode .main {
  background: #111827;
}

.dark-mode .topbar {
  background: #1f2937;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.2);
}

.dark-mode .topbar h2 {
  color: #f3f4f6;
}

.dark-mode .topbar p {
  color: #9ca3af;
}

.dark-mode .user-name {
  color: #f3f4f6;
}

.dark-mode .user-role {
  background: #263b56;
  color: #93c5fd;
}

.dark-mode .settings-panel {
  background: #1f2937;
}

.dark-mode .settings-header h3 {
  color: #f3f4f6;
}

.dark-mode .settings-header p {
  color: #9ca3af;
}

.dark-mode .section-title {
  color: #cbd5e1;
}

.dark-mode .theme-option {
  border-color: #374151;
  background: #1f2937;
}

.dark-mode .theme-option.selected {
  border-color: #5798ef;
  background: #263b56;
}

.dark-mode .theme-name {
  color: #e5e7eb;
}

.dark-mode .account-section {
  border-color: #374151;
}

.dark-mode .account-name {
  color: #f3f4f6;
}

.dark-mode .account-role {
  color: #9ca3af;
}

.dark-mode .close-button {
  color: #9ca3af;
}

.dark-mode .close-button:hover {
  background: #374151;
  color: #f3f4f6;
}

.dark-mode .logout-button {
  background: #352020;
  border-color: #5b3030;
  color: #fca5a5;
}

.dark-mode .logout-button:hover {
  background: #432525;
}

</style>
