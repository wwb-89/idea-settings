Vue.component('vue-activity-scope', {
    template: `
        <div class="dailog-box1" v-show="show">
            <div class="dailog">
                <div class="header">
                    <span>活动范围</span>
                    <div class="close" @click="show = false">
                        <img :src="ctx + '/pc/assets/images/close.png'">
                    </div>
                </div>
                <div class="body">
                    <div class="tree">
                        <div class="tree-head">
                            <span>选择发布的范围</span>
                            <div class="already">已选<span style="margin-left: 5px;">{{selectedFidNum}}</span></div>
                        </div>
                        <div class="tree-box">
                            <div id="organizationTree" class="ztree"></div>
                        </div>
                    </div>
                </div>
                <div class="footer">
                    <div class="normal-btn cancle" @click="show = false">取消</div>
                    <div class="normal-btn after-sure" @click="sureCallback">发布</div>
                </div>
            </div>
        </div>
    `,
    data: function () {
        return {
            ctx: ctx,
            show: false,
            orgs: [],
            loaded: false,
            zTree: null,
            selectedFids: [],
            selectedFidNum: 0
        }
    },
    mounted: function () {
        var $this = this;
        $this.loadOrgs();
    },
    watch: {
        show: function () {
            var $this = this;
            $this.clearSelect();
            if ($this.show && $this.loaded && $this.orgs.length < 1) {
                $this.sureCallback();
            }
        }
    },
    methods: {
        // 加载机构列表
        loadOrgs: function () {
            var $this = this;
            var url = ctx + "/api/regional-architecture";
            app.ajaxPost(url, {}, function (data) {
                $this.loaded = true;
                if (data.success) {
                    $this.orgs = data.data;
                    $this.initTree();
                } else {
                    var errorMessage = data.message;
                    if (activityApp.isEmpty(errorMessage)) {
                        errorMessage = "加载微服务层级架构失败";
                    }
                    console.log(errorMessage);
                }
            }, function () {
                $this.loaded = true;
                console.log("加载微服务层级架构失败");
            });
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
                        idKey: "id",
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
                    showIcon: false,
                }
            };
            $.fn.zTree.init($("#organizationTree"), setting, $this.orgs);
            $this.zTree = $.fn.zTree.getZTreeObj("organizationTree");
        },
        onOrgHierachyCheck: function () {
            var $this = this;
            var nodes = $this.zTree.getCheckedNodes(true);
            $this.selectedFidNum = nodes.length;
        },
        // 清空选择
        clearSelect: function () {
            var $this = this;
            $this.zTree.checkAllNodes(false);
            $this.zTree.expandAll(false);
            $this.selectedFids = [];
            $this.selectedFidNum = 0;
        },
        sureCallback: function () {
            var $this = this;
            $this.show = false;
            $this.selectedFids = [];
            if ($this.orgs && $this.orgs.length > 0) {
                // 获取选中
                var checkedNodes = $this.zTree.getCheckedNodes(true);
                $(checkedNodes).each(function () {
                    $this.selectedFids.push(this.fid);
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