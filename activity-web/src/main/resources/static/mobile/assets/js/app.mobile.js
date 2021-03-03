(function(W, $, J, B){
    if (typeof W.app != 'undefined') {
        return;
    }

    function app() {}

    app.prototype.origin = W.location.origin;
    /** 专题url */
    app.prototype.specialSubjectUrl = "https://special.rhky.com/mobile/mooc/tocourse/";
    /** 课程url */
    app.prototype.courseUrl = "http://mooc1-api.chaoxing.com/phone/courseindex?courseid=";

    /** 页面刷新标识 */
    app.prototype.pageRefreshFlag = "page_refresh_flag";
    /** 数据删除标识 */
    app.prototype.dataDeleteFlag = "data_delete_flag";
    /** 数据修改标识 */
    app.prototype.dataModifyFlag = "data_modify_flag";
    /** view关闭标识 */
    app.prototype.view_close_flag = "view_close_flag";
    /** 修改的数据对象 */
    app.prototype.dataModifyObject = "data_modify_object";
    /** 删除的数据对象 */
    app.prototype.dataDeleteObject = "data_delete_object";

    app.prototype.resCategory = {
        "specialSubject":{"id":"100000001","name":"专题"},
        "course":{"id":"100000002","name":"课程"},
        "magazine":{"id":"100000006","name":"期刊"},
        "webUrl":{"id":"100000015", "name":"网页"}
    };

    app.prototype.workAnnexSuffixes = {
        pictureSuffixes: ["png","jpg","jpeg","bmp","gif"],
        audioSuffixes: ["aac","ac3","aif","amr","ape","asf","flac","m4a","m4r","mid","mmf","mpa","mpc","ogg","pcm","mpc","mp3"],
        videoSuffixes: ["rmvb","3gp","mpg","mpeg","mov","wmv","avi","mkv","mp4","flv","vob","f4v"],
        wordSuffixes: ["doc", "docx"],
        pptSuffixes: ["ppt", "pptx"],
        excelSuffixes: ["xls", "xlsx"],
        pdfSuffixes: ["pdf"]
    };

    app.prototype.browser = {
        versions : function() {
            var u = navigator.userAgent, app = navigator.appVersion;
            return { //移动终端浏览器版本信息
                trident : u.indexOf('Trident') > -1, //IE内核
                presto : u.indexOf('Presto') > -1, //opera内核
                webKit : u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                mobile : !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
                ios : !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器
                iPhone : u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
                iPad : u.indexOf('iPad') > -1, //是否iPad
                webApp : u.indexOf('Safari') == -1
                //是否web应该程序，没有头部与底部
            };
        }(),
        language : (navigator.browserLanguage || navigator.language).toLowerCase()
    };
    /**
     * 获取学习通的版本
     * @returns {string}
     */
    app.prototype.getChaoxingVersion = function () {
        var that = this;
        if (!that.isChaoxingApp()) {
            return null;
        }
        var userAgent = navigator.userAgent;
        var arrs = userAgent.split("ChaoXingStudy_3_");
        var chaoxingAppVersion = arrs[1];
        arrs = chaoxingAppVersion.split("_");
        chaoxingAppVersion = arrs[0];
        return chaoxingAppVersion;
    };
    /**
     * 录音对象
     * @type {{size: number, succ: boolean, html: string, duration: number}}
     */
    app.prototype.soundRecordingObj = {
        size: 0,
        succ: false,
        html: '',
        duration: 0
    };
    /**
     * 图片对象
     * @type {{camera: string, files: *[]}}
     */
    app.prototype.pictureObj = {
        camera: "6",
        files: [
            {
                width: 0,
                height: 0,
                objectid: 0,
                size: 0,
                resid: 0,
                type: "jpg",
                name: ""

            }
        ]
    };
    /**
     * 照片对象
     * @type {{camera: string, files: *[]}}
     */
    app.prototype.cameraObj = {
        camera: "7",
        files: [
            {
                width: 0,
                height: 0,
                objectid: 0,
                size: 0,
                resid: 0,
                type: "jpg",
                name: ""

            }
        ]
    };
    /**
     * 视频对象
     * @type {{camera: string, files: *[]}}
     */
    app.prototype.videoObj = {
        camera: "2",
        resid: 0,
        datasize: 0,
        objectid: 0,
        name: "",
        type: "mp4"
    };
    /**
     * 判断当前环境是不是移动端
     * @returns {boolean|*}
     */
    app.prototype.isApp = function(){
        var that = this;
        return that.browser.versions.mobile;
    };
    /**
     * 判断当前环境是不是安卓
     */
    app.prototype.isAndroid = function(){
        var that = this;
        return that.browser.versions.android;
    };
    /**
     * 判断当前环境是不是ios
     */
    app.prototype.isIos = function(){
        var that = this;
        return that.browser.versions.iPhone;
    };
    /**
     * 判断当前环境是不是微信
     */
    app.prototype.isWeixin = function () {
        var that = this;
        if (that.browser.versions.mobile) {//判断是否是移动设备打开。browser代码在下面
            var ua = navigator.userAgent.toLowerCase();//获取判断用的对象
            return ua.match(/MicroMessenger/i) == 'micromessenger';
        } else {
            return false;
        }
    };
    /**
     * 判断是不是QQ
     * @returns {boolean}
     */
    app.prototype.isQQ = function () {
        var that = this;
        if (that.browser.versions.mobile) {//判断是否是移动设备打开。browser代码在下面
            var ua = navigator.userAgent.toLowerCase();//获取判断用的对象
            return ua.match(/QQ/i) == "qq";
        } else {
            return false;
        }
    };
    /**
     * 字符串是否为空
     * @param str
     */
    app.prototype.isEmpty = function(str){
        return typeof(str) == 'undefined' || str == null || "" === $.trim(str);
    };
    /**
     * 判断是否是超星app
     * @returns {boolean}
     */
    app.prototype.isChaoxingApp = function(){
        var ua = navigator.userAgent.toLowerCase();
        if (ua.match(/ChaoXingStudy/i) == "chaoxingstudy") {
            return true;
        }
        return false;
    };
    app.prototype.noAppMsg = function () {
        var that = this;
        that.showMsg("请使用学习通客户端");
    };
    /**
     * 通知自己刷新
     */
    app.prototype.noticeSelfRefresh = function () {
        var url = $.md5(W.location.href);
        var storage = W.localStorage;
        storage[url] = "1";
        B.postNotification("CLIENT_REFRESH_STATUS", {"status": "1"});
    };
    /**
     * 当前页面是否刷新
     * @returns {boolean}
     */
    app.prototype.isSelfRefresh = function () {
        var url = $.md5(W.location.href);
        var storage = W.localStorage;
        var currentPageReflushPageFlag = storage[url];//当前页面刷新标识
        storage.removeItem(url);
        return "1" == currentPageReflushPageFlag;
    };
    /**
     * 通知页面刷新
     */
    app.prototype.noticeRefresh = function () {
        var that = this;
        if (that.isChaoxingApp()) {
            var storage = W.localStorage;
            storage[that.pageRefreshFlag] = "1";
            B.postNotification("CLIENT_REFRESH_STATUS", {"status": "1"});
        }
    };
    /**
     * 是否刷新
     * @returns {boolean}
     */
    app.prototype.isRefresh = function () {
        var that = this;
        var storage = W.localStorage;
        var reflushPageFlag = storage[that.pageRefreshFlag];//页面刷新标识
        storage.removeItem(that.pageRefreshFlag);//清空刷新标识
        return "1" == reflushPageFlag;
    };
    /**
     * 通知修改
     */
    app.prototype.noticeModify = function(id){
        var that = this;
        if(that.isChaoxingApp()){
            var storage = W.localStorage;
            storage[that.dataModifyFlag] = "1";
            storage[that.dataModifyObject] = id;
            B.postNotification("CLIENT_REFRESH_STATUS", {"status":"1"});
        }
    };
    /**
     * 是否修改
     * @returns {boolean}
     */
    app.prototype.isModify = function () {
        var that = this;
        var storage = W.localStorage;
        var dataModifyFlag = storage[that.dataModifyFlag];//页面修改标识
        storage.removeItem(that.dataModifyFlag);//清空数据修改标识
        return "1" == dataModifyFlag;
    };
    /**
     * 获取修改对象
     * @returns {any}
     */
    app.prototype.getModifyObject = function () {
        var that = this;
        var storage = W.localStorage;
        var obj = storage[that.dataModifyObject];
        storage.removeItem(that.dataModifyObject);
        return obj;
    };
    /**
     * 通知删除
     */
    app.prototype.noticeDelete = function(id){
        var that = this;
        if(that.isChaoxingApp()){
            var storage = W.localStorage;
            storage[that.dataDeleteFlag] = "1";
            storage[that.dataDeleteObject] = id;
            B.postNotification("CLIENT_REFRESH_STATUS", {"status":"1"});
        }
    };
    /**
     * 是否删除
     * @returns {boolean}
     */
    app.prototype.isDelete = function () {
        var that = this;
        var storage = W.localStorage;
        var dataDeleteFlag = storage[that.dataDeleteFlag];
        storage.removeItem(that.dataDeleteFlag);
        return "1" == dataDeleteFlag;
    };
    /**
     * 获取删除对象
     * @returns {any}
     */
    app.prototype.getDeleteObject = function () {
        var that = this;
        var storage = W.localStorage;
        var obj = storage[that.dataDeleteObject];
        storage.removeItem(that.dataDeleteObject);
        return obj;
    };
    /**
     * 绑定刷新事件
     */
    app.prototype.bindRefresh = function(callback){
        var cmd = "CLIENT_REFRESH_EVENT";
        B.bind(cmd, callback);
    };
    /**
     * 退出app
     * @param tips 退出时的提示信息
     */
    app.prototype.exit = function(tips){
        var that = this;
        if(that.isChaoxingApp()){
            B.postNotification("CLIENT_EXIT_WEBAPP", {
                message: tips || ''
            });
        }else{
            that.noAppMsg();
        }
    };
    /**
     * 关闭当前页
     * @param tips
     */
    app.prototype.closeView = function(tips){
        var that = this;
        if(that.isChaoxingApp()){
            B.postNotification("CLIENT_EXIT_LEVEL", {
                message: tips || ''
            });
        }
    };
    /**
     * 封装打开url方法
     * @param option
     */
    app.prototype.packageOpenUrl = function (option) {
        if ($.isEmptyObject(option)) {
            return;
        }
        if (/.*[\u4e00-\u9fa5]+.*$/.test(option.webUrl)) {//有中文进行编码
            option.webUrl = encodeURI(option.webUrl);
        }
        if (option.webUrl.indexOf('/') == 0) {
            option.webUrl = location.protocol + "//" + location.hostname + (location.port == 80 ? "" : (":" + location.port + "")) + option.webUrl;
        }
        B.postNotification("CLIENT_OPEN_URL", $.extend({
            title: '', //标题
            loadType: 1, //打开方式，0在本页面打开，1使用客户端webview打开新页面，2打开系统浏览器
            webUrl: '', //要打开的url
            toolbarType: 1
        }, option));
    };
    /**
     * 当前页面打开url
     * @param url
     * @param title
     */
    app.prototype.replaceUrl = function(url, title){
        var that = this;
        var option = {
            "title":title,
            "webUrl":url,
            "loadType":0
        };
        if(that.isChaoxingApp()){
            that.packageOpenUrl(option);
        }else{
            window.location.replace(url);
        }
    };
    /**
     * 打开url
     * @param url
     * @param title
     */
    app.prototype.openUrl = function(url, title){
        var that = this;
        var option = {
            "title":title,
            "webUrl":url,
            "loadType":1
        };
        if(that.isChaoxingApp()){
            that.packageOpenUrl(option);
        }else{
            window.location.href = url;
        }
    };
    /**
     *调用浏览器打开url
     * @param url
     * @param title
     */
    app.prototype.openUrlByBrower = function(url, title){
        var that = this;
        var option = {
            "title":title,
            "webUrl":url,
            "loadType":2
        };
        if(that.isChaoxingApp()){
            that.packageOpenUrl(option);
        }else{
            window.location.href = url;
        }
    };
    /**
     * 打开url但是不带顶部栏
     * @param url
     * @param title
     */
    app.prototype.openUrlNoBar = function(url, title){
        var that = this;
        var option = {
            "title":title,
            "webUrl":url,
            "loadType":1,
            "toolbarType":0
        };
        if(that.isChaoxingApp()){
            that.packageOpenUrl(option);
        }else{
            window.location.href = url;
        }
    };
    /**
     * 根据uid打开用户信息页面
     * @param uid
     */
    app.prototype.openUserInfoPageByUid = function (uid) {
        B.postNotification('CLIENT_OPEN_USERINFO', {
            UserID: '' + uid + '',
            passportID: ''
        });
    };
    /**
     * 根据puid打开用户信息页面
     * @param puid
     */
    app.prototype.openUserInfoPageByPUid = function(puid){
        B.postNotification('CLIENT_OPEN_USERINFO',{
            UserID: '',
            passportID:''+puid+''
        });
    };
    /**
     * 隐藏导航
     */
    app.prototype.hideToolbar = function () {
        B.postNotification('CLIENT_TOOLBAR_TYPE', {
            toolbarType: 0
        });
    };
    /**
     * 显示提示信息
     * @param msg
     */
    app.prototype.showMsg = function(msg){
        var that = this;
        if(that.isChaoxingApp()){
            B.postNotification('CLIENT_DISPLAY_MESSAGE',{
                message: msg
            });
        }else{
            alert(msg);
        }
    };
    /**
     * 获取自己的用户信息
     * @param callback
     */
    app.prototype.getSelfUserInfo = function(callback){
        var that = this;
        if(that.isChaoxingApp()){
            var cmd = 'CLIENT_GET_USERINFO';
            B.unbind(cmd);
            callback && B.bind(cmd, callback);
            B.postNotification(cmd, {
                accountKey: ""
            });
        }else{
            that.noAppMsg();
        }
    };
    /**
     * 打开专题
     * @param resId
     * @param resName
     */
    app.prototype.openSpecial = function(resId, resName){
        var that = this;
        var url = that.specialSubjectUrl + resId;
        that.openUrl(url, resName);
    };
    /**
     *打开小组
     * @param groupId
     */
    app.prototype.openGroup = function (groupId) {
        var that = this;
        if (that.isChaoxingApp()) {
            var cmd = 'CLIENT_OPEN_GROUP';
            B.postNotification(cmd, {
                "GroupId": groupId,
                "needRecord": "false"
            });
        } else {
            that.noAppMsg();
        }
    };
    /**
     * 获取json对象
     * @param jsonStr
     */
    app.prototype.getJsonObject = function(jsonStr){
        var that = this;
        if(that.isEmpty(jsonStr)){
            return null;
        }
        return J.parse(jsonStr);
    };
    /**
     * 获取json字符串
     * @param jsonObject
     */
    app.prototype.getJsonStr = function(jsonObject){
        var that = this;
        if(that.isEmpty(jsonObject)){
            return null;
        }
        return J.stringify(jsonObject);
    };
    /**
     * 获取请求参数
     * @param name
     * @returns {*}
     */
    app.prototype.getUrlParamter = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return decodeURL(r[2]);
        }
        return null;
    };
    /**
     * 转发网页
     * @param title
     * @param url
     * @param logoUrl
     */
    app.prototype.transmitWebUrl = function (title, url, logoUrl) {
        var that = this;
        var resUid = $.md5(url);
        var content = {"resTitle": title, "resUrl": url, "resLogo": logoUrl, "resUid": resUid, "toolbarType": 2}
        B.postNotification("CLIENT_TRANSFER_INFO", {"cataid": that.resCategory.webUrl.id, "content": content});
    };
    /**
     * 从cookie中获取uid
     * @returns {*}
     */
    app.prototype.getUid = function () {
        var that = this;
        return that.getCookie("_uid")
    };
    /**
     * 直播
     * @param id
     */
    app.prototype.live = function (id) {
        var that = this;
        var params = {
            "liveId": id,
            "UID": that.getUid()
        };
        $.ajax({
            url : app.liveCourseDomain + "/api/getStartZhiboInfo",
            type : "get",
            data: params,
            dataType : "json",
            success : function(data) {
                if(data.status == "1"){
                    var cmd = 'CLIENT_OPEN_LIVE';
                    B.postNotification(cmd, data.jsonObject);
                }else{
                }
            },
            error : function() {
            }
        });
    };
    /**
     * 打开扫码
     * @param tips
     * @param callBack
     */
    app.prototype.openScanner = function(tips, callBack) {
        var cmd = 'CLIENT_BARCODE_SCANNER';
        B.unbind(cmd);
        callBack && B.bind(cmd, callBack);
        B.postNotification(cmd, {
            manualInputTitle: tips || ''
        });
    };
    /**
     * 录音
     * @param callback
     */
    app.prototype.soundRecording = function (callBack) {
        var cmd = "CLIENT_AUDIO_RECORD";
        B.unbind(cmd);
        callBack && B.bind(cmd, callBack);
        B.postNotification(cmd, {

        });
    };
    /**
     * 选择图片
     * @param callBack
     */
    app.prototype.selectPicture = function (callBack) {
        var cmd = "CLIENT_CHOOSE_IMAGE_RESULT";
        B.unbind(cmd);
        callBack && B.bind(cmd, callBack);
        B.postNotification('CLIENT_CHOOSE_IMAGE', {
            camare: 6
        });
    };
    /**
     * 拍照
     * @param callBack
     */
    app.prototype.camera = function (callBack) {
        var cmd = "CLIENT_CHOOSE_IMAGE_RESULT";
        B.unbind(cmd);
        callBack && B.bind(cmd, callBack);
        B.postNotification('CLIENT_CHOOSE_IMAGE', {
            camare: 7
        });
    };
    /**
     * 选择视频
     * @param callBack
     */
    app.prototype.selectVideo = function (callBack) {
        var cmd = "CLIENT_CHOOSE_IMAGE_RESULT";
        B.unbind(cmd);
        callBack && B.bind(cmd, callBack);
        B.postNotification('CLIENT_CHOOSE_IMAGE', {
            camare: 2
        });
    };
    /**
     * 选择云盘
     * @param callBack
     */
    app.prototype.selectCloud = function (callBack) {
        var cmd = "CLIENT_SELECT_CLOUDRES";
        var cmd1 = "CLIENT_WEB_EXTRAINFO";
        B.unbind(cmd);
        callBack && B.bind(cmd, callBack);
        B.unbind(cmd1);
        callBack && B.bind(cmd1, callBack);

        B.postNotification('CLIENT_SELECT_CLOUDRES', {
            "": ""
        });
    };
    /**
     *打开图片
     * @param cloudIds
     * @param curIndex
     */
    app.prototype.openImg = function (cloudIds, curIndex) {
        var urls = [];
        $(cloudIds).each(function () {
            urls.push("http://p.ananas.chaoxing.com/star3/origin/" + this);
        });
        var that = this;
        if (that.isEmpty(urls)) {
            return;
        }
        var imageUrls = [];
        $(urls).each(function () {
            imageUrls.push({
                imageUrl: this
            });
        });
        if (that.isEmpty(curIndex)) {
            curIndex = 0;
        }
        var option = {
            "showIndex": curIndex,
            "imageUrls": imageUrls
        };
        var cmd = "CLIENT_PREVIEW_IMAGES";
        B.postNotification(cmd, option);
    };
    /**
     * 打开音频
     * @param cloudId
     * @param title
     */
    app.prototype.openAudio = function (cloudId, title) {
        var $this = this;
        var url = ctx + "/api/cloud/status/" + cloudId;
        $.post(url, {}, function (data) {
            data = $this.getJsonObject(data);
            if (data.status == "waiting") {
                app.showMsg("转码中");
            } else {
                var audioUrl = data.http || data.download;
                B.postNotification('CLIENT_AUDIO_PLAYER', {
                    "sourceType": 1,
                    "title": title,
                    "activeIndex": 0,
                    "list": [{
                        "mediaId": "",
                        "mediaTitle": title,
                        "mediaPath": audioUrl,
                        "mediaPathIOS": audioUrl
                    }]
                });
            }
        });
    };
    /**
     * 打开视频
     * @param cloudId
     * @param title
     */
    app.prototype.openVideo = function (cloudId, title) {
        var $this = this;
        var url = ctx + "/api/cloud/status/" + cloudId;
        $.post(url, {}, function (data) {
            data = $this.getJsonObject(data);
            var videoUrl = data.http;
            B.postNotification('CLIENT_VIDEO_PLAYER', {
                    "videolist": [
                        {
                            "videopathm3u8": videoUrl,
                            "videopathmp4": videoUrl,
                            "title": title
                        }
                    ],
                    "title": title
                }
            );
        });
    };
    app.prototype.openCloudFile = function (cloudId) {
        var $this = this;
        var url = "https://pan-yz.chaoxing.com/screen/file_" + cloudId;
        $this.openUrl(url);
    };
    /**
     * 定制菜单
     * @param name 显示的名称，不能与iconUrl同时有值
     * @param iconUrl icon的url，name
     * @param option 格式{option: '调用的方法名，需要拼接成字符串'}
     */
    app.prototype.customMenu = function (name, iconUrl, option) {
        var that = this;
        var cmd = 'CLIENT_CUSTOM_MENU';
        B.postNotification(cmd, $.extend({
            index: 0,
            show: 1, //是否显示  1显示，0不显示
            width: '60',
            height: '25',
            icon: that.isEmpty(iconUrl) ? '' : iconUrl, //菜单图标，为空或没有此属性，则不显示
            menu: that.isEmpty(name) ? '' : name, //菜单名称，为空或没有此属性，则不显示
            option: '', //操作，实际类型为js方法，在客户端上调用webapp内的js方法
            children: [] //为子菜单列表，如上述属性
        }, option));
    };
    /**
     * 获取cookie的值
     * @param key
     * @returns {*}
     */
    app.prototype.getCookie = function (key) {
        var arr = document.cookie.match(new RegExp("(^| )" + key + "=([^;]*)(;|$)"));
        if (arr != null) return unescape(arr[2]);
        return null;
    };
    /**
     * 将textarea的内容转换为html显示的内容（保留空格回车）
     * @param content
     * @returns {string | *}
     */
    app.prototype.textareaToHtml = function (content) {
        var $this = this;
        if (activityApp.isEmpty(content)) {
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
    app.prototype.htmlToTextarea = function (content) {
        var $this = this;
        if (activityApp.isEmpty(content)) {
            return content;
        }
        var reg = new RegExp("<br>", "g");
        var regSpace = new RegExp("&nbsp;", "g");
        content = content.replace(reg, "\n");
        content = content.replace(regSpace, " ");
        return content;
    };
    /**
     * 创作专题
     */
    app.prototype.creationSpecialTopic = function () {
        var cmd = "CLIENT_CREATE_SPECIAL";
        B.postNotification(cmd, {});
    };
    /**
     * ajax请求
     * @param url
     * @param params
     * @param success
     * @param error
     * @param tips
     */
    app.prototype.ajaxPost = function(url, params, success, error, tips){
        var $this = this;
        if (activityApp.isEmpty(tips)) {
            tips = "";
        }
        $.post(url, params, function (data) {
            if (!activityApp.isEmpty(tips)) {
                if (data.success) {
                    $this.showMsg(tips + "成功", function () {
                        if (activityApp.isFunction(success)) {
                            success(data);
                        }
                    });
                } else {
                    var errorMessage = data.message;
                    if (activityApp.isEmpty(errorMessage)) {
                        errorMessage = tips + "失败";
                    }
                    $this.showMsg(errorMessage, function () {
                        if (activityApp.isFunction(success)) {
                            success(data);
                        }
                    });
                }
            } else {
                if (activityApp.isFunction(success)) {
                    success(data);
                }
            }
        }).fail(function () {
            if (!activityApp.isEmpty(tips)) {
                $this.showMsg(tips + "失败", function () {
                    if (activityApp.isFunction(error)) {
                        error(data);
                    }
                });
            }else {
                if (activityApp.isFunction(error)) {
                    error();
                }
            }
        });
    };
    app.prototype.ajaxPostLoading = function(url, params, success, error, tips){
        var $this = this;
        if (activityApp.isEmpty(tips)) {
            tips = "";
        }
        var object = $this.loading();
        $.post(url, params, function (data) {
            if (data.success) {
                if (!activityApp.isEmpty(tips)) {
                    tips += "成功";
                }
                $this.loadingEndSuccess(object, tips, function () {
                    if (activityApp.isFunction(success)) {
                        success(data);
                    }
                });
            } else {
                var errorMessage = data.message;
                if (!activityApp.isEmpty(errorMessage)) {
                    tips = errorMessage;
                } else if (!activityApp.isEmpty(tips)) {
                    tips += "失败";
                }
                $this.loadingEndError(object, tips, function () {
                    if (activityApp.isFunction(error)) {
                        error();
                    }
                });
            }
        }).fail(function () {
            if (!activityApp.isEmpty(tips)) {
                tips += "失败";
            }
            $this.loadingEndError(object, tips, function () {
                if (activityApp.isFunction(error)) {
                    error();
                }
            });
        });
    };
    app.prototype.loading = function () {
        var toast = $(document).dialog({
            type : 'noticeLoading',
            infoIcon: ctx + '/mobile/assets/lib/dialog2/images/icon/loading.gif'
        });
        return toast;
    };
    app.prototype.loadingEndSuccess = function (object, tips, callback) {
        object.close();
        if (!activityApp.isEmpty(tips)) {
            $(document).dialog({
                type: 'notice',
                infoText: tips,
                autoClose: 1500,
                onClosed: function () {
                    if (activityApp.isFunction(callback)) {
                        callback();
                    }
                }
            });
        } else {
            if (activityApp.isFunction(callback)) {
                callback();
            }
        }
    };
    app.prototype.loadingEndError = function (object, tips, callback) {
        var $this = this;
        $this.loadingEndSuccess(object, tips, callback);
    };
    app.prototype.confirm = function (title, content, sure, cancel, sureBtnTitle, cancleBtnTitle) {
        if (activityApp.isEmpty(title)) {
            title = "提示";
        }
        if (activityApp.isEmpty(sureBtnTitle)) {
            sureBtnTitle = "确定";
        }
        if (activityApp.isEmpty(cancleBtnTitle)) {
            cancleBtnTitle = "取消";
        }
        $(document).dialog({
            titleText: title,
            type : 'confirm',
            content: content,
            buttons: [
                {
                    name: cancleBtnTitle,
                    callback: function () {
                        if (activityApp.isFunction(cancel)) {
                            cancel();
                        }
                    }
                },
                {
                    name: sureBtnTitle,
                    callback: function () {
                        if (activityApp.isFunction(sure)) {
                            sure();
                        }
                    }
                }
            ]
        });
    };
    W['app'] = new app();
})(window, jQuery, JSON, jsBridge);
Array.prototype.remove = function(val) {
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
}
Array.prototype.contains = function (val) {
    var result = false;
    $(this).each(function () {
        if (val + "" == this + "") {
            result = true;
            return false;
        }
    });
    return result;
}
String.prototype.format = String.prototype.f = function () {
    var s = this,
        i = arguments.length;

    while (i--) {
        s = s.replace(new RegExp('\\{' + i + '\\}', 'gm'), arguments[i]);
    }
    return s;
};
function _jsBridgeReady() {
    if (typeof(ready) != undefined &&
        typeof(ready) != "undefined" &&
        typeof(ready) == "function") {
        ready();
    }
}
$(document).ready(function () {
    try {
        FastClick.attach(document.body);
    } catch (e) {
    }
    if (!app.isChaoxingApp()) {
        _jsBridgeReady();
    }
});