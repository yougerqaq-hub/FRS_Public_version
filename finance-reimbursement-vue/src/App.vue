<template>
  <div id="app">
    <transition name="notice">
      <div v-if="notice" class="app-notice" :class="notice.type">{{ notice.message }}</div>
    </transition>

    <!-- 登录页面 -->
    <div v-if="$route.path === '/'" class="login-page">

      <!-- 左侧介绍区域 -->
      <div class="login-left">
        <div class="left-content">

          <div class="logo-box">FRS</div>

          <h1>财务报销与预算预警管理系统</h1>

          <p>
            面向中小企业的财务管理平台
          </p>

          <div class="features">
            <span>报销管理</span>
            <span>审核管理</span>
            <span>预算管理</span>
            <span>预算预警</span>
          </div>

        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="login-right">
        <div class="login-box">

          <h2>欢迎登录</h2>

          <p class="login-tip">
            请输入您的账号信息
          </p>

          <div class="form-item">
            <label>用户名</label>

            <input
                v-model="username"
                type="text"
                placeholder="请输入用户名"
                @keyup.enter="login"
            >
          </div>

          <div class="form-item">
            <label>密码</label>

            <input
                v-model="password"
                type="password"
                placeholder="请输入密码"
                @keyup.enter="login"
            >
          </div>

          <button
              class="login-btn"
              @click="login"
          >
            登录系统
          </button>

          <p v-if="loginError" class="login-error">{{ loginError }}</p>
          <p class="footer-text">
            FBS · Finance Business System
          </p>

        </div>
      </div>

    </div>

    <!-- 系统其他页面 -->
    <router-view v-else></router-view>

  </div>
</template>

<script>
export default {
  name: 'App',

  data() {
    return {
      username: '',
      password: '',
      loginError: '',
      notice: null,
      noticeTimer: null
    }
  },
  mounted() { window.addEventListener('app-notice', this.showNotice) },
  beforeDestroy() { window.removeEventListener('app-notice', this.showNotice) },

  methods: {
    showNotice(event) {
      this.notice = event.detail || { message: '操作完成' }
      clearTimeout(this.noticeTimer)
      this.noticeTimer = setTimeout(() => { this.notice = null }, 2600)
    },

    async login() {

      if (!this.username.trim() || !this.password.trim()) {
        alert('请输入用户名和密码')
        return
      }

      try {

        const response = await fetch(
            'http://localhost:8080/user/login',
            {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                username: this.username,
                password: this.password
              })
            }
        )

        const result = await response.json()

        if (result.code === 200) {

          // 保存当前登录用户信息
          localStorage.setItem(
              'loginUser',
              JSON.stringify(result.data)
          )

          console.log('登录成功')
          console.log('当前登录用户：', result.data)

          // 进入系统首页
          this.$router.push('/home')

        } else {

          this.loginError = result.message || '登录失败'

        }

      } catch (error) {

        console.error(error)
        this.loginError = '无法连接服务器，请检查后端是否启动'

      }
    }
  }
}
</script>

<style>
* {
  box-sizing: border-box;
}

html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100%;
}

body {
  font-family: "Microsoft YaHei", Arial, sans-serif;
  background: #f5f7fb;
}

.sort-header { border: 0; background: transparent; color: inherit; font: inherit; cursor: pointer; padding: 0; }
.sort-header:hover { color: #2563eb; }

/* ==================== 登录页面 ==================== */

.login-page {
  min-height: 100vh;
  display: flex;
}

/* 左侧 */

.login-left {
  width: 55%;
  min-height: 100vh;
  background: linear-gradient(135deg, #17375e, #397bc1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.left-content {
  width: 500px;
}

.logo-box {
  width: 70px;
  height: 60px;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
  margin-bottom: 28px;
}

.left-content h1 {
  margin: 0;
  font-size: 34px;
  line-height: 1.4;
  font-weight: 600;
}

.left-content p {
  margin-top: 18px;
  color: #dbe9f7;
  font-size: 15px;
}

.features {
  display: flex;
  gap: 10px;
  margin-top: 35px;
  flex-wrap: wrap;
}

.features span {
  padding: 8px 14px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 20px;
  font-size: 12px;
  color: #e6f0fa;
}

/* 右侧 */

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f9fc;
}

.login-box {
  width: 380px;
}

.login-box h2 {
  margin: 0;
  color: #25364d;
  font-size: 29px;
}

.login-tip {
  margin: 10px 0 30px;
  color: #8b98a9;
  font-size: 13px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  color: #526277;
  font-size: 13px;
}

.form-item input {
  width: 100%;
  height: 46px;
  padding: 0 13px;
  border: 1px solid #dce4ee;
  border-radius: 8px;
  outline: none;
  font-size: 13px;
  background: white;
}

.form-item input:focus {
  border-color: #4d8ce8;
  box-shadow: 0 0 0 3px rgba(77, 140, 232, 0.1);
}

.login-btn {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: 8px;
  margin-top: 8px;
  background: linear-gradient(90deg, #397ce9, #5798ef);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.login-btn:hover {
  opacity: 0.9;
}

.footer-text {
  margin-top: 35px;
  text-align: center;
  color: #a5afbd;
  font-size: 10px;
}
.login-error { color: #dc2626; font-size: 13px; margin: 12px 0 0; }
.app-notice { position: fixed; z-index: 9999; top: 50%; left: 50%; min-width: 260px; max-width: 480px; transform: translate(-50%, -50%); padding: 16px 22px; border-radius: 12px; background: #172033; color: #fff; text-align: center; box-shadow: 0 18px 46px rgba(15, 23, 42, .32); font-size: 14px; }
.notice-enter-active,.notice-leave-active { transition: opacity .2s, transform .2s; }.notice-enter,.notice-leave-to { opacity: 0; transform: translate(-50%, -42%); }
</style>
