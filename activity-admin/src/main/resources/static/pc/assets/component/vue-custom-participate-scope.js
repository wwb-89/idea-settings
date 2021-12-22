Vue.component("vue-custom-participate-scope", {
    props: [],
    template: "<div class='dailog-box1' style='z-index: 999' v-show='show'>\n" +
        "        <div class='dailog'>\n" +
        "            <div class='header'>\n" +
        "                <span>请选择</span>\n" +
        "                <img :src='closeImgUrl' class='close' @click='cancelCallback'>\n" +
        "            </div>\n" +
        "            <div class='body'>\n" +
        "                <div class='tree'>\n" +
        "                    <div class='tree-box'>\n" +
        "                        <div id='custom-participate-scope-tree' class='ztree'></div>\n" +
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
            currWfwGroups: [],
            currGroupType: null,
            customParticipateScopeTreeObj: null,
            closeImgUrl: ctx + "/pc/assets/images/close.png",
        }
    },
    computed: {
        // 虚拟节点map，即非叶子节点复制本身作为其叶子节点，该叶子节点即为虚拟节点
        virtualNodeMap: function () {
            var $this = this;
            var nodeMap = {};
            $($this.currWfwGroups).each(function () {
                if (this.id !== this.virtualId) {
                    nodeMap[this.id] = this;
                }
            });
            return nodeMap;
        }
    },
    methods: {
        getTreeSetting: function () {
            var $this = this;
            $($this.currWfwGroups).each(function () {
                this.isParent = this.soncount > 0;
            })
            return {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true,
                        idKey: "virtualId",
                        pIdKey: "gid",
                    },
                    key: {
                        name: "groupname"
                    },
                    keep: {
                        parent: true
                    }
                },
                callback: {}
            };
        },
        initWfwGroups: function () {
            var $this = this;
            var nodeSoncountMap = {}
            $($this.currWfwGroups).each(function () {
                var _this = this;
                _this.chkDisabled = false;
                var actualSoncount = 0;
                $($this.currWfwGroups).each(function () {
                    if (_this.id !== this.id && _this.id === this.gid) {
                        actualSoncount++;
                    }
                });
                nodeSoncountMap[_this.id] = actualSoncount;
            });

            $($this.currWfwGroups).each(function () {
                // 若一个非叶子节点无自身的虚拟叶子节点，则该节点不可选中
                var nonLeaf = this.soncount && this.soncount !== 0;
                if ((nonLeaf && !$this.virtualNodeMap[this.id]) || (this.id === this.virtualId && this.soncount !== nodeSoncountMap[this.id])) {
                    this.chkDisabled = true;
                }
            });
        },
        initGroupTree: function (scopes) {
            var $this = this;
            var setting = $this.getTreeSetting();
            $this.customParticipateScopeTreeObj = $.fn.zTree.init($("#custom-participate-scope-tree"), setting, $this.currWfwGroups);
            // 获取所有父节点
            for(var i = 0; i < scopes.length; i++) {
                var item = scopes[i];
                $this.customParticipateScopeTreeObj.checkNode($this.customParticipateScopeTreeObj.getNodeByParam("virtualId", item.virtualId, null), true, !item.leaf);
            }
        },
        showWindow: function (groups, scopes, groupType) {
            var $this = this;
            $this.currWfwGroups = groups;
            $this.currGroupType = groupType || 'wfw';
            $this.initWfwGroups();
            if(!$this.customParticipateScopeTreeObj){
                $this.initGroupTree(scopes);
            }
            $this.show = true;
        },
        cancelCallback: function () {
            var $this = this;
            if ($this.customParticipateScopeTreeObj) {
                $this.customParticipateScopeTreeObj.destroy();
                $this.customParticipateScopeTreeObj = null;
            }
            $this.currWfwGroups = [];
            $this.$emit("cancel-callback");
            $this.show = false;
        },
        sureCallback: function () {
            var $this = this;
            $this.show = false;
            var zTree = $.fn.zTree.getZTreeObj("custom-participate-scope-tree");
            var nodeArr = zTree.getCheckedNodes(true);
            //首先保存 搜索父节点被全选的，在看非父节点被选中的(非父节点的父节点被全选，则不保存该条记录)
            var scopes = [];
            var pNodes = [];
            var pNodeMap = {};
            var cNodes = [];
            $(nodeArr).each(function () {
                // 半选状态
                var half = this.getCheckStatus().half;
                // 只记录全选的
                if (!half) {
                    var node = { externalId: this.id, virtualId: this.virtualId, externalPid: this.gid, externalName: this.groupname, leaf: !this.isParent };
                    // 如果是非叶子结点节点
                    if (this.isParent) {
                        pNodes.push(node);
                        pNodeMap[node.virtualId] = node;
                    } else {
                        cNodes.push(node);
                    }
                }
            });
            $(cNodes).each(function () {
                if (!this.externalPid || !pNodeMap[this.externalPid]) {
                    pNodes.push(this);
                    pNodeMap[this.virtualId] = this;
                }
            });
            $(pNodes).each(function () {
                if (!this.externalPid || !pNodeMap[this.externalPid]) {
                    scopes.push($.extend({}, this, {groupType: $this.currGroupType}));
                }
            });
            if (scopes.length < 1) {
                app.showMsg("请选择范围");
                $this.show = true;
            } else {
                $this.$emit("sure-callback", scopes);
                $this.cancelCallback();
            }
        }
    }
});