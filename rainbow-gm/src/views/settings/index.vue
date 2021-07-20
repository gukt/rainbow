<template>
  <div class="app-page">
    <!-- 表单-->
    <el-form label-position="left" label-width="120px" style="margin: 0 24px;">
      <el-form-item label="公众号名称">
        <el-input clearable
                  v-model="configs.gzh"
                  placeholder="请输入公众号名称"
                  @change="handleSaveBasic"></el-input>
      </el-form-item>
      <el-form-item label="公众号地址">
        <el-input clearable
                  v-model="configs.gzhUrl"
                  placeholder="请输入公众号地址"></el-input>
      </el-form-item>
      <el-form-item label="服务电话">
        <el-input clearable
                  v-model="configs.servicePhone"
                  placeholder="请输入公众号地址"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSaveBasic">保存</el-button>
      </el-form-item>
      <el-divider/>
      <el-form-item label="举报类型">
        <el-tag closable v-for="(item, index) in configs.reportTypes"
                :key="item + index">{{ item }}
        </el-tag>
      </el-form-item>
      <el-form-item label="设备">
        <el-tag closable v-for="(item, index) in configs.equips"
                :key="item + index">{{ item }}
        </el-tag>
      </el-form-item>
      <el-form-item label="标签">
        <el-tag closable v-for="item in configs.tags"
                :key="item">{{ item }}
        </el-tag>
        <el-input
          class="input-new-tag"
          v-if="inputVisible"
          v-model="inputValue"
          ref="saveTagInput"
          size="small"
          @keyup.enter.native="handleInputConfirm"
          @blur="handleInputConfirm"
        >
        </el-input>
        <el-button v-else icon="el-icon-plus"
                   class="button-new-tag"
                   size="mini"
                   @click="showInput"></el-button>
      </el-form-item>
      <el-divider/>
      <el-form-item label="App 首页横幅">
        <el-carousel height="300">
          <el-carousel-item v-for="(item,index) in configs.homeSwiper" :key="index">
            <el-image fit="contain" :src="item.img | houseImage"/>
            <h3 class="small">{{ item.img }}</h3>
          </el-carousel-item>
        </el-carousel>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

import * as settingsApi from '../../api/server-api'
import { commonMixin } from '../../mixins'

export default {
  mixins: [commonMixin],
  data() {
    return {
      inputVisible: false,
      inputValue: '',
      configs: {}
    }
  },
  computed: {},
  methods: {
    handleSaveBasic(newValue) {
      console.log('handleSaveBasic', newValue)
      this.$notify({
        title: '更新成功',
        type: 'success'
      })
    },
    handleClose(tag) {
      this.configs.tags.splice(this.configs.tags.indexOf(tag), 1)
    },

    showInput() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },

    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue) {
        this.configs.tags.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    fetchData() {
      settingsApi.getConfigs().then(res => {
        this.configs = res
      })
    }
  },
  created() {
    this.fetchData()
  }
}
</script>

<style>

</style>
