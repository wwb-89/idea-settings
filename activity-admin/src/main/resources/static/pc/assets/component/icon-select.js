Vue.component("vue-icon-select", {
    props: ["cloudDomain", "icons"],
    template: "<div class='dialog-mask selectIconPop' v-show='show'>\n" +
        "            <div class='dailog'>\n" +
        "                <div class='header'>\n" +
        "                    <span>选择图标</span>\n" +
        "                    <i class='close' @click='show = false'></i>\n" +
        "                </div>\n" +
        "                <div class='body'>\n" +
        "                    <ul class='icon-lists'>\n" +
        "                        <li :class='{\"icon-list\": true, \"active\":  (index == iconIndex) }' v-for='(item, index) in (icons || [])' :key='\"icon-\" + index' @click='chooseIcon(index)'>\n" +
        "                            <img :src='cloudDomain + \"/star3/origin/\" + item.defaultIconCloudId' alt=''>\n" +
        "                        </li>\n" +
        "                    </ul>\n" +
        "                </div>\n" +
        "                <div class='footer'>\n" +
        "                    <div class='btn-box'>\n" +
        "                        <div class='border-btn' @click='closeIconDialog'>取消</div>\n" +
        "                        <div class='normal-btn' @click='confirmIconChoose'>确定</div>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>",
    data: function () {
        return {
            show: false,
            iconIndex: -1,

        }
    },
    methods: {
        showWindow: function (iconId) {
            var $this = this;
            $this.icons = $this.icons || [];
            for (var i = 0; i < $this.icons.length; i++) {
                if ($this.icons[i].id == iconId) {
                    $this.iconIndex = i;
                        break;
                }
            }
            if ($this.iconIndex == -1) {
                $this.iconIndex = 0;
            }
            $this.show = true;
        },
        chooseIcon: function (index) {
            var $this = this;
            $this.iconIndex = index;
        },
        // 关闭图标选择弹窗
        closeIconDialog: function () {
            var $this = this;
            $this.iconIndex = -1;
            $this.show = false
        },
        // 确认图标选择
        confirmIconChoose: function () {
            var $this = this;
            $this.$emit("callback", $this.iconIndex == -1 ? null : $this.icons[$this.iconIndex]);
            $this.closeIconDialog();
        },
    }
})