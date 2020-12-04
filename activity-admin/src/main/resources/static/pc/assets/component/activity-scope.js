Vue.component('vue-activity-scope', {
    template: "<div class='dailog-box1' v-show='show'>\n" +
        "    <div class='dailog'>\n" +
        "        <div class='header'>\n" +
        "            <span>活动范围</span>\n" +
        "            <div @click='show = false'>\n" +
        "                <img :src='closeImgUrl' class='close'>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class='body'>\n" +
        "            <div class='tree'>\n" +
        "                <div class='tree-head'>\n" +
        "                    <span>选择发布的范围</span>\n" +
        "                    <div class='already'>已选<span style='margin-left: 5px;'>{{selectedFidNum}}</span></div>\n" +
        "                </div>\n" +
        "                <div class='tree-box'>\n" +
        "                    <div id='organizationTree' class='ztree'></div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class='footer'>\n" +
        "            <div class='normal-btn cancle' @click='show = false'>取消</div>\n" +
        "            <div class='normal-btn after-sure' @click='sureCallback'>发布</div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>",
    data: function () {
        return {
            ctx: ctx,
            closeImgUrl: ctx + "/pc/assets/images/close.png",
            show: false,
            orgs: [],
            loaded: false,
            zTree: null,
            selectedFids: [],
            selectedFidNum: 0,
            activityId: ""
        }
    },
    mounted: function () {
        var $this = this;
    },
    methods: {
        reload: function () {
            var $this = this;
            if (!activityApp.isEmpty($this.activityId)) {
                $this.loaded = false;
                $this.clearSelect();
                $this.loadOrgs();
            }
        },
        showWindow: function (activityId) {
            var $this = this;
            $this.activityId = activityId;
            $this.reload();
            // 没有加载完不显示
            if ($this.loaded && $this.orgs.length > 0) {
                $this.show = true;
            }
        },
        // 加载机构列表
        loadOrgs: function () {
            var $this = this;
            var url = ctx + "/api/regional-architecture";
            app.ajaxPostWithLoading(url, {activityId: $this.activityId}, function (data) {
                if (data.success) {
                    $this.loaded = true;
                    $this.orgs = $this.supportOnlySelectParentNodeHandle(data.data);
                    // 处理下数
                    $this.initTree();
                    if ($this.orgs.length < 1) {
                        $this.sureCallback();
                    } else {
                        $this.show = true;
                    }
                } else {
                    var errorMessage = data.message;
                    if (activityApp.isEmpty(errorMessage)) {
                        errorMessage = "加载发布架构失败";
                    }
                    app.showMsg("加载发布架构失败");
                    console.log(errorMessage);
                }
            }, function () {
                app.showMsg("加载发布架构失败");
                console.log("加载发布架构失败");
            });
        },
        // 支持只选择父节点处理
        supportOnlySelectParentNodeHandle: function (orgs) {
            var result = [];
            var pids = [];
            $(orgs).each(function () {
                pids.push(this.pid);
                this.virtualId = this.id;
            });
            $(orgs).each(function () {
                var org = this;
                result.push(this);
                if (pids.indexOf(org.id) > -1) {
                    // 有下级
                    var newOrg = $.extend({}, org);
                    newOrg.virtualId = null;
                    newOrg.pid = org.id;
                    result.push(newOrg);
                }
            });
            return result;
        },
        initTree: function () {
            var $this = this;
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true,
                        idKey: "virtualId",
                        pIdKey: "pid",
                        rootPId: 0
                    }
                },
                callback: {
                    onCheck: function () {
                        $this.onOrgHierachyCheck();
                    }
                },
                view: {
                    showIcon: true,
                }
            };
            $.fn.zTree.init($("#organizationTree"), setting, $this.orgs);
            $this.zTree = $.fn.zTree.getZTreeObj("organizationTree");
        },
        onOrgHierachyCheck: function () {
            var $this = this;
            var nodes = $this.zTree.getCheckedNodes(true);
            $this.selectedFidNum = 0;
            $(nodes).each(function () {
                if (!activityApp.isEmpty(this.virtualId)) {
                    $this.selectedFidNum++;
                }
            });
        },
        // 清空选择
        clearSelect: function () {
            var $this = this;
            if ($this.zTree) {
                $this.zTree.checkAllNodes(false);
                $this.zTree.expandAll(false);
                $this.selectedFids = [];
                $this.selectedFidNum = 0;
            }
        },
        sureCallback: function () {
            var $this = this;
            $this.show = false;
            $this.selectedFids = [];
            if ($this.orgs && $this.orgs.length > 0) {
                // 获取选中
                var checkedNodes = $this.zTree.getCheckedNodes(true);
                $(checkedNodes).each(function () {
                    if (!activityApp.isEmpty(this.virtualId)) {
                        $this.selectedFids.push(this.fid);
                    }
                });
                if ($this.selectedFids.length < 1) {
                    app.showMsg("请选择发布范围");
                    $this.show = true;
                    return false;
                }
            }
            $this.$emit("callback");
        }
    }
});