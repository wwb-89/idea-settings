Vue.component('vue-button', {
    props:['menuConfig'],
    template:"<div class='btn-wrap'>\n" +
        "           <div class='more-btn' v-show='menuConfig.my.show' @click='showTooltip = !showTooltip'>\n" +
        "               <i class='more-icon' v-show='!menuConfig.my.name'></i>\n" +
        "               <template v-if='menuConfig.my.name'>{{menuConfig.my.name}}</template>\n" +
        "           </div>\n" +
        "           <div class='tooltip' v-if='showTooltip'>\n" +
        "               <div class='menu-item' v-for='(item, index) in menuConfig.my.children' v-show='item.show' @click='callbackChildEvent(index)'>\n" +
        "                   <p>{{item.name}}</p>\n" +
        "                   <span class='triangle'></span>\n" +
        "               </div>\n" +
        "           </div>\n" +
        "         </div>\n",
    data: function () {
        return {
            show: false,
            showTooltip: false
        }
    },
    methods: {
        callbackChildEvent: function (index) {
            var $this = this;
            var child = $this.menuConfig.my.children[index];
            eval("("+ child.callback +")")
        }
    }
});