<template>
  <div class="app-page">
    <!-- 对话框 - 创建账号 -->
    <el-dialog title="创建账号" width="80%"
               :visible.sync="dialogForm.visible">
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
    <!-- 对话框 - 用户详情 -->
    <el-dialog title="用户详情" width="80%"
               :visible.sync="dialogView.visible">
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
    <el-dialog title="高级搜索" width="70%"
               :visible.sync="dialogSearch.visible">
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
        <el-button type="primary" :loading="processing" @click="handleSubmitAdvancedSearch">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 操作栏 -->
    <div class="app-ops-row">
      <el-button type="primary"
                 icon="el-icon-download"
                 :loading="loadingStates['unshelve']"
                 @click="handleCreate('unshelve')">创建账号...
      </el-button>
      <!--      <el-popconfirm title="确定要删除吗？"-->
      <!--                     style="margin-right: 5px;"-->
      <!--                     @confirm="handleBatchOps('delete')">-->
      <!--        <el-button slot="reference" type="danger"-->
      <!--                   :disabled="selectedIds.isEmpty()"-->
      <!--                   :loading="loadingStates['delete']"-->
      <!--                   icon="el-icon-delete">删除-->
      <!--        </el-button>-->
      <!--      </el-popconfirm>-->
      <!--      <el-button type="danger"-->
      <!--                 :disabled="selectedIds.isEmpty()"-->
      <!--                 icon="el-icon-download"-->
      <!--                 :loading="loadingStates['unshelve']"-->
      <!--                 @click="handleBatchOps('unshelve')">封号-->
      <!--      </el-button>-->
      <!--      <el-button type="primary"-->
      <!--                 :disabled="selectedIds.isEmpty()"-->
      <!--                 icon="el-icon-download"-->
      <!--                 :loading="loadingStates['unshelve']"-->
      <!--                 @click="handleBatchOps('unshelve')">解封-->
      <!--      </el-button>-->
      <el-select v-model="markAction"
                 :disabled="selectedIds.isEmpty()"
                 v-loading.fullscreen.lock="fullscreenLoading"
                 placeholder="批量操作..." @change="handleBatchMarking(markAction);">
        <el-option-group
            v-for="(group, index) in markOptions"
            :key="index"
            :label="group.label">
          <el-option
              v-for="item in group.options"
              :key="item.label"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-option-group>
      </el-select>
      <div style="flex-grow: 1"/>
      <el-button type="text" @click="fetchData">刷新</el-button>
      <el-input clearable v-model="query.q"
                style="width: 240px"
                placeholder="请输入用户ID或用户名"
                prefix-icon="el-icon-search"
                @keydown.enter="fetchData"
                @change="fetchData"
                @clear="fetchData">搜索
      </el-input>
      <el-button type="primary" @click="dialogSearch.visible = true">高级搜索</el-button>
    </div>
    <!-- 表格 -->
    <el-table fit highlight-current-row stripe
              :border="true"
              v-loading="loading"
              :element-loading-text="loadingText"
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
          <el-link type="primary" style="font-size:12px;" @click="handleNickClicked(props.row)">
            {{ props.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="创建角色数"
                       align="center" min-width="110"
                       sortable="custom">
        <template slot-scope="props">
          <el-link type="primary" style="font-size:12px;" @click="handleNickClicked(props.row)">
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
      <el-table-column prop="blockUntil" label="账号状态"
                       align="center" min-width="100"
                       sortable="custom">
        <template slot-scope="props">
          <el-tag v-if="props.row.blockUntil" type="info">已封</el-tag>
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
                       fixed="right" align="center" width="160">
        <template slot-scope="props">
          <el-button type="text" @click="openWithdrawDialog(props.row)">重置密码</el-button>
          <template>
            <el-button v-if="props.row.blockedUntil" type="text" @click="openWithdrawDialog(props.row)">解封</el-button>
            <el-button v-else type="text" @click="openWithdrawDialog(props.row)">封号</el-button>
          </template>
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

const { today, yesterday, past2, past3, past7, past30, past60, past90 } = datePickerShortcuts

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
      markAction: '',
      markOptions: [
        {
          label: '',
          options: [
            { label: '删除...', value: 'delete' },
          ]
        },
        {
          label: '',
          options: [
            { label: '封号...', value: 'block' },
            { label: '解封...', value: 'un-block' }
          ]
        },
        {
          label: '',
          options: [
            { label: '设为普通', value: 'block' },
            { label: '设为高级', value: 'un-block' }
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
      }
    }
  },
  methods: {
    /**
     * 创建账号
     */
    handleCreate() {
      console.log('handleCreate')
      this.dialogForm.visible = true
    },
    /**
     * 批量标注
     * @param action 批量标记动作
     */
    handleBatchMarking(action) {
      console.log('handleBatchMarking', action)
      this.handleBatchOps(action).then(() => {
        this.markAction = ''
      })
    },
    /**
     * 高级搜索 - 提交
     */
    handleSubmitAdvancedSearch() {
      this.processing = true
      console.log('handleSubmitAdvancedSearch')
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
     * 查看用户详情
     */
    handleViewDetail(row) {
      this.dialogView.visible = true
      this.dialogView.entity = row
    },
    /**
     * 处理几个标记位的变更
     *
     * @param row 标记位所在行对应的实体对象
     * @param field 标记位字段
     */
    handleFlagChanged(row, field) {
      const partial = {}
      partial[field] = row[field]
      this._doBatch(row.id, partial)
    },
    /**
     * 批量处理
     */
    handleBatchOps(action) {
      const partials = {
        'delete': { active: 0 },
        'block': { state: 0 },
        'unblock': { audited: 1 }
      }
      this.loadingStates[action] = true
      this.loadingText = '处理中...'
      const partial = partials[action]
      return this._doBatch(action, this.selectedIds, partial)
    },
    /**
     * 批量保存，返回 {Promise<void>} 对象。
     *
     * @param action batch action
     * @param ids ids of updating entities
     * @param partial modified fields
     */
    _doBatch(action, ids, partial) {
      return
      console.log('批量处理:', ids, partial)
      this.loading = true
      this.loadingStates[action] = true
      return userApi.batch(ids, partial).then(() => {
        this.$notify({
          title: '操作成功',
          type: 'success'
        })
        this.loadingStates[action] = false
        this.fetchData()
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
