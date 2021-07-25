<template>
  <div class="app-page">
    <!-- 求租详情 - 对话框 -->
    <el-dialog title="求租详情" width="80%"
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
        <el-form-item label="求租详情">{{ dialogView.entity.detail }}</el-form-item>
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
          <el-checkbox :value="dialogView.entity.promoted">促销求租</el-checkbox>
          <el-checkbox :value="dialogView.entity.certified">认证求租</el-checkbox>
          <el-checkbox :value="dialogView.entity.experienced">体验师求租</el-checkbox>
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
    <!-- 对话框 - 高级搜索 -->
    <el-dialog title="高级搜索" width="80%"
               :visible.sync="dialogSearch.visible">
      <el-form label-position="left" label-width="100px" style="margin: 0 24px;">
        <el-form-item label="标题包含">
          <el-input clearable v-model="query.q"></el-input>
        </el-form-item>
        <el-form-item label="价格范围">
          <div class="flex-box">
            <el-input clearable v-model="query.minPrice"
                      prefix-icon="el-icon-money"
                      placeholder="最小价格"></el-input>
            <span class="w32"/>
            <el-input clearable v-model="query.maxPrice"
                      prefix-icon="el-icon-money"
                      placeholder="最大价格"></el-input>
          </div>
        </el-form-item>
        <el-form-item label="更新时间">
          <div class="flex-box">
            <el-date-picker type="datetime"
                            style="width: 100%"
                            v-model="query.updatedStart"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="起始时间">
            </el-date-picker>
            <span class="w32"/>
            <el-date-picker type="datetime" style="width: 100%"
                            v-model="query.updatedEnd"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="结束时间">
            </el-date-picker>
          </div>
        </el-form-item>
        <el-form-item label="人数">
          <div class="flex-box">
            <el-input clearable v-model="query.minPerson"
                      prefix-icon="el-icon-user"
                      placeholder="最少人数"></el-input>
            <span class="w32"/>
            <el-input clearable v-model="query.maxPerson"
                      prefix-icon="el-icon-user"
                      placeholder="最多人数"></el-input>
          </div>
        </el-form-item>
        <el-form-item label="房间类型">
          <el-checkbox v-model="query.roomTypes"
                       v-for="(item, index) in allRoomTypes"
                       :label="index === 0 ? null : item"
                       :key="item">{{ item }}
          </el-checkbox>
        </el-form-item>
        <el-form-item label-width="0">
          <el-checkbox v-model="query.active">已上架</el-checkbox>
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
                       align="center" width="60" prop="id" label="ID"/>
      <!--      <el-table-column align="center" width="65" label="头像">-->
      <!--        <template slot-scope="props">-->
      <!--          <el-avatar :src="props.row.avatar"></el-avatar>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
      <el-table-column align="left" width="280" prop="community" label="标题">
        <template slot-scope="props">
          <el-link type="primary" @click="handleViewDetail(props.row)">
            {{ props.row.title }}
          </el-link>
          <div>
            {{ props.row.stations }}
            <el-tag v-for="(item, index) in props.row.stations" :key="index">
              {{ item[0] - item[1] }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" width="100" label="用户">
        <template slot-scope="props">
          <router-link to="/user-list">
            <el-link type="primary">{{ props.row.attrs.user.nick }}</el-link>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column align="center"
                       sortable="custom"
                       prop="roomType" label="房间类型"/>
      <el-table-column align="center"
                       sortable="custom"
                       prop="maxPerson" label="最多几人"/>
      <el-table-column align="center" width="120"
                       sortable="custom"
                       prop="minPrice" label="价格区间">
        <template slot-scope="props">
          {{ props.row.minPrice }} - {{ props.row.maxPrice }}
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center" width="85"
                       sortable="custom"
                       prop="state" label="已上架">
        <template slot-scope="props">
          <el-switch v-model="props.row.state"
                     :active-value="1"
                     :inactive-value="0"
                     @change="handleStateChanged(props.row)"></el-switch>
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
import * as roleApi from '../../../api/role-api'
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
        roomTypes: [],
        payments: [],
        ownerIds: null,
        uid: null,
        minPrice: null,
        maxPrice: null
      },
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
     * 批量处理
     * @param action 动作
     */
    handleBatchOps(action) {
      const partials = {
        'delete': { active: 0 },
        'unshelve': { state: 0 }
      }
      this.loadingStates[action] = true
      this.loadingText = '处理中...'
      const partial = partials[action]
      console.log('handleBatchOps', action, partial)
      return this._doBatchSave(this.selectedIds, partial, action)
    },
    /**
     * 批量保存
     *
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
      return roleApi.save(ids, partial).then(() => {
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
     *
     * @returns {Promise<void>}
     */
    fetchData() {
      this.loading = true
      this.loadingText = defaultLoadingText
      return roleApi.search(this.query).then(res => {
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