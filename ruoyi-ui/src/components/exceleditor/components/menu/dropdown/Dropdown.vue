<template>
    <div class="meg-menu-dropdown">
        <div class="meg-menu-dropdown-handler" @click="handlerClick">
            <slot></slot>
            <span class="meg-menu-arrowdown"></span>
        </div>
        <div class="meg-dr-content" v-show="show">
            <slot name="content"></slot>
        </div>
    </div>
</template>
<script>
import Popper from 'popper.js'
export default {
    data() {
        return {
            show: false
        }
    },
    watch: {
        show() {
            if (this.show) {
                this.showConent()
            }
        }
    },
    methods: {
        handlerClick() {
            this.show = !this.show;
        },
        showConent() {
            if (this.popper) {
                this.popper.update();
            } else {
                const ref = this.$el.querySelector('.meg-menu-dropdown-handler');
                const content = this.$el.querySelector('.meg-dr-content');

                this.popper = new Popper(ref, content, {
                    gpuAcceleration: false,
                    placement: 'bottom-start',
                    boundariesPadding: 0,
                    forceAbsolute: true,
                    boundariesElement: 'body',
                });

                const body = document.querySelector('body');
                body.append(this.$el.querySelector('.meg-dr-content'));
            }
            setTimeout(() => {
                document.addEventListener('click', this.hideContent)
            }, 10)
        },
        hideContent() {
            this.show = false;
            document.removeEventListener('click', this.hideContent)
        }
    }
}
</script>
<style lang="scss">
.meg-menu-dropdown {
    display: inline-block;

    .meg-menu-dropdown-handler {
        font-size: 0px;
    }

    .meg-menu-arrowdown {
        display: inline-block;
        width: 16px;
        height: 16px;
        cursor: pointer;
        background: url('../../../assets/icon_down-arrow.png') no-repeat;
        opacity: 0.7;
    }
}
.meg-dr-content {
    z-index: 2015;
    font-size: 12px;
    color: #666;
}
</style>
