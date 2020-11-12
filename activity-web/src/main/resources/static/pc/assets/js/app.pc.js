/**
 * Created by wwb on 2017/6/28.
 */
(function (W, $, J) {
    if (typeof W.app != 'undefined') {
        return;
    }

    function app() {
    }

    /**
     * get请求
     * @param url
     * @param success
     * @param error
     */
    app.prototype.ajaxGet = function (url, success, error) {
        var $this = this;
        $this.ajax(url, "get", {}, success, error, false);
    };
    /**
     * get请求
     * @param url
     * @param success
     * @param error
     * @param loading
     */
    app.prototype.ajaxGet = function (url, success, error, loading) {
        var $this = this;
        $this.ajax(url, "get", {}, true, error, true);
    };
    /**
     * post请求
     * @param url
     * @param params
     * @param success
     * @param error
     */
    app.prototype.ajaxPost = function (url, params, success, error) {
        var $this = this;
        $this.ajax(url, "post", params, success, error, false);
    };
    /**
     * post请求
     * @param url
     * @param params
     * @param success
     * @param error
     * @param loading
     */
    app.prototype.ajaxPost = function (url, params, success, error, loading) {
        var $this = this;
        $this.ajax(url, "post", params, success, error, true);
    };

    app.prototype.ajax = function (url, type, params, success, error, loading) {
        var $this = this;
        var ajaxLoadingIndex;
        if (loading) {
            ajaxLoadingIndex = $this.loading();
        }
        $.ajax({
            url: url,
            type: type,
            data: params,
            dataType: "json",
            success: function (data) {
                if (loading) {
                    $this.closeLayerPop(ajaxLoadingIndex);
                }
                if (activityApp.isFunction(success)) {
                    success(data);
                }
            },
            error: function () {
                if (loading) {
                    $this.closeLayerPop(ajaxLoadingIndex);
                }
                if (activityApp.isFunction(error)) {
                    error(data);
                }
            }
        });
    };

    /**
     * 显示信息
     * @param msg
     */
    app.prototype.showMsg = function (msg, callback) {
        if (activityApp.isFunction(callback)) {
            layer.msg(msg, {
                time: 1000
            }, function () {
                callback();
            });
        } else {
            layer.msg(msg);
        }
    };
    /**
     * 显示成功信息
     * @param msg
     * @param callback
     */
    app.prototype.showSuccessMsg = function (msg, callback) {
        var that = this;
        layer.alert(msg, {icon: 1, shade: 0.6, skin: 'dialog_same'}, function (result) {
            that.closeAllLayerPop();
            if (activityApp.isFunction(callback)) {
                callback(result);
            }
        });
    };
    /**
     * 显示失败信息
     * @param msg
     * @param callback
     */
    app.prototype.showFailMsg = function (msg, callback) {
        var that = this;
        layer.alert(msg, {icon: 2, shade: 0.6, skin: 'dialog_same'}, function () {
            that.closeAllLayerPop();
            if (activityApp.isFunction(callback)) {
                callback();
            }
        });
    };
    /**
     * 显示错误信息
     * @param msg
     * @param callback
     */
    app.prototype.showErrorMsg = function (msg, callback) {
        var that = this;
        layer.alert(msg, {icon: 2, shade: 0.6, skin: 'dialog_same'}, function () {
            that.closeAllLayerPop();
            if (activityApp.isFunction(callback)) {
                callback();
            }
        });
    };
    /**
     * 确认框
     * @param msg
     * @param sure
     * @param cancel
     * @param sureBtnTitle
     * @param cancleBtnTitle
     */
    app.prototype.confirm = function (msg, sure, cancel, sureBtnTitle, cancleBtnTitle) {
        var $this = this;
        if (activityApp.isEmpty(sureBtnTitle)) {
            sureBtnTitle = "确定";
        }
        if (activityApp.isEmpty(cancleBtnTitle)) {
            cancleBtnTitle = "取消";
        }
        layer.confirm(msg, {
            btn: [cancleBtnTitle, sureBtnTitle],
            shade: 0.6,
            skin:'dialog_same'
        }, function (index) {
            $this.closeLayerPop(index);
            if (activityApp.isFunction(cancel)) {
                cancel();
            }
        }, function (index) {
            if (activityApp.isFunction(sure)) {
                sure(index);
            }
        });
    };
    /**
     * loading
     */
    app.prototype.loading = function () {
        return layer.load(1, {shade: [0.2, '#000']});
    };

    app.prototype.closeLayerPop = function (index) {
        layer.close(index);
    };
    /**
     * 关闭所有layer弹窗
     */
    app.prototype.closeAllLayerPop = function () {
        layer.closeAll();
    };
    /**
     * 加载页面
     * @param dom
     * @param url
     */
    app.prototype.loadPage = function (dom, url, data) {
        var that = this;
        that.loading();
        $("" + dom).load(url, that.isEmpty(data) ? {} : data, function (response, status, xhr) {
            that.closeAllLayerPop();
            if ("error" == status) {
                //加载出错时显示错误页面的信息
                $("" + dom).html(response);
            }
        });
    };
    W['app'] = new app();
})(window, jQuery, JSON);
//统一处理ajax请求时未登录问题
var ajaxBackup = $.ajax;
$.ajax = function (url, options) {
    if (typeof url === "object") {
        options = url;
        url = undefined;
    }
    options = options || {};
    var complete = options.complete;
    var success = options.success;
    var error = options.error;

    options.complete = function (jqXHR, textStatus) {
        if ($.isFunction(complete)) {
            complete(jqXHR, textStatus);
        }
    };
    options.success = function (data, textStatus, jqXHR) {
        var contentType = jqXHR.getResponseHeader("content-type") || "";
        if (contentType.indexOf("application/json") > -1 || contentType.indexOf("text/plain") > -1) {
            if (!app.isEmpty(data)) {
                if ("530" == data.code) {//未登录
                    app.showLoginPop();
                    return true;
                }
            }
            if ($.isFunction(success)) {
                success(data, textStatus, jqXHR);
            }
        }
    };
    options.error = function (jqXHR, textStatus, errorThrown) {
        if ($.isFunction(error)) {
            error(jqXHR, textStatus, errorThrown);
        }
    };
    return ajaxBackup(url, options);
};