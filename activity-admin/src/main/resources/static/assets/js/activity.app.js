(function (W, $, J) {
    if (typeof W.activityApp != 'undefined') {
        return;
    }

    function activityApp() {
    }

    if (!window.location.origin) {
        activityApp.prototype.origin = window.location.protocol + "//" + window.location.hostname;
    } else {
        activityApp.prototype.origin = W.location.origin;
    }

    /** 需要展示线上线下活动类型的市场id列表 */
    activityApp.prototype.other_activity_type_marketids = [293, 305, 1149, 1267];
    /** 活动默认封面云盘id库 */
    activityApp.prototype.default_cover_cloud_id_repo = ['6918158e10d931e4b34c3a2005843343','cc6747c266990ae4623ee86b3580bdeb','a702ab20586128ee699c5b91be0ef327','334e2e1acc0e93a3e74e06266ef201b0','b13895d9cb4e8843ad73fbc2f98c5c20','b4b218b10d1f5482bb0cb21a04a79720','3c657a3e83e547547875ad77c5e650e7','18845df01f2b31a5230406071a2d26e7','c2f5af53ae71e6ade49b5349c8f3ad34','73c692ca3c6a8c596ac4353c933bdcce','c53deb05881621a5fd2fddd06c734f1f','6aee62c0290e2612d7a46ac90a53da89','776af507967361fb5385caf6c5e699b2','a843483e29c9e00b2bcd596e497838aa','54d1ada2a655689c15e2daa53072e996','9186c33388e760efa803768b3f9acb55','30ccf973c886947804b41da72296b276','bc46ba6f6729296779f41c06ebcaa40f','d42f5899c5f7761e0935c0036fa516d3','65cf2063eac37c48a09d0698da29c357','b4512fc47200ee1488a332d60ee99f32','fa6a3c3180ce04e49fe8835c4dd6c4d7','3431a538652d386e4902b6cab792ffe2'];

    /** 判断是否需要展示线上线下活动类型 */
    activityApp.prototype.showOtherActivityType = function (marketId) {
        var $this = this;
        return $this.other_activity_type_marketids.indexOf(marketId) != -1;
    }

    /**
     * 字符串是否为空
     * @param str
     */
    activityApp.prototype.isEmpty = function (str) {
        return typeof (str) == 'undefined' || str == null || "" === $.trim(str);
    };

    /**
     * 是否是手机号
     * @param phone
     * @returns {boolean}
     */
    activityApp.prototype.isMobile = function (phone) {
        return (/^1\d{10}$/.test(phone));
    };

    /**
     * 获取json对象
     * @param jsonStr
     */
    activityApp.prototype.getJsonObject = function (jsonStr) {
        if (this.isEmpty(jsonStr)) {
            return null;
        }
        return J.parse(jsonStr);
    };

    /**
     * 获取json字符串
     * @param jsonObject
     */
    activityApp.prototype.getJsonStr = function (jsonObject) {
        if (this.isEmpty(jsonObject)) {
            return null;
        }
        return J.stringify(jsonObject);
    };

    /**
     * 获取请求参数
     * @param name
     * @returns {*}
     */
    activityApp.prototype.getUrlParamter = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return decodeURI(r[2]);
        }
        return "";
    }

    /**
     * 时间戳转换为日期对象
     * @param millisecond
     * @returns {string|{month: (*|number), hour: any, year: number, day: any, minute: any, second: any}}
     */
    activityApp.prototype.millisecond2DateObj = function (millisecond) {
        var $this = this;
        if ($this.isEmpty(millisecond)) {
            return "";
        }
        var date = new Date(millisecond);
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        month = $this.fillSpecifiedDigits(month, 2, "0");
        var day = date.getDate();
        day = $this.fillSpecifiedDigits(day, 2, "0");
        var hour = date.getHours();
        hour = $this.fillSpecifiedDigits(hour, 2, "0");
        var minute = date.getMinutes();
        minute = $this.fillSpecifiedDigits(minute, 2, "0");
        var second = date.getSeconds();
        second = $this.fillSpecifiedDigits(second, 2, "0");
        return {
            year: year,
            month: month,
            day: day,
            hour: hour,
            minute: minute,
            second: second
        };
    };
    /**
     * 毫秒转换为日期字符串
     * @param millisecond
     */
    activityApp.prototype.millisecond2DateStr = function (millisecond) {
        var $this = this;
        var dateObj = $this.millisecond2DateObj(millisecond);
        if ($this.isEmpty(dateObj)) {
            return "";
        }
        return dateObj.year + "-" + dateObj.month + "-" + dateObj.day + " " + dateObj.hour + ":" + dateObj.minute + ":" + dateObj.second;
    };

    /**
     * 填充到指定位数
     * @param origin
     * @param digits
     * @param character
     */
    activityApp.prototype.fillSpecifiedDigits = function (origin, digits, character) {
        var lengthDiff = digits - (origin + "").length;
        if (lengthDiff > 0) {
            for (var i = 0; i < lengthDiff; i++) {
                origin = character + origin;
            }
        }
        return origin;
    };

    activityApp.prototype.isFunction = function (callback) {
        return typeof (callback) === "function";
    };

    /**
     * 获取cookie的值
     * @param key
     * @returns {*}
     */
    activityApp.prototype.getCookie = function (key) {
        var arr = document.cookie.match(new RegExp("(^| )" + key + "=([^;]*)(;|$)"));
        if (arr != null) return unescape(arr[2]);
        return null;
    };

    /**
     * 从cookie中获取uid
     * @returns {*}
     */
    activityApp.prototype.getUid = function () {
        var that = this;
        return that.getCookie("_uid")
    };

    /**
     * 将textarea的内容转换为html显示的内容（保留空格回车）
     * @param content
     * @returns {string | *}
     */
    activityApp.prototype.textareaToHtml = function (content) {
        var $this = this;
        if ($this.isEmpty(content)) {
            return content;
        }
        var reg = new RegExp("\n", "g");
        var regSpace = new RegExp(" ", "g");
        content = content.replace(reg, "<br>");
        content = content.replace(regSpace, "&nbsp;");
        return content;
    };

    /**
     * 将html显示的内容转换为textarea的内容（保留空格回车）
     * @param content
     * @returns {string | *}
     */
    activityApp.prototype.htmlToTextarea = function (content) {
        var $this = this;
        if ($this.isEmpty(content)) {
            return content;
        }
        var reg = new RegExp("<br>", "g");
        var regSpace = new RegExp("&nbsp;", "g");
        content = content.replace(reg, "\n");
        content = content.replace(regSpace, " ");
        return content;
    };

    /**
     * 获取云盘图片的url
     * @param activity
     * @returns {string}
     */
    activityApp.prototype.getCloudImgUrl = function (activity) {
        var $this = this;
        var coverCloudId = activity.coverCloudId;
        var coverUrl = activity.coverUrl;
        if (!$this.isEmpty(coverUrl)) {
            return activity.coverUrl;
        }
        return $this.buildCloudImgUrl(coverCloudId);
    };

    activityApp.prototype.buildCloudImgUrl = function (cloudId) {
        return "http://p.ananas.chaoxing.com/star3/origin/" + cloudId;
    };

    /**
     * 获取活动状态说明
     * @param status
     * @returns {string|*}
     */
    activityApp.prototype.getActivityStatusInstructions = function (status) {
        var $this = this;
        if ($this.isEmpty(status)) {
            return status;
        }
        switch (status) {
            case 0:
                return "已删除";
            case 1:
                return "未发布";
            case 2:
                return "未开始";
            case 3:
                return "进行中";
            default:
                return "已结束";
        }
    };

    /**
     * 获取活动默认封面云盘id
     * @returns {string}
     */
    activityApp.prototype.getDefaultCoverCloudId = function () {
        return "3b16823d7d5fc677d13c042479e3c6d0";
    };
    /**
     * 获取相对路径
     * @param url
     * @returns {string}
     */
    activityApp.prototype.getRelativeUrl = function (url) {
        var pathname = document.location.pathname;
        if (pathname == "/") {
            pathname = "";
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return pathname + url;
    };
    /**
     * 生成活动的默认开始时间
     * @returns {string}
     */
    activityApp.prototype.generateActivityDefaultStartTimeStamp = function () {
        var $this = this;
        return new Date().getTime();
    };
    /**
     * 生成活动的默认结束时间
     * @returns {string}
     */
    activityApp.prototype.generateActivityDefaultEndTimeStamp = function () {
        return new Date(new Date().setMonth(new Date().getMonth() + 1)).getTime();
    };
    /**
     * 获取双选会主页地址
     * @param activityId
     * @param fid
     * @returns {string}
     */
    activityApp.prototype.generateDualSelectIndexUrl = function (activityId, fid) {
        return "http://appcd.chaoxing.com/form-employment/pc/double/election?activityId=" + activityId + "&wfwfid=" + fid;
    };
    /**
     * 双选会统计导出地址
     * @param activityId
     * @param fid
     * @returns {string}
     */
    activityApp.prototype.generateDualSelectStatExportUrl = function (activityId, fid) {
        return "http://appcd.chaoxing.com/form-employment/export/double/selection/statistics?activityId=" + activityId + "&wfwfid=" + fid;
    };
    /**
     * 禁用滚动
     */
    activityApp.prototype.disableScroll = function () {
        var scrollTop = $(document).scrollTop();
        $(document).on('scroll.unable', function (e) {
            $(document).scrollTop(scrollTop);
        });
    };
    /**
     * 启用滚动
     */
    activityApp.prototype.enableScroll = function () {
        $(document).unbind("scroll.unable");
    };
    /**
     * 初始化滚动条
     */
    activityApp.prototype.initScroll = function () {
        this.scrollTop = $(document).scrollTop();
        $(document).scrollTop(0);
        this.disableScroll();
    };
    /**
     * 重置滚动条
     */
    activityApp.prototype.resetScroll = function () {
        this.enableScroll();
        $(document).scrollTop(this.scrollTop);
    };
    /**
     * 是否在今天以前
     * @param timestamp
     */
    activityApp.prototype.isBeforeToday = function (timestamp) {
        var now = new Date();
        var todayStr = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + " 00:00:00";
        var today = new Date(todayStr);
        if (!isNaN(today)) {
            today = new Date(Date.parse(todayStr.replace(/-/g, "/")));
        }
        return today.getTime() > timestamp;
    };
    /**
     * 是否被嵌入
     * @returns {boolean}
     */
    activityApp.prototype.isEmbedded = function () {
        return window.parent !== window;
    };
    /**
     * 手动出发resize
     */
    activityApp.prototype.resize = function () {
        if(document.createEvent) {
            var event = document.createEvent("HTMLEvents");
            event.initEvent("resize", true, true);
            window.dispatchEvent(event);
        } else if(document.createEventObject) {
            window.fireEvent("onresize");
        }
    };
    /**
     * 返回作品征集的管理地址
     * @param workId
     * @returns {string}
     */
    activityApp.prototype.getWorkManageUrl = function (workId) {
        return "http://reading.chaoxing.com/zj//manage/activity/" + workId + "/new?isHideHeader=false";
    };
    /**
     * 返回阅读书单的管理地址
     * @param workId
     * @returns {string}
     */
    activityApp.prototype.getReadingBookManageUrl = function (readingId, moduleId) {
        return "http://xueya.chaoxing.com/school-base/school-reading/" + readingId + "/" + moduleId + "/book-list";
    };
    W['activityApp'] = new activityApp();
})(window, jQuery, JSON);
Array.prototype.remove = function (val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};
Array.prototype.pushArray = function (array) {
    var $this = this;
    $.each(array, function () {
        $this.push(this);
    });
};
Vue.filter("getCloudImgUrl", function (activity) {
    return activityApp.getCloudImgUrl(activity);
});
Vue.filter("activityStatusInstructions", function (status) {
    return activityApp.getActivityStatusInstructions(status);
});


