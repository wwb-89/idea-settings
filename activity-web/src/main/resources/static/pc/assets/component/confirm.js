Vue.component('vue-confirm', {
    props:["message", "sureBtnName", "cancelBtnName"],
    template: "<div class='dialog-box' v-show='show'>\n" +
        "    <div class='confirm_dialog'>\n" +
        "        <span class='icon'>\n" +
        "            <img :src='icon'>\n" +
        "        </span>\n" +
        "        <span class='text'>{{message}}</span>\n" +
        "        <div class='btn'>\n" +
        "            <span class='cancel' @click='show = false'>{{cancelName}}</span>\n" +
        "            <span class='ok' @click='sure'>{{sureName}}</span>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>",
    data: function () {
        return {
            ctx: ctx,
            icon: ctx + "/pc/assets/images/icon-30-warning.png",
            show: false,
            sureName: "确认",
            cancelName: "取消"
        }
    },
    created: function () {
        var $this = this;
        if (!activityApp.isEmpty($this.sureBtnName)) {
            $this.sureName = $this.sureBtnName;
        }
        if (!activityApp.isEmpty($this.cancelBtnName)) {
            $this.cancelName = $this.cancelBtnName;
        }
    },
    methods: {
        sure: function () {
            var $this = this;
            $this.$emit("callback");
            $this.show = false;
        }
    }
});