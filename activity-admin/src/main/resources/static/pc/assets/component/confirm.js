Vue.component('vue-confirm', {
    props: ['message', 'sure', 'cancel'],
    template: "<div class='dailog-box1' v-show='show'>\n" +
        "    <div class='dailog delete-dailog'>\n" +
        "        <img :src='warnImgUrl' class='warn'>\n" +
        "        <span>{{message}}</span>\n" +
        "        <div>\n" +
        "            <div class='normal-btn cancle' @click='show = false'>{{cancel}}</div>\n" +
        "            <div class='normal-btn after-sure' @click='sureCallback'>{{sure}}</div>\n" +
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