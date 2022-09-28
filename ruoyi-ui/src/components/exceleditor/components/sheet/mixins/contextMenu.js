//右键菜单

const baseMenuItems = [{
    text: '剪切',
    handler: 'doCutSelection',
}, {
    text: '复制',
    handler: 'doCopySelection',
}, {
    text: '粘贴',
    handler: 'doPasteSelection',
    divided: true,
}];

const cellMenuItems = baseMenuItems.concat([{
    text: '合并单元格',
    handler: 'doMergeCell',
}, {
    text: '边框',
    handler: 'setBorder',
    children: [{
        text: '所有边框',
        handler: 'setBorder("blrt")',
    }, {
        text: '无边框',
        handler: 'setBorder',
    }, {
        text: '上边框',
        handler: 'setBorder("lt")',
    }, {
        text: '右边框',
        handler: 'setBorder("r")',
    }, {
        text: '下边框',
        handler: 'setBorder("b")',
    }, {
        text: '左边框',
        handler: 'setBorder("l")',
    }],
}]);

const rowMenuItems = baseMenuItems.concat([{
    text: '插入行',
    handler: 'doInsterRow',
}, {
    text: '删除行',
    handler: 'doDelRow',
}, {
    text: '设置行高',
    handler: 'setRowHeightMenu',
    type: 'row',
}, {
    text: '隐藏行',
    handler: 'doHideRow',
}, {
    text: '取消隐藏行',
    handler: 'doUnHideRow',
}]);

const columnMenuItems = baseMenuItems.concat([{
    text: '插入列',
    handler: 'doInsterColumn',
}, {
    text: '删除列',
    handler: 'doDelColumn',
}, {
    text: '设置列宽',
    handler: 'setColumnHeightMenu',
    type: 'column',
}, {
    text: '隐藏列',
    handler: 'doHideColumn',
}, {
    text: '取消隐藏列',
    handler: 'doUnHideColumn',
}]);


import ContextMenu from '../../common/contextmenu/ContextMenu.vue';

export default {
    components: {
        ContextMenu,
    },
    data() {
        return {
            contextMenuState: false,
            curMenuItems: [],
            contextMenuPos: {
                top: '',
                left: '',
            },
            curH: 0,
            curW: 0,
            itemMenuInfo: {},
        };
    },
    methods: {
        showCellMenu(pos, top, left) {
            if (!this.s_inSelectArea(pos)) {
                this.setSelectArea(pos, pos);
            }
            this.cm_showMenu(top, left, cellMenuItems);
        },
        showHeadMenu(type, top, left) {
            if (type == 'row') {
                if (!!this.rows[this.selctionExpand.start.rowIndex]) {
                    this.curH = this.rows[this.selctionExpand.start.rowIndex].hpx;
                } else {
                    this.curH = 20;
                }                
                const rowMenuIndex = rowMenuItems.findIndex(rowM => !!rowM.type);
                if (rowMenuIndex != -1) {
                    rowMenuItems.splice(rowMenuIndex, 1, Object.assign(rowMenuItems[rowMenuIndex], { value: this.curH }));
                }
            } else {
                if (!!this.columns[this.selctionExpand.start.columnIndex]) {
                    this.curW = this.columns[this.selctionExpand.start.columnIndex].wpx;
                } else {
                    this.curW = 64;
                }
                const colMenuIndex = columnMenuItems.findIndex(colM => !!colM.type);
                if (colMenuIndex != -1) {
                    columnMenuItems.splice(colMenuIndex, 1, Object.assign(columnMenuItems[colMenuIndex], { value: this.curW }));
                }
            }
            this.cm_showMenu(top, left, type == 'row' ? rowMenuItems : columnMenuItems);
        },
        cm_showMenu(top, left, menuItems) {
            const rect = this.$el.getBoundingClientRect();
            this.contextMenuState = true;
            this.curMenuItems = menuItems;
            this.contextMenuPos = {
                top: `${top + this.scrollTop - rect.top}px`,
                left: `${left + this.scrollLeft - rect.left}px`,
            };
        },
        clickItem(item) {
            this.itemMenuInfo = item;
            if (this[item.handler]) {
                this[item.handler].call(this);
            } else {
                new Function(`with(this){
            ${item.handler}
        }`).call(this);
            }
        },
        // 设置行高
        setRowHeightMenu() {
            this.setRowHeight(this.selctionExpand.start.rowIndex, this.itemMenuInfo.value);
            this.contextMenuState = true;
        },
        // 设置列高
        setColumnHeightMenu() {
            this.setColumnWidth(this.selctionExpand.start.columnIndex, this.itemMenuInfo.value);
            this.contextMenuState = true;
        },
    },
};
