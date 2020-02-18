import Vue from 'vue'
import App from './App'
import Index from './Index'
import Test from './Test'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)
Vue.config.productionTip = false

new Vue({
  render: h => h(Index),
}).$mount('#app')
