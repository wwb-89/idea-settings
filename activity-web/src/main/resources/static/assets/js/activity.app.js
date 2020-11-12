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
     * 毫秒转换为日期字符串
     * @param millisecond
     */
    activityApp.prototype.millisecond2DateStr = function (millisecond) {
        var that = this;
        if (that.isEmpty(millisecond)) {
            return "";
        }
        var date = new Date(millisecond);
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        month = that.fillSpecifiedDigits(month, 2, "0");
        var day = date.getDate();
        day = that.fillSpecifiedDigits(day, 2, "0");
        var hour = date.getHours();
        hour = that.fillSpecifiedDigits(hour, 2, "0");
        var minute = date.getMinutes();
        minute = that.fillSpecifiedDigits(minute, 2, "0");
        var second = date.getSeconds();
        second = that.fillSpecifiedDigits(second, 2, "0");
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
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