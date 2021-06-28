Vue.component('vue-sign-map', {
    props:["mapDomId", "title", "uid", "fid"],
    template: "<div class='dailog-box1' v-show='show'>\n" +
        "    <div class='dailog map-dailog'>\n" +
        "        <div class='header'>\n" +
        "            <span>{{title}}</span>\n" +
        "            <div @click='show = false'>\n" +
        "                <img :src='closeImgUrl' class='close'>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div class='body'>\n" +
        "            <div class='body-head'>\n" +
        "                <div class='input-addr'>\n" +
        "                    <el-autocomplete style='width:100%;' popper-class='autoAddressClass' v-model.trim='address' :fetch-suggestions='querySearchAsync' :trigger-on-focus='false' placeholder='请输入地址' @select='handleSelect' clearable>\n" +
        "                        <template slot-scope='scope'> <i class='el-icon-search fl mgr10'></i>\n" +
        "                            <div class='address-box'>\n" +
        "                                <div class='title1'>{{ scope.item.title }}</div>\n" +
        "                                --\n" +
        "                                <span class='address ellipsis'>{{ scope.item.address }}</span>\n" +
        "                            </div>\n" +
        "                            <i class='el-icon-close' @click='deleteHistory(scope.item, $event)'></i>\n" +
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
            selectedItem: null,
            // 经度
            longitude: null,
            // 维度
            dimension: null,
            // 地图实例
            map: '',
            // Marker实例
            mk: '',
            // 历史记录
            histories: [],
            cb: null,
            inited: false
        }
    },
    created: function () {
        var $this = this;
        $this.loadHistory();
    },
    watch: {
        "show": function () {
            var $this = this;
            if (!$this.show) {
                $this.address = "";
                $this.longitude = null;
                $this.dimension = null;
                activityApp.resetScroll();
            } else {
                if (!$this.inited) {
                    $this.$nextTick(function () {
                        $this.initMap();
                    });
                }
                activityApp.initScroll();
            }
        }
    },
    methods: {
        loadHistory: function () {
            var $this = this;
            var url = ctx + "/api/sign/sign-in/position-history";
            $this.histories = [];
            var params = {
                uid: $this.uid,
                fid: $this.fid
            };
            app.ajaxPost(url, params, function (data) {
                if (data.success) {
                    var histories = data.data;
                    if (!activityApp.isEmpty(histories)) {
                        $(histories).each(function () {
                            if (!activityApp.isEmpty(this)) {
                                $this.histories.push(activityApp.getJsonObject(this));
                            }
                        });
                        // 选中第一个
                        var item = activityApp.getJsonObject(histories[0]);
                        $this.address = item.address + item.title;
                        $this.longitude = item.point.lng;
                        $this.dimension = item.point.lat;
                        $this.selectedItem = item;
                    }
                    $this.querySearchAsync("", $this.cb);
                }
            }, function () {

            });
        },
        addHistory: function (jsonStr) {
            var $this = this;
            var url = ctx + "/api/sign/sign-in/position-history/add";
            var params = {
                uid: $this.uid,
                fid: $this.fid,
                jsonStr: jsonStr
            };
            app.ajaxPost(url, params, function (data) {
                if (data.success) {
                    $this.loadHistory();
                }
            }, function () {

            });
        },
        deleteHistory: function (json, e) {
            var $this = this;
            e.stopPropagation();
            var url = ctx + "/api/sign/sign-in/position-history/delete";
            var params = {
                uid: $this.uid,
                fid: $this.fid,
                jsonStr: activityApp.getJsonStr(json)
            };
            app.ajaxPost(url, params, function (data) {
                if (data.success) {
                    $this.loadHistory();
                }
            }, function () {

            });
        },
        // 初始化百度地图
        initMap: function () {
            var $this = this;
            // 新建地图实例，enableMapClick:false ：禁用地图默认点击弹框
            this.map = new BMap.Map($this.mapDomId, {
                enableMapClick: false
            });
            var point = new BMap.Point(116.41338729034514000, 39.91092364795759600);
            this.map.centerAndZoom(point, 11);
            // 启用滚轮放大缩小，默认禁用
            this.map.enableScrollWheelZoom(true);
            this.mk = new BMap.Marker(point);
            this.map.addOverlay(this.mk);
            // 给地图绑定点击事件
            this.map.addEventListener('click', function (e) {
                // 点击后调用逆地址解析函数
                $this.getAddrByPoint(e.point);
            });
            $this.inited = true;
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
            var $this = this;
            $this.cb = cb;
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
            if (activityApp.isEmpty(str)) {
                // 加载历史
                if (cb) {
                    cb($this.histories);
                }
            } else {
                // 创建LocalSearch构造函数
                var local = new BMap.LocalSearch(this.map, options);
                // 调用search方法，根据检索词str发起检索
                local.search(str);
            }
        },
        handleSelect: function (item) {
            var $this = this;
            $this.selectedItem = item;
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
            if (activityApp.isEmpty($this.address)) {
                return;
            }
            $this.addHistory(activityApp.getJsonStr($this.selectedItem));
            $this.$emit("callback");
            $this.show = false;
        }
    }
});