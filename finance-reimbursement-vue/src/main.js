import Vue from 'vue'
import App from './App.vue'
import router from './router'

Vue.config.productionTip = false

// Keep feedback inside the application instead of using browser-native dialogs.
window.alert = (message) => window.dispatchEvent(new CustomEvent('app-notice', {
  detail: { message: String(message || '操作失败') }
}))

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
