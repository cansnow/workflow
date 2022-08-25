//右键菜单

const baseMenuItems = [
    {
        text: '新增',
        handler: 'addItem',
    },
    {
        text: '重命名',
        handler: 'renameItem',
    },
    {
        text: '删除',
        handler: 'deleteItem',
    },
    {
        text: '创建副本',
        handler: 'copyItem',
    }
];

const menuItems = baseMenuItems.concat([]);


import ContextMenu from '@/components/exceleditor/components/common/contextmenu/ContextMenu.vue';

export default {
    components: {
        ContextMenu,
    },
    data() {
        return {
            contextMenuState: false,
            curMenuItems: [],
            contextMenuPos: {
                bottom: '',
                left: '',
            },
            tempData: {},
        };
    },
    methods: {
        showDataMenu(top, left, data) {
            Object.assign(this.tempData, data);
            this.cm_showMenu(top, left, menuItems);
        },
        // showHeadMenu(type, top, left) {
        //     this.cm_showMenu(top, left, type == 'row' ? rowMenuItems : columnMenuItems);
        // },
        cm_showMenu(top, left, menuItems) {
            const rect = this.$el.getBoundingClientRect();
            this.contextMenuState = true;
            this.curMenuItems = menuItems;
            this.contextMenuPos = {
                bottom: `${top - rect.top}px`,
                left: `${left - rect.left}px`,
            };
        },
        clickItem(item) {
            if (this[item.handler]) {
                this[item.handler].call(this);
            } else {
                new Function(`with(this){
            ${item.handler}
        }`).call(this);
            }
        },
    },
};
