import * as userApi from '../api/user-api'
import { defaultLoadingText } from '../utils/consts'

// 表格显示 Mixin
export var listMixin = {
  data() {
    return {
      // 分页设置
      pagination: {
        // 页大小下拉选项
        pageSizes: [5, 10, 15, 20, 50, 100],
        // 分页布局
        layout: 'total, sizes, prev, pager, next',
        // 总记录条数
        totalElements: 0
      },
      // 已选择的记录 ids
      selectedIds: [],
      // 表格数据
      listData: [],
      defaultSort: { prop: 'id', order: 'descending' },
      // defaultSort: { prop: 'id' },
      // 查询条件
      query: {
        // 当前页页码
        page: 0,
        // 页大小
        size: 15,
        // 排序规则
        sort: 'id,desc'
      },
      // 拉取列表数据的方法
      fetchMethod: null
    }
  },
  computed: {
  },
  created: function () {
    if (!this.fetchMethod) {
      console.error('请提供 fetchData 方法名，用以加载列表数据')
    }
  },
  methods: {
    /**
     * 更改页大小
     * @param size Page size
     */
    handlePageSizeChanged(size) {
      this.query.size = size
      this.query.page = 0
      this.fetchMethod && this.fetchMethod()
    },
    /**
     * 翻页
     * @param page Current page index
     */
    handleCurrentPageChanged(page) {
      this.query.page = page - 1
      this.fetchMethod && this.fetchMethod()
    },
    /**
     * 处理列表多选发生改变
     * @param rows Selected rows (entities)
     */
    handleSelectionChanged(rows) {
      this.selectedIds.splice(0, this.selectedIds.length)
      rows.forEach(row => {
        this.selectedIds.push(row.id)
      })
      // console.log('选中了', this.selectedIds)
    },
    /**
     * 处理排序变更（使用服务端排序）
     * @param column Changed sortable column
     */
    handleSortChanged(column) {
      console.log('handleSortChanged', column)
      const { prop, order } = column
      if (prop) {
        this.query.sort = `${prop},${order === 'descending' ? 'desc' : 'asc'}`
        this.fetchMethod()
      }
    }
  }
}
// 通用 Mixin
export var commonMixin = {
  data() {
    return {
      processing: false,
      loading: false,
      loadingText: defaultLoadingText,
      loadingStates: [],
      fullscreenLoading: false
    }
  }
}

export var ownerSuggestionMixin = {
  data() {
    return {
      ownerSuggestions: [],
      selectedOwners: ''
    }
  },
  methods: {
    fetchOwnerSuggestions(q) {
      this.loading = true
      console.log('fetchOwnerSuggestions', q)
      // 只搜索 active 的，且最多每页显示 15 条
      const params = {
        active: 1,
        q,
        page: 0,
        size: 15
      }
      userApi.search(params).then(res => {
        this.ownerSuggestions.clear()
        res.content && res.content.forEach(item => {
          this.ownerSuggestions.push({
            label: `${item.id}-${item.nick}`,
            value: item.id
          })
        })
        this.loading = false
      })
    }
  }
}
