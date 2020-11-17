// map工具类
var MapUtil = {
    create: function (domainId) {
        var mapUtil = {};
        mapUtil.address = "";
        mapUtil.longitude = "";
        mapUtil.dimension = "";
        mapUtil.mk = "";

        mapUtil.map = new BMap.Map(domainId, {
            enableMapClick: false
        });
        var point = new BMap.Point(104.10194, 30.65984);
        mapUtil.map.centerAndZoom(point, 19);
        // 启用滚轮放大缩小，默认禁用
        mapUtil.map.enableScrollWheelZoom(true);
        mapUtil.mk = new BMap.Marker(point);
        mapUtil.map.addOverlay(mapUtil.mk);
        // 给地图绑定点击事件
        mapUtil.map.addEventListener('click', function (e) {
            // 点击后调用逆地址解析函数
            mapUtil.getAddrByPoint(e.point);
        });
        mapUtil.getAddrByPoint = function () {
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
        };
        mapUtil.querySearchAsync = function (str, cb) {
            var $this = this;
            var options = {
                // 检索完成后的回调函数
                onSearchComplete: function (res) {
                    var s = [];
                    if ($this.local.getStatus() == BMAP_STATUS_SUCCESS) {
                        for (var i = 0; i < res.getCurrentNumPois(); i++) {
                            s.push(res.getPoi(i));
                        }
                        // 获取到数据时，通过回调函数cb返回到<el-autocomplete>组件中进行显示
                        cb(s);
                    } else {
                        cb(s)
                    }
                }
            }
            // 创建LocalSearch构造函数
            $this.local = new BMap.LocalSearch($this.map, options);
            // 调用search方法，根据检索词str发起检索
            $this.local.search(str);
        };
        mapUtil.handleSelect = function (item) {
            var $this = this;
            // 记录当前选中地址坐标
            // 清除地图上所有覆盖物
            $this.map.clearOverlays();
            // 根据所选坐标重新创建Marker
            $this.mk = new BMap.Marker(item.point);
            // 将覆盖物重新添加到地图中
            $this.map.addOverlay(this.mk);
            // 将地图的中心点更改为选定坐标点
            $this.map.panTo(item.point);
            // 记录详细地址，含建筑物名
            $this.address = item.address + item.title;
            $this.longitude = item.point.lng;
            $this.dimension = item.point.lat;
        };
        return mapUtil;
    }
};