Vue.filter("activityStatusDescribe", function (status) {
    if (activityApp.isEmpty(status)) {
        return "";
    }
    switch (status) {
        case 0:
            return "已删除";
        case 1:
            return "未发布";
        case 2:
            return "已发布";
        case 3:
            return "进行中";
        case 4:
            return "已结束";
        default:
            return "";
    }
});

Vue.filter("activityStatusFilter", function (status) {
    if (activityApp.isEmpty(status)) {
        return status;
    }
    switch (status) {
        case 0:
            return "已删除";
        case 1:
        case 2:
            return "未开始";
        case 3:
            return "进行中";
        default:
            return "已结束";
    }
});

Vue.filter("timestampFormat", function (timestamp) {
    if (activityApp.isEmpty(timestamp)) {
        return "";
    }
    var dateObj = moment(timestamp);
    var year = dateObj.year();
    if (year == new Date().getFullYear()) {
        return dateObj.format("MM-DD HH:mm");
    } else {
        return dateObj.format("YYYY-MM-DD HH:mm");
    }

});
Vue.filter("timestampScopeFormat", function (startTimestamp, endTimestamp) {
    var thisYear = new Date().getFullYear();
    var start = "";
    var startDateObj = null;
    var end = "";
    var endDateObj = null;
    if (!activityApp.isEmpty(startTimestamp)) {
        startDateObj = moment(startTimestamp);
    }
    if (!activityApp.isEmpty(endTimestamp)) {
        endDateObj = moment(endTimestamp);
    }
    var isThisYear = (!startDateObj || startDateObj.year() == thisYear) && (!endDateObj || endDateObj.year() == thisYear);
    // 是不是同一天
    var isSameDay = startDateObj && endDateObj && startDateObj.year() == endDateObj.year() && startDateObj.month() == endDateObj.month() && startDateObj.date() == endDateObj.date();
    if (isThisYear) {
        start = startDateObj ? startDateObj.format("MM-DD HH:mm") : "";
        if (isSameDay) {
            end = endDateObj ? endDateObj.format("HH:mm") : "";
        } else {
            end = endDateObj ? endDateObj.format("MM-DD HH:mm") : "";
        }
    } else {
        start = startDateObj ? startDateObj.format("YYYY-MM-DD HH:mm") : "";
        if (isSameDay) {
            end = endDateObj ? endDateObj.format("MM-DD HH:mm") : "";
        } else {
            end = endDateObj ? endDateObj.format("YYYY-MM-DD HH:mm") : "";
        }
    }
    var result = start;
    if (!activityApp.isEmpty(start) && !activityApp.isEmpty(end)) {
        result += " ~ ";
    }
    result += end;
    return result;
});

Vue.filter("percentageFormat", function (value) {
    if (!value || typeof value != "number") {
        return 0;
    }
    return Decimal.mul(value, 100).toNumber() + '%';
});