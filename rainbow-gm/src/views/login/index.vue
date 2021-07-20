<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" autoComplete="on"
             class="login-form" label-position="left">
      <el-form-item label="请登录！"></el-form-item>
      <el-form-item prop="username" label="">
        <el-input prefix-icon="el-icon-user"
                  v-model="loginForm.username" autoComplete="on" name="username" placeholder="请输入用户名"
                  type="text"/>
      </el-form-item>
      <el-form-item prop="password" label="">
        <el-input prefix-icon="el-icon-key"
                  v-model="loginForm.password" :type="pwdType" autoComplete="on"
                  name="password" placeholder="请输入密码"
                  @keyup.enter.native="handleLogin"></el-input>
<!--        <span class="show-pwd" @click="showPassword"><svg-icon icon-class="eye"/></span>-->
      </el-form-item>
      <el-button :loading="loading" style="width:100%;" type="primary" @click.native.prevent="handleLogin">登录
      </el-button>
    </el-form>
  </div>
</template>

<script>
import { isValidUsername } from '../../utils/validate'

export default {
  data() {
    const validateUsername = (rule, value, cb) => {
      if (!isValidUsername(value)) {
        cb(new Error('请输入用户名'))
      } else {
        cb()
      }
    }
    const validatePassword = (rule, value, cb) => {
      if (value.length < 6) {
        cb(new Error('密码不能小于6位'))
      } else {
        cb()
      }
    }
    return {
      loginForm: {
        username: 'admin',
        password: '1234+aaaa'
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      pwdType: 'password'
    }
  },
  methods: {
    showPassword() {
      if (this.pwdType === 'password') {
        this.pwdType = ''
      } else {
        this.pwdType = 'password'
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('Login', this.loginForm).then(() => {
            this.loading = false
            this.$router.push({ path: '/' })
          }).catch(err => {
            console.log('error', err)
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>

<style>
.login-container {
  width: 80%;
  margin: 200px auto 0;
}

.title {
  margin: 36px auto;
}

.show-pwd {
  position: absolute;
  right: 10px;
  top: 7px;
  font-size: 16px;
  color: #666666;
  cursor: pointer;
}
</style>
