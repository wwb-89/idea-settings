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
        return (/^1(3|4|5|7|8|9)\d{9}$/.test(phone));
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
            return decodeURL(r[2]);
        }
        return null;
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
     * @param cloudId
     * @returns {string}
     */
    activityApp.prototype.getCloudImgUrl = function (cloudId) {
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
                return "待发布";
            case 2:
                return "待开始";
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
        return "ce257d8a6c546bcedcc7f415dd504296";
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
    activityApp.prototype.generateActivityDefaultStartTime = function () {
        var $this = this;
        var date = new Date();
        var dateObj = $this.millisecond2DateObj(date.getTime());
        return dateObj.year + "-" + dateObj.month + "-" + dateObj.day + " " + "00:00:00";
    };
    /**
     * 生成活动的默认结束时间
     * @returns {string}
     */
    activityApp.prototype.generateActivityDefaultEndTime = function () {
        var $this = this;
        var date = new Date(new Date().setMonth(new Date().getMonth() + 1));
        var dateObj = $this.millisecond2DateObj(date.getTime());
        return dateObj.year + "-" + dateObj.month + "-" + dateObj.day + " " + "23:59:59";
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
    var that = this;
    $.each(array, function () {
        that.push(this);
    });
};
Vue.filter("getCloudImgUrl", function (cloudId) {
    return activityApp.getCloudImgUrl(cloudId);
});
Vue.filter("activityStatusInstructions", function (status) {
    return activityApp.getActivityStatusInstructions(status);
});
Vue.filter("timestamp2ChineseYMD", function (timestamp) {
    var dateObj = activityApp.millisecond2DateObj(timestamp);
    return dateObj.year + "年" + dateObj.month + "月" + dateObj.day + "日";
});
Vue.filter("timestamp2ChineseYMDHM", function (timestamp) {
    var dateObj = activityApp.millisecond2DateObj(timestamp);
    return dateObj.year + "年" + dateObj.month + "月" + dateObj.day + "日" + " " + dateObj.hour + ":" + dateObj.minute;
});
Vue.filter("timestamp2ChineseYMDHMS", function (timestamp) {
    var dateObj = activityApp.millisecond2DateObj(timestamp);
    return dateObj.year + "年" + dateObj.month + "月" + dateObj.day + "日" + " " + dateObj.hour + ":" + dateObj.minute + ":" + dateObj.second;
});
Vue.filter("timestamp2YMD", function (timestamp) {
    var dateObj = activityApp.millisecond2DateObj(timestamp);
    return dateObj.year + "-" + dateObj.month + "-" + dateObj.day;
});
Vue.filter("timestamp2YMDH", function (timestamp) {
    var dateObj = activityApp.millisecond2DateObj(timestamp);
    return dateObj.year + "-" + dateObj.month + "-" + dateObj.day +" "+dateObj.hour;
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