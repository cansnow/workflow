<template>
    <div tabIndex="999" class="meg-workbook" ref="megWorkbook" @parse="handleParse" @keydown="handleKeyMap">
        <div style="display: flex; align-items: center;">
            <Menus v-if="menu" class="meg-workbook-menu" ref="menus" style="width: calc(100% - 200px); max-width: 100%; flex: 1; flex-wrap: wrap; height: auto; min-height: 40px;"/>
            <div class="meg-workbook-btn">
                <el-button type="default" size="small" @click="handleSave">保存</el-button>
                <el-button type="primary" size="small" @click="handlePreview">预览</el-button>
                <el-button  type="default" size="small" @click="handleRelease">发布</el-button>
            </div>
        </div>
        <!-- 多个sheet，防止重叠，将不显示的放到最外面 -->
        <div
            style="position: relative; top: 0; left: 0; right: 0; bottom: 0; overflow: hidden;"
            :style="{ height: `calc(100vh - ${menusHeigth}px - 52px - 60px)` }"
        >
            <div
                v-for="(sheet, index) in data"
                :key="'_'+index"
                style="height: 100%; background-color: white; position: absolute; top: 0; left: 0; right: 0; bottom: 0;"
                :style="sheetIndex == '_'+index ? { zIndex: 99 } : { zIndex: -1 }"
            >
                <Sheet
                    @selectCell="handleSelectCell"
                    @selectEnd="selectEnd"
                    style="height: 100%;"
                    @click-head="handleClickHead"
                    @EditEnter="handleEditEnter"
                    :ref="'sheet_'+index"
                    :options="sheet.data"
                    :sheetIndex="index"
                    :autoCreate="autoCreate"
                />
            </div>
        </div>
        <!-- <div style="background-color: #f5f7fa; width: 100vw; display: flex;">
            <div style="max-width: 50vw;">
                <el-tabs type="border-card" :tab-position="tabPosition" v-model="sheetIndex" closable @tab-remove="(targetName) => handleTabsEdit(targetName, 'del')">
                    <el-tab-pane v-for="(sheet,index) in data" :key="'_'+index" :name="'_'+index">
                        <template slot="label">
                            <el-input
                                @keydown.native.stop="() => {}"
                                @keydown.native.enter.stop="ifEditTitle = false"
                                :ref="'label_' + index"
                                v-show="ifEditTitle && sheetIndex == '_' + index"
                                v-model="sheet.title"
                                @change="(e) => handleChangeTitle(e, index)"
                                @blur="ifEditTitle = false"
                                placeholder="请输入内容"
                                style="width: 80px;"
                            ></el-input>
                            <span
                                v-show="!ifEditTitle || sheetIndex != '_' + index"
                                @dblclick="() => handleDblclickTitle('label_' + index)"
                            >
                                {{sheet.title}}
                            </span>
                        </template>
                    </el-tab-pane>
                </el-tabs>
            </div>
            <div style="padding-left: 10px; display: flex; align-items: center;">
                <el-button @click="() => handleTabsEdit('', 'add')" type="text"><i class="el-icon-plus"></i></el-button>
            </div>
        </div> -->
        <Tabs
            :sheetIndex="sheetIndex"
            :data="data"
            :tabPosition="tabPosition"
            :ifEditTitle="ifEditTitle"
            @noEdit="ifEditTitle = false"
            @handleTabsEdit="handleTabsEdit"
            @handleChangeTitle="handleChangeTitle"
            @handleDblclickTitle="handleDblclickTitle"
            @tab-click="handleTabClick"
            @copyTabsItem="copyTabsItem"
        ></Tabs>
    </div>
</template>

<script>
import Menus from './menu/Menu.vue';
import Sheet from './sheet/Sheet.vue';

import keyMap from './mixins/keyMap';
import templateData from './templateData';

import Tabs from './tabs/index.vue';

