Vue.component('vue-map', {
    props:["mapDomId"],
    template: "<div class='dailog-box1' v-show='show'>\n" +
        "    <div class='dailog map-dailog'>\n" +
        "        <div class='header'>\n" +
        "            <span>设置地图</span>\n" +
        "            <div @click='show = false'>\n" +
        "                <img :src='closeImgUrl' class='close'>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class='body'>\n" +
        "            <div class='body-head'>\n" +
        "                <div class='input-addr'>\n" +
        "                    <el-autocomplete style='width:100%;' popper-class='autoAddressClass' v-model.trim='address' :fetch-suggestions='querySearchAsync' :trigger-on-focus='false' placeholder='请输入地址' @select='handleSelect' clearable>\n" +
        "                        <template slot-scope='{ item }'> <i class='el-icon-search fl mgr10'></i>\n" +
        "                            <div class='address-box'>\n" +
        "                                <div class='title1'>{{ item.title }}</div>\n" +
        "                                --\n" +
        "                                <span class='address ellipsis'>{{ item.address }}</span>\n" +
        "                            </div>\n" +
        "                        </template>\n" +
        "                    </el-autocomplete>\n" +
        "                </div>\n" +
        "                <div class='normal-btn after-sure' @click='sure'>确定</div>\n" +
        "            </div>\n" +
        "            <div :id='mapDomId' class='map'></div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>",
    data: function () {
        return {
            ctx: ctx,
            closeImgUrl: ctx + "/pc/assets/images/close.png",
            show: false,
            // 地址
            address: "",
            // 经度
            longitude: "",
            // 维度
            dimension: "",
            // 地图实例
            map: '',
            // Marker实例
            mk: ''
        }
    },
    mounted: function () {
        var $this = this;
        $this.initMap();
    },
    methods: {
        // 初始化百度地图
        initMap: function () {
            var $this = this;
            // 新建地图实例，enableMapClick:false ：禁用地图默认点击弹框
            this.map = new BMap.Map($this.mapDomId, {
                enableMapClick: false
            });
            var point = new BMap.Point(104.10194, 30.65984);
            this.map.centerAndZoom(point, 19);
            // 启用滚轮放大缩小，默认禁用
            this.map.enableScrollWheelZoom(true);
            this.mk = new BMap.Marker(point);
            this.map.addOverlay(this.mk);
            // 给地图绑定点击事件
            this.map.addEventListener('click', function (e) {
                // 点击后调用逆地址解析函数
                $this.getAddrByPoint(e.point);
            });
        },
        //逆地址解析
        getAddrByPoint: function (point) {
            var $this = this;
            var geco = new BMap.Geocoder();
            geco.getLocation(point, function (res) {
                // 重新设置标注的地理坐标
                $this.mk.setPosition(point);
                // 将地图的中心点更改为给定的点
                $this.map.panTo(point);
                // 记录该点的详细地址信息
                $this.address = res.address;
                // 记录当前坐标点
                $this.longitude = point.lng;
                $this.dimension = point.lat;
            })
        },
        querySearchAsync: function (str, cb) {
            var options = {
                // 检索完成后的回调函数
                onSearchComplete: function (res) {
                    var s = [];
                    if (local.getStatus() == BMAP_STATUS_SUCCESS) {
                        for (var i = 0; i < res.getCurrentNumPois(); i++) {
                            s.push(res.getPoi(i));
                        }
                        // 获取到数据时，通过回调函数cb返回到<el-autocomplete>组件中进行显示
                        cb(s);
                    } else {
                        cb(s);
                    }
                }
            }
            // 创建LocalSearch构造函数
            var local = new BMap.LocalSearch(this.map, options);
            // 调用search方法，根据检索词str发起检索
            local.search(str);
        },
        handleSelect: function (item) {
            var $this = this;
            // 记录当前选中地址坐标
            // 清除地图上所有覆盖物
            this.map.clearOverlays();
            // 根据所选坐标重新创建Marker
            this.mk = new BMap.Marker(item.point);
            // 将覆盖物重新添加到地图中
            this.map.addOverlay(this.mk);
            // 将地图的中心点更改为选定坐标点
            this.map.panTo(item.point);
            // 记录详细地址，含建筑物名
            this.address = item.address + item.title;
            $this.longitude = item.point.lng;
            $this.dimension = item.point.lat;
        },
        sure: function () {
            var $this = this;
            $this.$emit("callback");
            $this.show = false;
        }
    }
});