Vue.component('vue-confirm', {
    props: ['message', 'sure', 'cancel'],
    template: `
        <div class="dailog-box1" v-show="show">
            <div class="dailog delete-dailog">
                <img :src="ctx + '/pc/assets/images/warning.png'" class="warn">
                <span>{{message}}</span>
                <div>
                    <div class="normal-btn" @click="show = false">{{cancel}}</div>
                    <div class="normal-btn after-sure" @click="sureCallback">{{sure}}</div>
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
        sureCallback: function () {
            var $this = this;
            $this.$emit("callback");
        }
    }
});