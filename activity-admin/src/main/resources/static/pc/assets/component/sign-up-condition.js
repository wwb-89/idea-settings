Vue.component("vue-sign-up-condition", {
    props: [],
    template: "<el-dialog title='配置条件' id='dialog' :visible.sync='show' :center=true width='456px' class='normal-dialog'>\n" +
        "           <el-form ref='form' class='set-form' onsubmit='return false'>\n" +
        "               <div class='form-item' v-for='(detail, index) in conditionDetails' :key='\"detail-\" + index'>\n" +
        "                   <div :style='labelStyleObj'>{{detail.fieldName}}</div>\n" +
        "                   <el-select v-model='detail.condition' size='medium' class='width120px' placeholder='请选择'>\n" +
        "                       <el-option v-for='(ce, ceIndex) in conditionEnums' :key='ceIndex'  :label='ce.name' :value='ce.value'></el-option>\n" +
        "                   </el-select>\n" +
        "                   <template v-if='detail.condition && detail.condition != \"empty\" && detail.condition != \"un_empty\"'>\n" +
        "                       <el-select v-if='detail.compt == \"selectbox\" || detail.compt == \"selectmultibox\"' v-model='detail.value' size='medium' class='width120px' placeholder='请选择'>\n" +
        "                           <el-option v-for='(it, itIndex) in detail.options' :key='itIndex' :label='it' :value='it'></el-option>\n" +
        "                       </el-select>\n" +
        "                   <el-input v-else v-model='detail.value' placeholder='请输入'size='medium' class='width120px'></el-input>\n" +
        "                   </template>\n" +
        // "                   <div class='icon-minus-circle delete-icon-style'></div>\n" +
        "              </div>\n" +
        // "               <div class='form-item'>\n" +
        // "                   <div class='plus-circle'><i class='icon-plus-circle'></i>添加条件</div>\n" +
        // "               </div>\n" +
        "           </el-form>\n" +
        "           <span slot='footer' class='dialog-footer center-footer'>\n" +
        "               <el-button class='btn-second-normal' @click='show =false'>取消</el-button>\n" +
        "               <el-button class='btn-main'  @click='confirm'>完成</el-button>\n" +
        "           </span>\n" +
        "   </el-dialog>",
    style: {

    },
    data: function () {
        return {
            show: false,
            conditionDetails: [],
            conditionEnums: [],
            labelStyleObj: {
                'height': '32px',
                'line-height': '32px',
                'width': '40px',
                'font-size': '14px',
                'margin-right': '5px'
            }
        }
    },
    methods: {
        showWindow: function (conditionDetails, conditionEnums) {
            var $this = this;
            $this.conditionDetails = $.extend(true, [], conditionDetails);
            $this.conditionEnums = $.extend(true, [], conditionEnums);

            $this.show = true;
        },
        confirm: function () {
            var $this = this;
            $($this.conditionDetails).each(function () {
                // 条件是不限、为空、不为空, 清空value
                if (!this.condition || this.condition == 'empty' || this.condition == 'un_empty') {
                    this.value = "";
                }
            });
            $this.$emit("callback", $this.conditionDetails);
            $this.show = false;
        }
    }
})