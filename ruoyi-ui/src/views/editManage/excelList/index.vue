<template>
  <div class="excelList app-container">
    <!-- 查询 -->
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
    >
      <el-form-item label="名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择"
          clearable
        >
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 批量操作 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          :disabled="single"
          size="mini"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          :disabled="multiple"
          size="mini"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-upload"
          size="mini"
          >导入</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>
    <!-- 表格 -->
    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="tList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        prop="title"
        label="名称"
        :show-overflow-tooltip="true"
        width="160"
      ></el-table-column>
      <el-table-column
        prop="sheet"
        label="工作簿"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="link"
        label="链接"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_normal_disable"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column label="最后修改时间" align="center" prop="updatetime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updatetime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>=0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
// TODO 模板接口
import { templateList, delTemplateById } from '@/api/editManage';

export default {
  name: 'ExcelList',
  dicts: ["sys_show_hide", "sys_normal_disable"],
  components: { },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 模版数据数据
      tList: [],
      // 重新渲染表格状态
      refreshTable: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        status: '0',
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询菜单列表 */
    getList() {
      this.loading = true;
      templateList().then((result) => {
        console.log('templateList result', result);
        this.tList = result.rows;
        this.total = result.total || 0;
        this.loading = false;
      });
    },
    // 表头操作
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.dictId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$store.dispatch('setTemplateId', '');
    },
    // 行操作
    /** 修改按钮操作 */
    handleUpdate(row) {
      // 设置修改模板id
      this.$store.dispatch('setTemplateId', row.id);
      // 跳转设计页面
      this.$router.push({ path:'/design' });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const _this = this;
      delTemplateById(row.id).then((result) => {
        console.log('result', result)
        if (result.code == 200) {
          _this.getList();
        }
      });
    },
  },
}
</script>

<style>
  /* .excelList {

  } */
</style>
