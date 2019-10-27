import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import locale from 'element-ui/lib/locale/lang/en'
import '../style/theme/index.css'

Vue.use(ElementUI, { locale })

new Vue({
  el: '#app',
  render: h => h(App)
});