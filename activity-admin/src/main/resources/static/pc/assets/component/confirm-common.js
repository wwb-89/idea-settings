Vue.component('vue-confirm-common', {
    props: ['message', 'sure', 'cancel', 'reverse'],
    template: "<div class='dialog-box' v-show='show'>\n" +
        "    <div class='confirm_dialog'>\n" +
        "        <span class='icon'><img :src='warnImgUrl' class='warn'></span>\n" +
        "        <span class='text'>{{message}}</span>\n" +
        "        <div class='btn' v-if='reverse'>\n" +
        "            <span class='ok' style='margin-right: 24px' @click='sureCallback'>{{sure}}</span>\n" +
        "            <span class='cancel' style='margin-right: 0' @click='show = false'>{{cancel}}</span>\n" +
        "        </div>\n" +
        "        <div class='btn' v-else>\n" +
        "            <span class='cancel' @click='show = false'>{{cancel}}</span>\n" +
        "            <span class='ok' @click='sureCallback'>{{sure}}</span>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>",
    data: function () {
        return {
            ctx: ctx,
            warnImgUrl: ctx + "/pc/assets/images/warning.png",
            show: false
        }
    },
    methods: {
        sureCallback: function () {
            var $this = this;
            $this.show = false;
            $this.$emit("callback");
        }
    }
});