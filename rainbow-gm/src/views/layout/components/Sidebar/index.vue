<template>
  <el-scrollbar wrapClass="scrollbar-wrapper">
    <el-menu
      :collapse="isCollapse"
      :default-active="$route.path"
      :show-timeout="200"
      active-text-color="#409EFF"
      background-color="#304156"
      mode="vertical"
      text-color="#bfcbd9"
    >
      <sidebar-item v-for="route in routes" :key="route.name" :base-path="route.path" :item="route"></sidebar-item>
    </el-menu>
  </el-scrollbar>
</template>

<script>
import { mapGetters } from 'vuex'
import SidebarItem from './SidebarItem'

export default {
  components: { SidebarItem },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    routes() {
      return this.$router.options.routes
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  }
}
</script>
