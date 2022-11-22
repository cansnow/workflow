<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane label="设置" name="setting">
        <div style="display: flex;">
          <div style="display: flex; width: 100%;">
            <div style="margin-right: 30px; width: 40px; line-height: 36px;">条件</div>
            <div style="display: flex; flex-wrap: wrap; width: calc(100% - 70px);">
              <div  style="width: 100%; display: flex;">
                <el-select style="flex: auto" v-model="form.condition" placeholder="请选择">
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <el-select
                  v-if="
                    form.condition != 'none' &&
                    form.condition !=  'phone' &&
                    form.condition !=  'emil' &&
                    form.condition !=  'IDCard' &&
                    form.condition !=  'custom'
                  "
                  style="width: 50%; margin-left: 20px;"
                  v-model="form.Judge"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="item in optionsJudge"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </div>
              <div
                v-if="
                  form.condition != 'none' &&
                  form.condition !=  'phone' &&
                  form.condition !=  'emil' &&
                  form.condition !=  'IDCard'
                "
                style="width: 100%; margin-top: 10px; display: flex;">
                <div style="flex: auto;">
                  <el-input  v-model="form.start" :placeholder="placeholder(0)"></el-input>
                </div>
                <div v-if="(form.Judge == 'and' || form.Judge == 'not') && form.condition != 'custom'" style="width: 50%; margin-left: 20px;">
                  <el-input  v-model="form.end" :placeholder="placeholder(1)"></el-input>
                </div>                
              </div>
            </div>
          </div>
        </div>
        <div v-if="form.condition != 'none'" style="display: flex; align-items: center; margin-top: 10px">
          <div style="margin-right: 30px; width: 40px;">位置</div>
          <div style="flex: auto;">
            <el-input v-model="form.pos" placeholder="请输入内容" readonly></el-input>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="错误提示" name="tip">
        <div style="display: flex; align-items: center;">
          <div style="margin-right: 30px;">标题</div>
          <div style="flex: auto;">
            <el-input v-model="form.title" placeholder="请输入内容"></el-input>
          </div>
        </div>
        <div style="display: flex; margin-top: 10px;">
          <div style="margin-right: 30px;">内容</div>
          <div style="flex: auto;">
            <el-input
              v-model="form.content"
              placeholder="请输入内容"
              type="textarea"
              :rows="4"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
    data() {
      return {
        activeName: 'setting',
        options: [
          { label: '无', value: 'none' },
          { label: '整数', value: 'int' },
          { label: '小数', value: 'float' },
          { label: '日期', value: 'datetime' },
          { label: '文本长度', value: 'length' },
          { label: '手机号', value: 'phone' },
          { label: '邮箱', value: 'emil' },
          { label: '身份证号', value: 'IDCard' },
          { label: '自定义正则表达式', value: 'custom' },
        ],
        optionsJudge: [
          { label: '介于', value: 'and' },
          { label: '未介于', value: 'not' },
          { label: '等于', value: '==' },
          { label: '不等于', value: '!=' },
          { label: '大于', value: '>' },
          { label: '小于', value: '<' },
          { label: '大于等于', value: '>=' },
          { label: '小于等于', value: '<=' },
        ],
        form: {
          title: '',
          content: '',
          pos: '',
          condition: 'none',
          Judge: 'and',
          start: '',
          end: '',
        },
        index: -1,
      };
    },
    computed: {
      placeholder() {
        return (type) => {
          switch(this.form.condition) {
            case 'Int':
            case 'Float':
            case 'length':
              if (this.form.Judge == 'Between' || this.form.Judge == 'NoBetween') {
                return `请输入最${type == 0 ? '大' : '小'}值`;
              }
              return '例如: 1';
            case 'datetime':
              return `例如: ${type == 0 ? '2022/11/11' : '2023/11/11'}`;
            case 'custom':
              return '例如：/[0-9A-Z].*?/igm';
            default:
              return '';
          }
        }
      },
    },
    methods: {
      // 获取数据
      getData() {
        return JSON.parse(JSON.stringify({ ...this.form, index: this.index }));
      },
      setData(data) {
        this.resetData();
        Object.assign(this.form, JSON.parse(JSON.stringify(data)));
        this.index = data.index;
      },
      resetData() {
        this.activeName = 'setting';
        this.index = -1;
        Object.assign(this.form, {
          title: '',
          content: '',
          pos: '',
          condition: 'none',
          Judge: 'and',
          start: '',
          end: '',
        })
      },
      setCellValue(pos) {
        this.form.pos = pos;
      },
    }
  };
</script>
