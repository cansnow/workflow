<template>
	<Group name="字体">
		<el-select v-model="fontFamily" placeholder="字体" @input="setFontFamily" :filterable="true">
			<el-option v-for="item in fontFamilyOptions" :key="item" :label="item" :value="item"></el-option>
		</el-select>

		<el-select v-model="fontSize" placeholder="字号" @input="setFontSize" :filterable="true">
			<el-option v-for="item in fontSizeOptions" :key="item" :label="item" :value="item"></el-option>
		</el-select>

		<!-- <button class="meg-menu-op" icon="growfont16" title="增大字号" @click="setFontPlus"></button>
			<button class="meg-menu-op" icon="shrinkfont16" title="减小字号" @click="setFontMinus"></button> -->
		<button type="button" @click="setFontWeight" :class="{ selected: fontWeight }"><i class="mdi mdi-format-bold" title="加粗"></i></button>

		<button type="button" @click="setFontStyle" :class="{ selected: fontStyle }"><i class="mdi mdi-format-italic" title="倾斜"></i></button>

		<button type="button" @click="setTextDecoration('underline')" :class="{ selected: textDecoration == 'underline' }">
			<i class="mdi mdi-format-underline" title="下划线"></i>
		</button>

		<button type="button" @click="setTextDecoration('line-through')" :class="{ selected: textDecoration == 'line-through' }">
			<i class="mdi mdi-format-strikethrough-variant" title="删除线"></i>
		</button>

		<Dropdown class="meg-menu-op">
			<i @click="setBorder" class="mdi mdi-border-all"></i>
			<m-Select slot="content" v-model="borderType" :options="borderOptions" @input="setBorder"></m-Select>
		</Dropdown>

		<Dropdown class="meg-menu-op meg-menu-op-color">
			<i class="mdi mdi-format-color-fill" @click="setBgColor"></i>
			<span class="meg-menu-pcol" :style="{ background: bgColor }" @click="setBgColor"></span>
			<Colorpick slot="content" v-model="bgColor" @input="setBgColor"></Colorpick>
		</Dropdown>

		<Dropdown class="meg-menu-op meg-menu-op-color">
			<i class="mdi mdi-format-text-variant" @click="setColor"></i>
			<span class="meg-menu-pcol" :style="{ background: color }" @click="setColor"></span>
			<Colorpick slot="content" v-model="color" @input="setColor"></Colorpick>
		</Dropdown>
	</Group>
</template>
<script>
import AppGroup from './AppGroup.vue';
import Dropdown from '../dropdown/Dropdown.vue';
import mSelect from '../select/Select.vue';
import Colorpick from '../colorpick/Colorpick.vue';

export default {
	extends: AppGroup,
	components: {
		Dropdown,
		mSelect,
		Colorpick
	},
	data() {
		return {
			items: [{ key: 'fontSize', value: 11 }, { key: 'fontFamily', value: '宋体' }, 'fontWeight', 'fontStyle', 'textDecoration'],

			fontSize: 11,
			fontSizeOptions: [8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 26, 28, 36, 48, 72],

			fontFamily: '宋体',
			fontFamilyOptions: ['宋体', '楷体', '仿宋', '微软雅黑', '黑体', 'Calibri', 'Consolas'],

			color: '#ff1400',
			bgColor: '#ffff00',

			fontWeight: undefined,
			fontStyle: undefined,
			textDecoration: undefined,

			borderType: 't',
			borderOptions: [
				{
					text: '上边框',
					value: 't'
				},
				{
					text: '下边框',
					value: 'b'
				},
				{
					text: '左边框',
					value: 'l'
				},
				{
					text: '右边框',
					value: 'r'
				},
				{
					text: '无边框',
					value: ''
				},
				{
					text: '全边框',
					value: 'blrt'
				}
			]
		};
	},
	computed: {
		border() {
			return this.borderOptions.find(opt => opt.value == this.borderType); // _.find(this.borderOptions, { value: this.borderType })
		}
	},
	methods: {
		setBgColor() {
			this.$sheet.setBackground(this.bgColor);
		},
		setColor() {
			this.$sheet.setFillColor(this.color);
		},
		setBorder() {
			this.$sheet.setBorder(this.borderType);
		},
		setFontSize() {
			this.$sheet.setFontSize(this.fontSize);
		},
		setFontPlus() {
			this.fontSize++;
			this.setFontSize();
		},
		setFontMinus() {
			this.fontSize--;
			this.setFontSize();
		},
		setFontFamily() {
			this.$sheet.setFontFamily(this.fontFamily);
		},
		setFontWeight() {
			this.fontWeight = this.fontWeight ? undefined : 'bold';
			this.$sheet.setFontWeight(this.fontWeight);
		},
		setFontStyle() {
			this.fontStyle = this.fontStyle ? undefined : 'italic';
			this.$sheet.setFontStyle(this.fontStyle);
		},
		setTextDecoration(decoration) {
			this.textDecoration = this.textDecoration === decoration ? undefined : decoration;
			this.$sheet.setTextDecoration(this.textDecoration);
		}
	}
};
</script>
<style lang="scss">
.meg-font-family {
	width: 134px;
	padding: 2px;
}

.meg-font-family-sel {
	width: 156px;
}

.meg-font-size-sel {
	width: 52px;
}

.meg-font-size {
	padding: 2px;
	width: 30px;
}
</style>
