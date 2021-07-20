<template>
  <div class="app-page flex-box" style="flex-wrap: wrap">
    <el-card v-for="item in items"
             style="width: 200px; margin-bottom:12px; text-align: center;">
      <div slot="header">
        <span>{{ item.title }}</span>
      </div>
      <div>{{ item.content }}</div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'dashboard',
  data() {
    return {
      items: [
        { title: '总注册用户数', content: 54 },
        { title: '注册用户数（今日）', content: 12 },
        { title: '注册用户数（本周）', content: 43 },
        { title: '注册用户数（本月）', content: 654 },
        { title: '总角色数', content: 54 },
        { title: '角色数（今日）', content: 12 },
        { title: '角色数（本周）', content: 43 },
        { title: '角色数（本月）', content: 654 }
      ]
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'roles'
    ])
  }
}
</script>

<style lang="scss" rel="stylesheet/scss" scoped>
.dashboard {

&
-container {
  margin: 30px;
}

&
-text {
  font-size: 30px;
  line-height: 46px;
}

}
</style>
