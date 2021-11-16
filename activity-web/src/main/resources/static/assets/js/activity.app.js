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

    /** 活动管理的域名 */
    activityApp.prototype.manage_domain = "https://manage.hd.chaoxing.com";

    /** 新增图标的地址 */
    activityApp.prototype.add_icon_url = "http://p.ananas.chaoxing.com/star3/origin/1424584b7802e81c1e2db2e89b855c4f";
    /** "我的"图标的地址 */
    activityApp.prototype.my_icon_url = "http://p.ananas.chaoxing.com/star3/origin/ce27315ad48158b1df8fba285eb9f1d4";
    /** 更多图标的地址 */
    activityApp.prototype.more_icon_url = "http://p.ananas.chaoxing.com/star3/origin/4e462ff961ef7f94dfb02b2d301a8b7a";

    /**
     * 字符串是否为空
     * @param str
     */
    activityApp.prototype.isEmpty = function (str) {
        return typeof (str) == 'undefined' || str == null || "" === $.trim(str);
    };

    activityApp.prototype.other_activity_type_marketids = [293, 305, 1149];

    activityApp.prototype.showOtherActivityType = function (marketId) {
        var $this = this;
        return $this.other_activity_type_marketids.indexOf(marketId) != -1;
    }
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
            return decodeURL(r[2]);
        }
        return null;
    }
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
        return "http://p.ananas.chaoxing.com/star3/origin/" + coverCloudId;
    };
    /**
     * 生成新的签到报名
     * @returns {{signUpEndTime: string, limitPerson: boolean, signUpFormId: string, createUserName: string, signInStartTime: string, createFid: string, signInFormId: string, signUpStartTime: string, signInEndTime: string, id: null, dimension: string, createUid: string, longitude: string, createOrgName: string, address: string, signUpBtnName: string, publicSignUpList: boolean, partakeForm: null, signInInfoWrite: string, signUpForm: string, signInForm: string, signInBtnName: string, signUpInfoWrite: boolean, personLimit: number, name: string}}
     */
    activityApp.prototype.newSign = function () {
        return {
            id: null,
            name: "",
            // 1：报名。2：签到，3：报名后签到
            partakeForm: 1,
            // 报名开始时间
            signUpStartTime: "",
            //报名结束时间
            signUpEndTime: "",
            // 签到开始时间
            signInStartTime: "",
            // 签到结束时间
            signInEndTime: "",
            // 是否限制报名人数
            limitPerson: false,
            // 限制的人数
            personLimit: 1,
            // 报名方式
            signUpForm: "",
            // 签到方式
            signInForm: 1,
            // 地址
            address: "",
            // 经度
            longitude: "",
            // 维度
            dimension: "",
            // 是否开启报名信息填写
            signUpInfoWrite: false,
            // 报名信息填写的表单id
            signUpFormId: "",
            // 是否开启签到信息填写
            signInInfoWrite: false,
            // 签到信息填写的表单id
            signInFormId: "",
            // 是否公开报名名单
            publicSignUpList: false,
            // 是否公开签到名单
            publicSignInList: false,
            // 报名按钮名称
            signUpBtnName: "",
            // 签到按钮名称
            signInBtnName: "",
            // 创建人uid
            createUid: "",
            // 创建人姓名
            createUserName: "",
            // 创建人fid
            createFid: "",
            // 创建人机构名称
            createOrgName: ""

        };
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
                return "活动预告";
            case 3:
                return "进行中";
            default:
                return "已结束";
        }
    };
    /**
     * 获取我的活动状态说明
     * @param status
     * @returns {string|*}
     */
    activityApp.prototype.getMyActivityStatusInstructions = function (status) {
        var $this = this;
        if ($this.isEmpty(status)) {
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
    };
    /**
     * 获取活动默认封面云盘id
     * @returns {string}
     */
    activityApp.prototype.getDefaultCoverCloudId = function () {
        return "3b16823d7d5fc677d13c042479e3c6d0";
    };
    /**
     * 积分推送
     * @param activityId
     * @param activityName
     */
    activityApp.prototype.integralPush = function (activityId, activityName) {
        var url = ctx + "/api/integral/push/activity/" + activityId + "/view";
        var params = {
            activityName: activityName
        };
        $.post(url, params, function (data) {}).fail(function () {});
    };
    /**
     * 获取相对路径
     * @param url
     * @returns {string}
     */
    activityApp.prototype.getRelativeUrl = function (url) {
        return document.location.pathname + url;
    };
    /**
     * 获取活动管理主页url
     * @param activityId
     * @returns {string}
     */
    activityApp.prototype.getActivityManageIndexUrl = function (activityId) {
        return "https://manage.hd.chaoxing.com/activity/" + activityId;
    };
    /**
     * 解析活动标签
     * @param activity
     * @returns {*[]}
     */
    activityApp.prototype.resolveActivityTags = function (activity) {
        var $this = this;
        var tags = activity.tags;
        var tagList = [];
        if (!$this.isEmpty(tags)) {
            tagList = tags.split(",");
        }
        return tagList;
    };
    /**
     * 是否被嵌入
     * @returns {boolean}
     */
    activityApp.prototype.isEmbedded = function () {
        return window.parent !== window;
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
Vue.filter("myActivityStatusInstructions", function (status) {
    return activityApp.getMyActivityStatusInstructions(status);
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
Vue.filter("timestampScopeFormat", function (startTimestamp, endTimestamp, format) {
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
    if (format) {
        start = startDateObj ? startDateObj.format(format) : "";
        end = endDateObj ? endDateObj.format(format) : "";
    } else if (isThisYear) {
        start = startDateObj ? startDateObj.format("MM-DD HH:mm") : "";
        if (isSameDay) {
            end = endDateObj ? endDateObj.format("HH:mm") : "";
        } else {
            end = endDateObj ? endDateObj.format("MM-DD HH:mm") : "";
        }
    } else {
        start = startDateObj ? startDateObj.format("YYYY-MM-DD HH:mm") : "";
        if (isSameDay) {
            end = endDateObj ? endDateObj.format("HH:mm") : "";
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