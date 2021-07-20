<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger :isActive="sidebar.opened" :toggleClick="toggleSideBar" class="hamburger-container"></hamburger>
    <breadcrumb></breadcrumb>
    <el-dropdown class="avatar-container" trigger="click">
      <div class="avatar-wrapper">
        <img :src="avatar+'?imageView2/1/w/80/h/80'" class="account-avatar">
        <i class="el-icon-caret-bottom"></i>
      </div>
      <el-dropdown-menu slot="dropdown" class="account-dropdown">
        <el-dropdown-item divided>
          <span style="display:block;" @click="changePassword">修改密码</span>
        </el-dropdown-item>
        <el-dropdown-item divided>
          <span style="display:block;" @click="logout">退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </el-menu>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/breadcrumb'
import Hamburger from '@/components/hamburger'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload() // 为了重新实例化vue-router对象 避免bug
      })
    },
    changePassword() {
      this.$router.push('/change_password')
    }
  }
}
</script>

<style lang="scss" rel="stylesheet/scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;

.hamburger-container {
  line-height: 58px;
  height: 50px;
  float: left;
  padding: 0 10px;
}

.screenfull {
  position: absolute;
  right: 90px;
  top: 16px;
  color: red;
}

.avatar-container {
  height: 50px;
  display: inline-block;
  position: absolute;
  right: 35px;

.avatar-wrapper {
  cursor: pointer;
  margin-top: 5px;
  position: relative;

.account-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
}

.el-icon-caret-bottom {
  position: absolute;
  right: -20px;
  top: 25px;
  font-size: 12px;
}

}
}
}
</style>

