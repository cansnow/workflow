<template>
    <div tabIndex="999" class="meg-workbook" @parse="handleParse" @keydown="handleKeyMap">
        <Menus v-if="menu" class="meg-workbook-menu" ref="menus" style="max-width: 100%; flex-wrap: wrap; height: auto; min-height: 40px;"/>
        <el-tabs type="border-card" :tab-position="tabPosition" v-model="sheetIndex" :addable="true">
            <el-tab-pane :label="sheet.title" v-for="(sheet,index) in data" :key="'_'+index" :name="'_'+index">
                <Sheet
                    @selectCell="handleSelectCell"
                    @selectEnd="selectEnd"
                    :style="{ height: `calc(100vh - ${menusHeigth}px - 40px - 60px)` }"
                    :ref="'sheet_'+index"
                    :options="sheet.data"
                    :sheetIndex="index"
                    :autoCreate="autoCreate"
                />
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>
import Menus from './menu/Menu.vue';
import Sheet from './sheet/Sheet.vue';

import keyMap from './mixins/keyMap';

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
            sheetIndex:'_0',
            tabPosition:'bottom',
            menusHeigth: 40,
            screenWidth: 0,
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
                // debugger;
                this.screenWidth = document.body.clientWidth
            })()
        };
        this.menusHeigth = this.$refs.menus.$el.offsetHeight;
    },
    watch: {
        screenWidth(newValue) {
            console.log('newValue', newValue);
            this.setMenusHeigth();
        },
    },
    methods: {
        init() {
            let _this = this;
            _this.update(this.$piniastore.$state);
            this.$piniastore.$subscribe((mutation, state) => {
                _this.update(state);
            })
        },
        update(state) {
            this.data = state.data;
        },
        getCurSheet() {
            return this.$refs['sheet'+this.sheetIndex];
        },
        handleSelectCell() {
            this.$emit('selectCell');
        },
        selectEnd() {
            this.$emit('selectEnd');
        },
        setMenusHeigth() {
            this.menusHeigth = this.$refs.menus.$el.offsetHeight;
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
</style>
