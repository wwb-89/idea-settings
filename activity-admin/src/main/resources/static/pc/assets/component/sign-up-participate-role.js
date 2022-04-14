Vue.component("sign-up-participate-role", {
    props: ["domId", "fid", "areaCode"],
    template: "<div class='dailog-box1' style='z-index: 999' v-show='show'>\n" +
        "        <div class='dailog'>\n" +
        "            <div class='header'>\n" +
        "                <span>请选择</span>\n" +
        "                <img :src='closeImgUrl' class='close' @click='cancelCallback'>\n" +
        "            </div>\n" +
        "            <div class='body'>\n" +
        "                <div class='tree'>\n" +
        "                    <div class='tree-box'>\n" +
        "                        <div :id='domId' class='ztree'></div>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div class='footer'>\n" +
        "                <div class='normal-btn cancle' @click='cancelCallback'>取消</div>\n" +
        "            <div class='normal-btn after-sure' @click='sureCallback'>确定</div>\n" +
        "    </div>\n" +
        "</div>\n" +
        "</div>",
    data: function () {
        return {
            show: false,
            ctx: ctx,
            zTree: null,
            roles: null,
            closeImgUrl: ctx + "/pc/assets/images/close.png",
        }
    },
    created: function () {
        var $this = this;
        $this.loadRole();
    },
    methods: {
        loadRole: function () {
            var $this = this;
            var url = ctx + "/api/org/" + $this.fid + "/roles";
            var params = {
                areaCode: $this.areaCode
            };
            app.ajaxPost(url, params, function (data) {
                if (data.success) {
                    $this.roles = data.data;
                    $this.$nextTick(function () {
                        $this.initTree();
                    });
                } else {

                }
            }, function () {
            });
        },
        initTree: function () {
            var $this = this;
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    key: {
                        isParent: "group"
                    },
                    simpleData: {
                        enable: true,
                        idKey: "id",
                        pIdKey: "roleGroupId",
                        rootPId: 0
                    }
                },
                callback: {
                    onCheck: function () {

                    }
                },
                view: {
                    showIcon: true,
                }
            };
            $.fn.zTree.init($("#" + $this.domId), setting, $this.roles);
            $this.zTree = $.fn.zTree.getZTreeObj($this.domId);
        },
        /**
         * @param roles role值列表
         */
        showWindow: function (roles) {
            var $this = this;
            // 清空选择
            if ($this.zTree) {
                $this.zTree.checkAllNodes(false);
                $this.zTree.expandAll(false);
                var nodes = $this.zTree.transformToArray($this.zTree.getNodes());
                $(nodes).each(function () {
                    if (roles.indexOf(this.role) > -1) {
                        $this.zTree.checkNode(this, true, true);
                    }
                });
                // 展开第一个
                if (nodes.length > 0) {
                    $this.zTree.expandNode(nodes[0], true, true, true);
                }
            }
            $this.show = true;
        },
        cancelCallback: function () {
            var $this = this;
            $this.show = false;
        },
        sureCallback: function () {
            var $this = this;
            var selectedRoles = [];
            var checkedNodes = $this.zTree.getCheckedNodes(true);
            $(checkedNodes).each(function () {
                if (!this.group) {
                    selectedRoles.push(this);
                }
            });
            $this.$emit("callback", selectedRoles);
            $this.show = false;
        }
    }
});