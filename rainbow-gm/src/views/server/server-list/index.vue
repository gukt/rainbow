<template>
  <div class="app-page">
    <!-- 房源详情 - 对话框 -->
    <el-dialog title="房源详情" width="80%"
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
        <el-form-item label="房源详情">{{ dialogView.entity.detail }}</el-form-item>
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
          <el-checkbox :value="dialogView.entity.promoted">促销房源</el-checkbox>
          <el-checkbox :value="dialogView.entity.certified">认证房源</el-checkbox>
          <el-checkbox :value="dialogView.entity.experienced">体验师房源</el-checkbox>
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
    <!-- 高级搜索 - 对话框 -->
    <el-dialog title="高级搜索" width="70%"
               :visible.sync="dialogSearch.visible">
      <el-form label-position="left" label-width="100px" style="margin: 0 24px;">
        <el-form-item label="名称">
          <el-input clearable
                    v-model="query.q"
                    placeholder="请输入搜索关键字"></el-input>
        </el-form-item>
        <el-form-item label="用户">
          <el-select clearable multiple filterable
                     remote reserve-keyword
                     default-first-option
                     v-model="selectedOwners"
                     :remote-method="fetchOwnerSuggestions"
                     style="width: 100%;"
                     placeholder="请输入用户ID/昵称/手机号">
            <el-option
              v-for="item in ownerSuggestions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="价格范围">
          <div class="flex-box">
            <el-input clearable
                      v-model="query.minPrice"
                      prefix-icon="el-icon-money"
                      placeholder="最小价格"></el-input>
            <span class="w32"/>
            <el-input clearable
                      v-model="query.maxPrice"
                      prefix-icon="el-icon-money"
                      placeholder="最大价格"></el-input>
          </div>
        </el-form-item>
        <el-form-item label="可入住时间">
          <div class="flex-box">
            <el-date-picker type="date"
                            style="width: 100%"
                            v-model="query.availableStart"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
            <span class="w32"/>
            <el-date-picker type="date" style="width: 100%"
                            v-model="query.availableEnd"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="选择日期">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="楼层">
          <div class="flex-box">
            <el-input clearable
                      v-model="query.minFloor"
                      prefix-icon="el-icon-office-building"
                      placeholder="最小楼层"></el-input>
            <span class="w32"/>
            <el-input clearable
                      v-model="query.maxFloor"
                      prefix-icon="el-icon-office-building"
                      placeholder="最大楼层"></el-input>
          </div>
        </el-form-item>
        <el-form-item label="朝向">
          <el-checkbox v-model="query.aspects"
                       v-for="(item, index) in allAspects"
                       :label="index === 0 ? null : item"
                       :key="item">{{ item }}
          </el-checkbox>
        </el-form-item>
        <el-form-item label="付款方式">
          <el-checkbox v-model="query.payments"
                       v-for="(item, index) in allPayments"
                       :label="index === 0 ? null : item"
                       :key="item">
            {{ item }}
          </el-checkbox>
        </el-form-item>
        <el-form-item label-width="0">
          <el-checkbox v-model="query.elevator">电梯</el-checkbox>
          <el-checkbox v-model="query.shortRental">短租</el-checkbox>
          <el-checkbox v-model="query.fineDecoration">精装修</el-checkbox>
          <el-checkbox v-model="query.anytime">随时看房</el-checkbox>
          <el-checkbox v-model="query.easyCheckin">拎包入住</el-checkbox>
          <el-checkbox v-model="query.promoted">促销房源</el-checkbox>
          <el-checkbox v-model="query.certified">认证房源</el-checkbox>
          <el-checkbox v-model="query.experienced">体验标记</el-checkbox>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <span slot="footer" class="app-dialog-footer">
        <el-button @click="dialogSearch.visible = false">取 消</el-button>
        <el-button type="primary" :loading="processing" @click="handleSubmitAdvancedSearch">确 定</el-button>
      </span>
    </el-dialog>
    <div class="app-ops-row">
      <el-popconfirm title="确定要删除吗？"
                     style="margin-right: 5px;"
                     @confirm="handleBatchOps('delete')">
        <el-button slot="reference" type="danger"
                   :disabled="selectedIds.isEmpty()"
                   :loading="loadingStates['delete']"
                   icon="el-icon-delete">删除
        </el-button>
      </el-popconfirm>
      <el-button type="primary"
                 :disabled="selectedIds.isEmpty()"
                 icon="el-icon-download"
                 :loading="loadingStates['unshelve']"
                 @click="handleBatchOps('unshelve')">上架
      </el-button>
      <el-select v-model="markAction"
                 :disabled="selectedIds.isEmpty()"
                 v-loading.fullscreen.lock="fullscreenLoading"
                 placeholder="标记为..." @change="handleBatchMarking(markAction);">
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
      <el-input clearable v-model="query.q"
                style="width: 200px"
                prefix-icon="el-icon-search"
                @keydown.enter="fetchData"
                @change="fetchData"
                @clear="fetchData">搜索
      </el-input>
      <el-button type="primary" @click="dialogSearch.visible = true">高级搜索</el-button>
    </div>
    <!-- 表格 -->
    <el-table fit highlight-current-row
              :border="true"
              v-loading="loading"
              :element-loading-text="loadingText"
              :default-sort="defaultSort"
              :data="listData"
              @selection-change="handleSelectionChanged"
              @sort-change="handleSortChanged">
      <!-- 全选/取消全选列 -->
      <el-table-column type="selection" align="left" width="40"/>
      <el-table-column sortable="custom" fixed="left"
                       align="center" width="60"
                       prop="id" label="ID" />
      <el-table-column align="left" width="350" prop="community" label="标题">
        <template slot-scope="props">
          <el-popover placement="right"
            :title="props.row | houseTitleInColumn"
            width="500px"
            trigger="hover"
            :content="props.row.address">
            <el-link slot="reference" type="primary" @click="handleViewDetail(props.row)">
              {{ props.row | houseTitleInColumn }}
            </el-link>
          </el-popover>
          <div>
            <el-tag style="font-size: 7px !important;" v-if="props.row.easyCheckin">领包入住</el-tag>
            <el-tag v-if="props.row.anytime">随时看房</el-tag>
          </div>
          <div>
            {{ props.row | houseLayout }} | 楼层: {{ props.row.floor }}/{{ props.row.totalFloor }}
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" width="100"
                       label="用户">
        <template slot-scope="props">
          <router-link :to="`/user-list?id=${props.row.uid}`">
            <el-link type="primary">{{ props.row.attrs.user.nick }}</el-link>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="aspect" label="朝向" />
      <el-table-column align="center" prop="decoration" label="装修" />
      <el-table-column align="center" prop="payment" label="付款方式" />
      <el-table-column sortable="custom"
                       align="center" width="120"
                       prop="minTenancy" label="最短租期(月)" />
      <el-table-column sortable="custom" align="center" width="110"
                       prop="availableAt" label="可入住时间" >
        <template slot-scope="props">
          {{ props.row.availableAt | dateOnly }}
        </template>
      </el-table-column>
      <el-table-column :disabled="true" align="center" label="电梯">
        <template slot-scope="props">
          <el-checkbox :disabled="true" :value="props.row.elevator"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="水电煤">
        <template slot-scope="props">
          <el-tag>
            {{ props.row.sdm === 0 ? '民用' : '商用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="80" label="拎包入住">
        <template slot-scope="props">
          <el-checkbox :disabled="true" v-model="props.row.easyCheckin"></el-checkbox>
        </template>
      </el-table-column>
      <el-table-column align="center" width="80" label="随时看房">
        <template slot-scope="props">
          <el-checkbox :disabled="true" v-model="props.row.anytime"></el-checkbox>
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center" width="65" label="促销">
        <template slot-scope="props">
          <el-switch v-model="props.row.promoted"
                     @change="handleFlagChanged(props.row,'promoted' )"></el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center" width="65" label="已认证">
        <template slot-scope="props">
          <el-switch v-model="props.row.certified"
                     @change="handleFlagChanged(props.row,'certified' )"></el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center" width="65" label="已体验">
        <template slot-scope="props">
          <el-switch v-model="props.row.experienced"
                     @change="handleFlagChanged(props.row,'experienced' )"></el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center" width="65" label="已审核">
        <template slot-scope="props">
          <el-switch v-model="props.row.audited"
                     @change="handleFlagChanged(props.row,'audited' )"></el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center"
                       sortable="custom"
                       width="85" label="已上架">
        <template slot-scope="props">
          <el-switch v-model="props.row.state"
                     :active-value="1"
                     :inactive-value="0"
                     @change="handleFlagChanged(props.row,'state' )"></el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" sortable="custom"
                       align="left" width="200"
                       prop="updatedAt" label="发布时间">
        <template slot-scope="props">
          创建于:{{ props.row.updatedAt }}
          <div>最后更新: {{ props.row.updatedAt }}</div>
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
import * as houseApi from '../../../api/role-api'
import { commonMixin, listMixin, ownerSuggestionMixin } from '../../../mixins'
import { defaultLoadingText } from '../../../utils/consts'

export default {
  mixins: [commonMixin, ownerSuggestionMixin, listMixin],
  data() {
    return {
      fetchMethod: this.fetchData,
      query: {
        active: 1,
        states: [0, 1],
        aspects: [],
        payments: [],
        ownerIds: null,
        uid: null,
        minFloor: null,
        maxFloor: null,
        minPrice: null,
        maxPrice: null,
        availableStart: null,
        availableEnd: null,
        elevator: null,
        shortRental: null,
        fineDecoration: null,
        anytime: null,
        easyCheckin: null,
        certified: null,
        audited: null,
        promoted: null,
        experienced: null
      },
      markAction: '',
      markOptions: [
        {
          label: '',
          options: [
            { label: '已审核', value: 'apply-audited' },
            { label: '未审核', value: 'cancel-audited' }
          ]
        },
        {
          label: '',
          options: [
            { label: '认证房源', value: 'apply-certified' },
            { label: '取消认证', value: 'cancel-certified' }
          ]
        },
        {
          label: '',
          options: [
            { label: '促销房源', value: 'apply-promoted' },
            { label: '取消促销', value: 'cancel-promoted' }
          ]
        },
        {
          label: '',
          options: [
            { label: '已体验房源', value: 'apply-experienced' },
            { label: '未体验房源', value: 'cancel-experienced' }
          ]
        }
      ],
      dialogView: {
        visible: false,
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
     * 查看房源详情
     */
    handleViewDetail(row) {
      this.dialogView.visible = true
      this.dialogView.entity = row
    },
    /**
     * 处理几个标记位的变更
     * @param row 标记位所在行对应的实体对象
     * @param field 标记位字段
     */
    handleFlagChanged(row, field) {
      const partial = {}
      partial[field] = row[field]
      this._doBatchSave(row.id, partial)
    },
    /**
     * 批量处理
     * @param action 动作
     */
    handleBatchOps(action) {
      const partials = {
        'delete': { active: 0 },
        'unshelve': { state: 0 },
        'apply-audited': { audited: 1 },
        'cancel-audited': { audited: 0 },
        'apply-certified': { certified: 1 },
        'cancel-certified': { certified: 0 },
        'apply-promoted': { promoted: 1 },
        'cancel-promoted': { promoted: 0 },
        'apply-experienced': { experienced: 1 },
        'cancel-experienced': { experienced: 0 }
      }
      this.loadingStates[action] = true
      this.loadingText = '处理中...'
      const partial = partials[action]
      console.log('handleBatchOps', action, partial)
      return this._doBatchSave(this.selectedIds, partial, action)
    },
    /**
     * 批量保存
     * @param ids Entity ids
     * @param partial 被修改的属性
     * @param action 动作
     * @returns {Promise<void>}
     * @private yes
     */
    _doBatchSave(ids, partial, action) {
      console.log('doBatchSave', ids, partial)
      this.loading = true
      this.loadingStates[action] = true
      return houseApi.save(ids, partial).then(() => {
        this.$notify({
          title: '操作成功',
          type: 'success'
        })
        this.loadingStates[action] = false
        this.fetchData()
      })
    },
    /**
     * 加载列表数据
     * @returns {Promise<void>}
     */
    fetchData() {
      this.loading = true
      this.loadingText = defaultLoadingText
      return houseApi.search(this.query).then(res => {
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