export default {
    name: 'app',
    props: {
        menu: {
            type:Boolean,
            default:true
        },
		//是否自动补齐行列
        autoCreate: {
            type:Boolean,
            default:true
        },
    },
    data(){
        return {
            sheetIndex:'',
            tabPosition:'bottom',
            menusHeigth: 40,
            screenWidth: 0,
            data: [],
            ifEditTitle: false,
            tempTitle: [],
        }
    },
    mixins: [keyMap],
    components: {
        Sheet,
        Menus,
        Tabs,
    },
    created() {
        this.init();
    },
    mounted() {
        window.onresize = () => {
            return (() => {
                this.screenWidth = document.body.clientWidth
            })()
        };
        this.menusHeigth = this.$refs.menus.$el.offsetHeight;
    },
    watch: {
        // 监控屏幕宽度
        screenWidth() {
            this.setMenusHeigth();
        },
        sheetIndex(newValue) {
            const index = newValue.substring(1);
            const title = this.data[index].title;
            const freezeColumn = this.data[index].data.freezeColumn;
            const freezeRow = this.data[index].data.freezeRow;
            this.$emit('changSheet', { title, index, freezeColumn, freezeRow });
        },
    },
    methods: {
        // 获取焦点，用于回车换下一个单元格
        handleEditEnter() {
            // debugger;
            this.$refs.megWorkbook.focus();
        },
        //　创建副本
        copyTabsItem(index) {
            if (!!this.data[index]) {
                const temp = JSON.parse(JSON.stringify(this.data[index]));
                temp.title += ' 副本';
                this.data.push(temp);
            }           
        },
        handleTabClick(sheetIndex) {
            this.sheetIndex = sheetIndex;
        },
        // 修改sheet name
        handleChangeTitle({ event, index }) {
            const tempIndex = this.tempTitle.findIndex(item => item == event);
            if (tempIndex != -1) {
                this.$message({
                    message: 'sheet 不能同名!!!',
                    type: 'warning'
                });
                Object.assign(this.data[index], { title: this.tempTitle[index] });
            } else {
                if (!!event) {
                    this.$emit('handleChangeSheetTilte', event);
                } else {
                    this.$message({
                        message: 'sheet 不能为空!!!',
                        type: 'warning'
                    });
                    Object.assign(this.data[index], { title: this.tempTitle[index] });
                }
            }
        },
        handleDblclickTitle({ tempTitle }) {
            this.tempTitle = tempTitle; //_.map(this.data, item => item.title);
            this.ifEditTitle = true;
            // this.$nextTick(() => {
            //     setTimeout(() => {
            //         this.$refs[refV][0].focus();
            //     }, 200);
            // });
        },
        handlePreview() {
            this.$emit('handlePreview');
        },
        handleSave() {
            this.$emit('handleSave');
        },
        handleRelease() {
            this.$emit('handleRelease');
        },
        init() {
            let _this = this;
            _this.update(this.$piniastore.$state);
            this.$piniastore.$subscribe((mutation, state) => {
                _this.update(state);
            });
        },
        update(state) {
            this.data = state.data;
            if (this.data instanceof Array) {
                if (this.sheetIndex == '') {
                    this.sheetIndex = '_0';
                }
            }
        },
        getCurSheet() {
            return this.$refs['sheet'+this.sheetIndex];
        },
        // 根据sheet下标获取sheet实例
        getSheetByIndex(index = 0) {
            return this.$refs['sheet_' + index][0];
        },
        handleSelectCell() {
            this.$emit('selectCell');
        },
        selectEnd() {
            this.$emit('selectEnd');
        },
        setMenusHeigth() {
            this.menusHeigth = this.$refs.menus.$el.offsetHeight;
            this.$emit('menusHeigth', this.menusHeigth);
        },
        handleClickHead() {
            this.$emit('click-head');
        },
        getNewSheetName() {
            const titles = _.filter(_.map(this.data, item => {
                if (item.title.indexOf('sheet') != -1) {
                    return item.title.replace(/[^0-9]/g,'');
                }
            }), item => !!item).sort((a, b) => b - a);
            if (titles.length <= 0) {
                return `sheet${this.data.length + 1}`;
            }
            return `sheet${parseInt(titles[0]) + 1}`;
        },
        // 新增sheet
        handleTabsEdit({targetName, action}) {
            console.log(targetName, action);
            if (action == 'add') {
                const title = this.getNewSheetName();
                const data = JSON.parse(JSON.stringify(templateData));
                const allData = this.data;
                allData.push({ title, data });
                this.$piniastore.setData(allData);
            } else {
                if (this.data.length == 1) {
                    this.$message({
                        message: '最少一个sheet!!!',
                        type: 'warning'
                    });
                    return;
                }
                const index = targetName.substring(1);
                const temp = JSON.parse(JSON.stringify(this.data));
                temp.splice(index, 1);
                // this.sheetIndex = '_0';
                let curIndex = this.sheetIndex.substring(1);
                if (index == 0 && temp.length == 1) {
                    this.sheetIndex = '_0';
                    curIndex = 0;
                } else {
                    if (index < curIndex || (index == curIndex && curIndex == temp.length)) {
                        curIndex -= 1;
                        this.sheetIndex = '_' + curIndex;
                    }
                }
                this.$piniastore.setData(temp);
                this.$nextTick(function() {
                    const title = temp[curIndex].title;
                    const freezeColumn = temp[curIndex].data.freezeColumn;
                    const freezeRow = temp[curIndex].data.freezeRow;
                    this.$emit('changSheet', { title, index: curIndex, freezeColumn, freezeRow });
                });
            }
        },
    },
};
</script>

<style lang="scss" scoped>
.meg-workbook {
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    outline: none;

    .meg-sheet {
        flex-grow: 1;
        order: 1
    }

    &-menu {
        flex-shrink: 0;
    }
}
</style>

<style>
    .meg-workbook .el-tabs--border-card > .el-tabs__content {
        padding: 0px !important;
    }

    /* .meg-workbook .el-tabs--border-card > .el-tabs__header > .el-tabs__nav-wrap {
        width: calc(100vw - 30px) !important;
    } */

    .meg-workbook .el-tabs--border-card {
        box-shadow: unset !important;
    }


    .meg-workbook .el-tabs--border-card > .el-tabs__header.is-bottom {
        margin: unset !important;
    }

    .meg-workbook .el-tabs--border-card > .el-tabs__header > .el-tabs__nav-wrap > .el-tabs__nav-scroll > .el-tabs__nav > .el-tabs__item.is-bottom.is-closable{
        border-right-color: #dcdfe6;
    }

    .meg-workbook-btn {
        width: 200px;
        display: flex;
        height: 100%;
        /* padding: 5px; */
        justify-content: space-evenly;
        align-items: center;
        border-bottom: 1px solid #ddd;
    }
</style>
