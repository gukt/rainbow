<template>
  <div class="app-page">
    <!-- 对话框 - 创建账号 -->
    <el-dialog title="创建账号" width="80%" :visible.sync="dialogForm.visible">
      <el-form label-position="left" label-width="80px" style="margin: 0 24px;">
        <el-form-item label="用户名">
          <el-input></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-input></el-input>
        </el-form-item>
        <template>
          <el-form-item label="封号到">
            <el-input></el-input>
          </el-form-item>
        </template>
      </el-form>
      <!-- 对话框底部按钮 -->
      <span slot="footer" class="app-dialog-footer">
        <el-button @click="dialogView.visible = false">取 消</el-button>
        <el-button type="primary" @click="dialogView.visible = false">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 对话框 - 封号 -->
    <el-dialog title="封号设置" width="80%" :visible.sync="dialogBlockingForm.visible">
      <el-form label-position="left" label-width="80px" style="margin: 0 24px;">
        <el-form-item label="封号时长">
          <el-input></el-input>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <span slot="footer" class="app-dialog-footer">
        <el-button @click="dialogView.visible = false">取 消</el-button>
        <el-button type="primary" @click="dialogView.visible = false">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 对话框 - 用户详情 -->
    <el-dialog title="用户详情" width="80%" :visible.sync="dialogView.visible">
      <el-form label-position="left" label-width="80px" style="margin: 0 24px;">
        <el-carousel height="300">
          <el-carousel-item v-for="image in dialogView.entity.images" :key="image">
            <el-image fit="contain" :src="image.file | houseImage"/>
            <h3 class="small">{{ image.file }}</h3>
          </el-carousel-item>
        </el-carousel>
        <el-form-item label-width="0px">
          <h1>{{ dialogView.entity | houseTitle }}</h1>
          <div>{{ dialogView.entity.createdAt }}发布 最后更新于: {{ dialogView.entity.updatedAt }}</div>
        </el-form-item>
        <el-row>
          <el-col span="8">
            <el-form-item label="卧室类型">{{ dialogView.entity.roomType }}</el-form-item>
          </el-col>
          <el-col span="8">
            <el-form-item label="户型">
              {{ dialogView.entity | houseLayout }}
            </el-form-item>
          </el-col>
          <el-col span="8">
            <el-form-item label="楼层">
              {{ dialogView.entity.floor }}/{{ dialogView.entity.totalFloor }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col span="8">
            <el-form-item label="地铁">
              {{ dialogView.entity.line }}-{{ dialogView.entity.station }}
            </el-form-item>
          </el-col>
          <el-col span="8">
            <el-form-item label="装修情况">{{ dialogView.entity.decoration }}</el-form-item>
          </el-col>
          <el-col span="8">
            <el-checkbox :value="dialogView.entity.elevator">电梯</el-checkbox>
          </el-col>
        </el-row>
        <el-row>
          <el-col span="8">
            <el-form-item label="付款方式">{{ dialogView.entity.payment }}</el-form-item>
          </el-col>
          <el-col span="8">
            <el-form-item label="水电煤">
              {{ dialogView.entity.sdm }}
            </el-form-item>
          </el-col>
          <el-col span="8">
            <el-form-item label="最短租期">{{ dialogView.entity.minTenancy }}</el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="用户详情">{{ dialogView.entity.detail }}</el-form-item>
        <el-form-item label="设备">
          <el-tag v-for="item in dialogView.entity.equips"
                  :key="item">{{ item }}
          </el-tag>
        </el-form-item>
        <el-form-item label="标签">
          <el-tag v-for="item in dialogView.entity.tags"
                  :key="item">{{ item }}
          </el-tag>
        </el-form-item>
        <el-form-item label="特色">
          <el-checkbox :value="dialogView.entity.promoted">促销用户</el-checkbox>
          <el-checkbox :value="dialogView.entity.certified">认证用户</el-checkbox>
          <el-checkbox :value="dialogView.entity.experienced">体验师用户</el-checkbox>
          <el-checkbox :value="dialogView.entity.easyCheckin">拎包入住</el-checkbox>
          <el-checkbox :value="dialogView.entity.anytime">随时看房</el-checkbox>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <span slot="footer" class="app-dialog-footer">
        <el-button @click="dialogView.visible = false">取 消</el-button>
        <el-button type="primary" @click="dialogView.visible = false">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 对话框 - 高级搜索 -->
    <el-dialog title="高级搜索" width="70%" :visible.sync="dialogSearch.visible">
      <el-form label-position="left" label-width="100px" style="margin: 0 24px;">
        <el-form-item label="关键字">
          <el-select clearable multiple filterable
                     remote reserve-keyword
                     default-first-option
                     v-model="query.q"
                     :remote-method="fetchOwnerSuggestions"
                     style="width: 100%;"
                     placeholder="请输入用户ID/用户名/最后登陆IP">
            <el-option
                v-for="item in ownerSuggestions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间">
          <div class="flex-box">
            <el-date-picker type="date" v-model="query.createdStart"
                            :picker-options="createdDatePickerOptions"
                            style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
            <span class="w32"/>
            <el-date-picker type="date" v-model="query.createdEnd"
                            style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="最后登陆时间">
          <div class="flex-box">
            <el-date-picker type="datetime" v-model="query.loginStart"
                            :picker-options="loginDatePickerOptions"
                            style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
            <span class="w32"/>
            <el-date-picker type="datetime" v-model="query.loginEnd"
                            :picker-options="loginDatePickerOptions"
                            style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="封号时间">
          <div class="flex-box">
            <el-date-picker type="date" v-model="query.blockStart"
                            :picker-options="blockDatePickerOptions"
                            style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
            <span class="w32"/>
            <el-date-picker type="date" v-model="query.blockEnd"
                            :picker-options="blockDatePickerOptions"
                            style="width: 100%" value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="类型">
          <el-checkbox v-model="query.types"
                       v-for="(item, index) in userTypes"
                       :label="index === 0 ? null : item"
                       :key="item">{{ item }}
          </el-checkbox>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <span slot="footer" class="app-dialog-footer">
        <el-button @click="dialogSearch.visible = false">取 消</el-button>
        <el-button type="primary" :loading="processing" @click="doAdvancedSearch">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 操作栏 -->
    <div class="app-ops-row">
      <el-button type="primary"
                 icon="el-icon-download"
                 :loading="loadingStates['unshelve']"
                 @click="onCreateButtonClicked('unshelve')">创建账号...
      </el-button>
      <!-- 批量操作行为下拉选择框 -->
      <!-- NOTE：不使用 v-model 绑定变量，因为每次选中后需要自动移除掉被选项 -->
      <el-select value="" v-loading.fullscreen.lock="fullscreenLoading"
                 placeholder="批量操作..." @change="onBatchActionChanged">
        <el-option-group v-for="(group, index) in batchActions" :key="index" :label="group.label">
          <el-option v-for="item in group.options"
                     :key="item.label"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-option-group>
      </el-select>
      <div style="flex-grow: 1"/>
      <el-button type="text" @click="fetchData">刷新</el-button>
      <el-autocomplete clearable v-model="query.q" placeholder="请输入用户ID或用户名"
                       style="width: 240px" prefix-icon="el-icon-search"
                       value-key="value"
                       :debounce="500"
                       :fetch-suggestions="onFetchSuggestions"
                       @change="fetchData"
                       @clear="fetchData">
      </el-autocomplete>
      <el-button type="primary" @click="dialogSearch.visible = true">高级搜索</el-button>
    </div>
    <!-- 表格 -->
    <el-table fit highlight-current-row stripe
              :border="true"
              v-loading="loading" :element-loading-text="loadingText"
              :default-sort="defaultSort"
              :data="listData"
              @selection-change="handleSelectionChanged"
              @sort-change="handleSortChanged">
      <el-table-column type="selection" align="left" width="40"/>
      <el-table-column prop="id" label="ID"
                       fixed="left" align="center" width="180"
                       sortable="custom"/>
      <el-table-column prop="name" label="用户名"
                       align="center" width="100"
                       sortable="custom">
        <template slot-scope="props">
          <el-link type="primary" style="font-size:12px;" @click="onNameLinkClicked(props.row)">
            {{ props.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="创建角色数"
                       align="center" min-width="110"
                       sortable="custom">
        <template slot-scope="props">
          <el-link type="primary" style="font-size:12px;" @click="onRoleLinkClicked(props.row)">
            1
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型"
                       align="center"
                       sortable="custom">
        <template slot-scope="props">
          <el-tag>{{ props.row.type | toUserTypeString }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inactive" label="已删除"
                       align="center" min-width="100"
                       sortable="custom">
        <template slot-scope="props">
          <el-checkbox :disabled="false" :value="props.row.inactive"/>
        </template>
      </el-table-column>
      <el-table-column prop="blockedUntil" label="账号状态"
                       align="center" min-width="100"
                       sortable="custom">
        <template slot-scope="props">
          <el-tag v-if="props.row.blockedUntil" type="warning">已封</el-tag>
          <el-tag v-else type="success">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="loginTime" label="最后登陆时间"
                       align="center" width="160"
                       sortable="custom">
        <template slot-scope="props">{{ props.row.loginTime }}</template>
      </el-table-column>
      <el-table-column prop="loginIp" label="最后登陆 IP"
                       align="center" width="160"
                       sortable="custom">
        <template slot-scope="props">{{ props.row.loginIp }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间"
                       align="center" width="160"
                       sortable="custom">
        <template slot-scope="props">{{ props.row.createdAt }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="最后更新时间"
                       align="center" width="160"
                       sortable="custom">
        <template slot-scope="props">{{ props.row.createdAt }}</template>
      </el-table-column>

      <el-table-column prop="actions" label="操作"
                       fixed="right" align="center" width="120">
        <template slot-scope="props">
          <el-popconfirm title="确定要重置吗？"
                         style="margin-right: 5px;"
                         @confirm="onResetPasswordClicked(props.row)">
            <el-button slot="reference" type="text">重置密码</el-button>
          </el-popconfirm>
          <el-button type="text" @click="onEditButtonClicked(props.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination :current-page="query.page + 1"
                   :page-size="query.size"
                   :page-sizes="pagination.pageSizes"
                   :total="pagination.totalElements"
                   :layout="pagination.layout"
                   class="app-pagination pull-right"
                   @size-change="handlePageSizeChanged"
                   @current-change="handleCurrentPageChanged">
    </el-pagination>
  </div>
</template>

<script>
import * as userApi from '../../../api/user-api'
import { commonMixin, listMixin, ownerSuggestionMixin } from '../../../mixins'
import { datePickerShortcuts, defaultLoadingText, userTypes } from '../../../utils/consts'

const { today, yesterday, past2, past3, past7, past30 } = datePickerShortcuts

export default {
  mixins: [commonMixin, ownerSuggestionMixin, listMixin],
  data() {
    return {
      datePickerShortcuts,
      createdDatePickerOptions: {
        shortcuts: [today, yesterday, past2, past3, past7, past30]
      },
      loginDatePickerOptions: {
        shortcuts: [today, yesterday, past3, past7]
      },
      blockDatePickerOptions: {
        shortcuts: [today, yesterday, past3, past7]
      },
      userTypes,
      fetchMethod: this.fetchData,
      query: {
        types: [0, 1]
      },
      // selectedBatchAction: '',
      batchActions: [
        {
          label: '',
          options: [
            { label: '删除...', value: 'delete' }
          ]
        },
        {
          label: '',
          options: [
            { label: '封号...', value: 'block' },
            { label: '解封...', value: 'unblock' }
          ]
        },
        {
          label: '',
          options: [
            { label: '设为普通', value: 'type0' },
            { label: '设为高级', value: 'type1' }
          ]
        }
      ],
      dialogView: {
        visible: false,
        entity: {}
      },
      dialogForm: {
        visible: false,
        mode: 'new',
        entity: {}
      },
      dialogSearch: {
        visible: false,
        entity: {}
      },
      dialogBlockingForm: {
        visible: false,
        batch: true
      }
    }
  },
  methods: {
    onNameLinkClicked() {
      console.log('onNameClicked', arguments)
      this.$notify({
        title: 'onNameClicked',
        type: 'success'
      })
    },
    onRoleLinkClicked() {
      console.log('onRoleClicked', arguments)
      this.$confirm('确定要删除吗', {
        callback() {
          console.log(arguments)
        }
      })
    },
    onResetPasswordClicked() {
      console.log('onResetPasswordClicked', arguments)
      this.$notify({
        title: '重置成功',
        type: 'success'
      })
    },
    onEditButtonClicked() {
      console.log('onEditClicked', arguments)
    },
    /**
     * 处理搜索框自动提示
     *
     * @param q 输入的值，字符串
     * @param cb 建议数据准备好时通过 cb(data) 返回到 autocomplete 组件中
     * @see https://element.eleme.cn/#/zh-CN/component/input
     */
    onFetchSuggestions(q, cb) {
      console.log('onFetchSuggestions', q, cb)
      const arr = []
      if (!q) {
        return cb(arr)
      }
      userApi.search({ q, size: 15, page: 0 }).then(res => {
        res.content && res.content.forEach(item => {
          arr.push({ value: item.name })
        })
        cb(arr)
      })
    },
    /**
     * 处理'创建账号'按钮被点击
     */
    onCreateButtonClicked() {
      console.log('onCreateButtonClicked')
      this.dialogForm.visible = true
    },
    _confirm(message, callback) {
      this.$confirm(message, { callback })
    },
    /**
     * 处理'批量操作项变更'事件
     *
     * @param action 当前选择的项
     */
    onBatchActionChanged(action) {
      console.log('onBatchActionChanged', action)
      // this.selectedBatchAction = null
      // 是否选中了？
      if (this.selectedIds.isEmpty()) {
        this.$message({
          message: '请选择你要操作的记录',
          type: 'error'
        })
        return
      }
      let data = []
      const that = this
      switch (action) {
        case 'delete':
          data = [...this.selectedIds]
          this.$confirm('确定要删除吗?', {
            callback(result) {
              console.log(result)
              if (result === 'confirm') {
                that._doBatching(action, data)
              }
            }
          })
          break
        case 'block':
          // Show dialog
          this.dialogBlockingForm.visible = true
          break
        case 'unblock':
          this.selectedIds.forEach(id => {
            data.push({ id, blockUntil: null })
          })
          this._doBatching(action, data)
          break
        case 'type0':
          this.selectedIds.forEach(id => {
            data.push({ id, type: 0 })
          })
          this._doBatching(action, data)
          break
        case 'type1':
          this.selectedIds.forEach(id => {
            data.push({ id, type: 1 })
          })
          this._doBatching(action, data)
          break
      }
    },
    _doBatching(action, data) {
      console.log('正在请求批量删除', action, data)
      userApi.batch({ action, data }).then(() => {
        this.$notify({
          title: '操作成功',
          type: 'success'
        })
        this.loadingStates[action] = false
        this.fetchData()
      })
    },
    /**
     * 提交 - 高级搜索
     */
    doAdvancedSearch() {
      this.processing = true
      console.log('doAdvancedSearch')
      this.fetchData().then(() => {
        this.processing = false
        this.dialogSearch.visible = false
      }).catch(() => {
        this.$notify({
          title: '搜索失败',
          type: 'error'
        })
        this.processing = false
      })
    },
    /**
     * 加载列表数据，返回 {Promise<void>} 对象。
     */
    fetchData() {
      this.loading = true
      this.loadingText = defaultLoadingText
      return userApi.search(this.query).then(res => {
        this.listData = res.content || []
        this.pagination.totalElements = res.totalElements
        this.loading = false
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
