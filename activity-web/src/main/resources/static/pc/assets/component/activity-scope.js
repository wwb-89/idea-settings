Vue.component('vue-activity-scope', {
    props: ['orgs'],
    template: `
        <div class="dailog-box1" v-show="show">
            <div class="dailog">
                <div class="header">
                    <span>活动范围</span>
                    <img :src="ctx + '/pc/assets/images/close.png'" class="close" @click="show = false">
                </div>
                <div class="body">
                    <div class="tree">
                        <div class="tree-head">
                            <span>选择发布的范围</span>
                            <div class="already">已选<span style="margin-left: 5px;">0</span></div>
                        </div>
                        <div class="tree-box">
                            <div id="organizationTree" class="ztree"></div>
                        </div>
                    </div>
                </div>
                <div class="footer">
                    <div class="normal-btn cancle" @click="show = false">取消</div>
                    <div class="normal-btn before-sure" @click="sureCallback">发布</div>
                </div>
            </div>
        </div>
    `,
    data: function () {
        return {
            ctx: ctx,
            show: false
        }
    },
    methods: {
        initTree: function () {
            var $this = this;
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onCheck: zTreeOnCheck
                },
                view: {
                    showIcon:false,
                }
            };
        },
        sureCallback: function () {
            var $this = this;
            $this.$emit("callback");
        }
    }
});