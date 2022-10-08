export default {
    methods: {
        //设置边框
        setBorder(border) {
            this.setSelctionExpandStyle({ border });
        },
        // 设置边框样式
        setBorderStyle(borderStyle) {
            this.setSelctionExpandStyle({ borderStyle });
        },
        //设置背景颜色
        setBackground(backgroundColor) {
            this.setSelctionExpandStyle({ backgroundColor });
        },
        // 设置边框颜色
        setBorderColor(borderColor) {
            this.setSelctionExpandStyle({ borderColor });
        },
        // 设置边框尺寸
        setBorderBold(borderBold) {
            this.setSelctionExpandStyle({ borderBold });
        },
        //设置前景颜色
        setFillColor(color) {
            this.setSelctionExpandStyle({ color });
        },
        //设置字体种类
        setFontWeight(fontWeight) {
            this.setSelctionExpandStyle({ fontWeight });
        },
        //设置字体种类
        setFontFamily(fontFamily) {
            this.setSelctionExpandStyle({ fontFamily });
        },
        //设置字体大小
        setFontSize(fontSize) {
            this.setSelctionExpandStyle({ fontSize });
        },
        //设置字体样式
        setFontStyle(fontStyle) {
            this.setSelctionExpandStyle({ fontStyle });
        },
        //下划线 删除线
        setTextDecoration(textDecoration) {
            this.setSelctionExpandStyle({ textDecoration });
        },
        setTextDel(textDel) {
            this.setSelctionExpandStyle({ textDel });
        },
        //设置水平对齐方式
        setTextAlign(textAlign) {
            this.setSelctionExpandStyle({ textAlign });
        },
        //设置垂直对齐方式
        setVerticalAlign(verticalAlign) {
            this.setSelctionExpandStyle({ verticalAlign });
        },
        setSelctionExpandStyle(option) {
            this.setAreaStyle(this.selctionExpand, style => {
                const temp = option;
                // 设置边框加减
                if (!!style.option.border && !!option.border) {
                    const border = style.option.border;
                    if (option.border.length == 1) {
                        if (border.includes(option.border)) {
                            Object.assign(temp, { border: border.replace(option.border, '') });
                        } else {
                            Object.assign(temp, { border: border +  option.border});
                        }
                    }
                }
                if (!!option.border) {
                    // 保留边框副本
                    Object.assign(temp, { borderSrc: temp.border });
                }
                style.setOption(temp);
            });
        },
        //设置单元格格式化
        setCellFormat() {
            this.stopAction();
        },
        stopAction() {
            this.curAction = null;
        },
        //设置单元格格式化
        setFormat(format) {
            this.setFormatCell(this.selctionExpand.start, format);
        },
        // 增加小数点
        // 减少小数点
        setDecimal(type) {
            this.setNumberDecimal(this.selctionExpand.start, type);
        },
        setFormaluValue(type) {
            let cell = this.getPosCell(this.selctionExpand.start);
            this.setCellAttribute(this.selctionExpand.start, cell, 'f', '=' + type + '()');
        },
        // 清空样式
        resetStyle() {
            let cell = this.getPosCell(this.selctionExpand.start);
            if (typeof cell.s != 'undefined') {
                this.setCellAttribute(this.selctionExpand.start, cell, 's', undefined);
            }
        },
    },
};
