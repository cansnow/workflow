<template>
    <div tabIndex="999" class="meg-workbook" @parse="handleParse" @keydown="handleKeyMap">
        <div style="display: flex; align-items: center;">
            <Menus v-if="menu" class="meg-workbook-menu" ref="menus" style="max-width: 100%; flex: 1; flex-wrap: wrap; height: auto; min-height: 40px;"/>
            <div class="meg-workbook-btn">
                <el-button type="primary" size="small" @click="handlePreview">预览</el-button>
                <!-- <el-button type="default" @click="viewData">查看代码</el-button> -->
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
                style="height: 100%;"
                :style="sheetIndex == '_'+index ? '' : { position: 'absolute', top: `calc(100vh - ${menusHeigth}px - 52px - 60px)` }"
            >
                <Sheet
                    @selectCell="handleSelectCell"
                    @selectEnd="selectEnd"
                    style="height: 100%;"
                    @click-head="handleClickHead"
                    :ref="'sheet_'+index"
                    :options="sheet.data"
                    :sheetIndex="index"
                    :autoCreate="autoCreate"
                />
            </div>
        </div>
        <el-tabs type="border-card" :tab-position="tabPosition" v-model="sheetIndex" :editable="true" @edit="handleTabsEdit">
            <el-tab-pane :label="sheet.title" v-for="(sheet,index) in data" :key="'_'+index" :name="'_'+index">
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>
import Menus from './menu/Menu.vue';
import Sheet from './sheet/Sheet.vue';

import keyMap from './mixins/keyMap';
import templateData from './templateData';

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
        }
    },
    mixins: [keyMap],
    components: {
        Sheet,
        Menus,
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
        handlePreview() {
            this.$emit('handlePreview');
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
        // 新增sheet
        handleTabsEdit(targetName, action) {
            console.log(targetName, action);
            if (action == 'add') {
                const title = `sheet${this.data.length + 1}`;
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
