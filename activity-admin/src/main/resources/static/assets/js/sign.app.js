(function (W, $) {
    if (typeof W.signApp != 'undefined') {
        return;
    }

    function signApp() {
    }

    /** 报名模块类型 */
    signApp.prototype.signUpModuleType = "sign_up";
    /** 签到模块类型 */
    signApp.prototype.signInModuleType = "sign_in";
    /** 签退模块类型 */
    signApp.prototype.signOutModuleType = "sign_out";

    /**
     * 创建新的报名签到
     * @param activityFlagSignModules
     * @returns {{notes: string, defaultSignUpId: null, signIn: {btnName: string, fillInfoFormId: null, address: string, publicList: boolean, startTimeStr: string, scanCodeWay: number, signId: null, way: number, endTimeStr: string, fillInfo: boolean, name: string, startTime: null, id: null, endTime: null, dimension: null, longitude: null}, name: string, signOut: *, id: null, signUp: {btnName: string, fillInfoFormId: null, limitPerson: boolean, publicList: boolean, startTimeStr: string, signId: null, openAudit: boolean, endTimeStr: string, personLimit: number, fillInfo: boolean, startTime: null, id: null, endTime: null}, defaultSignInId: null}}
     */
    signApp.prototype.newSign = function () {
        return {
            id: null,
            name: "",
            notes: "",
            signUps: [],
            signIns: []
        };
    };

    /**
     * 创建报名
     * @param activityFlagSignModule
     * @returns {{btnName: string, fillInfoFormId: null, limitPerson: boolean, publicList: boolean, moduleName: (*|string), signId: null, deleted: boolean, openAudit: boolean, personLimit: number, fillInfo: boolean, startTime: number, id: null, endTime: number, endTimestamp: string, startTimestamp: string}}
     */
    signApp.prototype.newSignUp = function (tplComponent) {
        return {
            id: null,
            signId: null,
            name: tplComponent.name ? tplComponent.name : "报名",
            openAudit: false,
            startTime: new Date(activityApp.generateActivityDefaultStartTime()).getTime(),
            startTimestamp: "",
            endTime: (new Date(new Date(activityApp.generateActivityDefaultEndTime()).getTime() - 24 * 60 * 60 * 1000)).getTime(),
            endTimestamp: "",
            limitPerson: false,
            personLimit: 100,
            fillInfo: false,
            fillInfoFormId: null,
            publicList: false,
            btnName: "报名参与",
            endAllowCancel: true,
            endNotAllowCancel: false,
            enableWfwParticipateScope: false,
            enableContactsParticipateScope: false,
            activityFlag: "",
            participateScopes: [],
            wfwParticipateScopes: [],
            contactsParticipateScopes: [],
            deleted: false,
            // deleted取反
            enable: true
        };
    };

    /**
     * 创建签到
     * @param activityFlagSignModule
     * @returns {{btnName: string, fillInfoFormId: null, address: string, publicList: boolean, moduleName: (*|string), scanCodeWay: number, type: string, signId: null, way: number, deleted: boolean, fillInfo: boolean, name: string, detailAddress: string, startTime: number, id: null, endTime: number, endTimestamp: string, dimension: null, startTimestamp: string, longitude: null}}
     */
    signApp.prototype.newSignIn = function (tplComponent) {
        var now = new Date();
        return {
            id: null,
            signId: null,
            name: tplComponent.name ? tplComponent.name : "签到",
            type: tplComponent.code == 'sign_in' ? "sign_in" : "sign_out",
            typeName: tplComponent.code == 'sign_in' ? "签到" : "签退",
            startTime: now.getTime(),
            startTimestamp: "",
            endTime: (new Date(now.getTime() + 2 * 60 * 60 * 1000)).getTime(),
            endTimestamp: "",
            way: 1,
            address: "",
            detailAddress: "",
            longitude: null,
            dimension: null,
            scanCodeWay: 1,
            fillInfo: false,
            fillInfoFormId: null,
            publicList: false,
            btnName: "签到",
            deleted: true,
            // deleted取反
            enable: false
        };
    };

    /**
     * 创建签退
     * @param moduleName
     * @returns {*}
     */
    signApp.prototype.newSignOut = function (activityFlagSignModule) {
        var $this = this;
        if (activityApp.isEmpty(activityFlagSignModule)) {
            activityFlagSignModule = {
                moduleName: "签退"
            }
        }
        return $.extend({}, $this.newSignIn(activityFlagSignModule), {
            type: $this.signOutModuleType,
            typeName: "签退",
            btnName: "签退"
        });
    };
    /**
     * 修改报名签到
     * @param sign
     * @param activitySignModules
     * @param activityFlagSignModules
     */
    signApp.prototype.editSign = function (sign, activitySignModules, activityFlagSignModules) {
        var $this = this;
        var oldSignUps = sign.signUps;
        var oldSignIns = sign.signIns;
        var signUps = [];
        var signIns = [];
        // 活动规定了要显示多少报名和签到的模块， 现有报名签到中不存在的需要补上（deleted设置为false）
        // 处理报名
        $(oldSignUps).each(function () {
            this.enable = !this.deleted;
            var signUpId = this.id;
            var exist = false;
            $(activitySignModules).each(function () {
                if (this.moduleType == $this.signUpModuleType && this.moduleId == signUpId) {
                    exist = true;
                    return false;
                }
            });
            if (exist) {
                signUps.push(this);
            }
        });
        // 处理签到、签退
        $(oldSignIns).each(function () {
            this.typeName = this.type == $this.signInModuleType ? "签到" : "签退";
            this.enable = !this.deleted;
            var signInId = this.id;
            var moduleType = this.type;
            var exist = false;
            $(activitySignModules).each(function () {
                if (this.moduleType == moduleType && this.moduleId == signInId) {
                    exist = true;
                    return false;
                }
            });
            if (exist) {
                signIns.push(this);
            }
        });
        // 判断当前的活动标示应该有多少个报名和签到， 多了忽略，少了补上
        var needSignUpNum = 0;
        var needSignInNum = 0;
        var needSignOutNum = 0;
        $(activityFlagSignModules).each(function () {
            if (this.moduleType == $this.signUpModuleType) {
                needSignUpNum++;
            }else if (this.moduleType == $this.signInModuleType) {
                needSignInNum++;
            }else if (this.moduleType == $this.signOutModuleType) {
                needSignOutNum++;
            }
        });
        var existSignUpNum = 0;
        var existSignInNum = 0;
        var existSignOutNum = 0;
        $(activitySignModules).each(function () {
            if (this.moduleType == $this.signUpModuleType) {
                existSignUpNum++;
            }else if (this.moduleType == $this.signInModuleType) {
                existSignInNum++;
            }else if (this.moduleType == $this.signOutModuleType) {
                existSignOutNum++;
            }
        });
        for (var i = 0; i < (needSignUpNum - existSignUpNum); i++) {
            var signUp = $this.newSignUp();
            signUp.deleted = true;
            signUps.push(signUp);
        }
        for (var i = 0; i < (needSignInNum - existSignInNum); i++) {
            var signIn = $this.newSignIn();
            signIn.deleted = true;
            signIns.push(signIn);
        }
        for (var i = 0; i < (needSignOutNum - existSignOutNum); i++) {
            var signOut = $this.newSignOut();
            signOut.deleted = true;
            signIns.push(signOut);
        }
        $(signUps).each(function () {
            var participateScopes = this.participateScopes;
            if (activityApp.isEmpty(participateScopes)) {
                this.participateScopes = [];
            }
        });
        sign.signUps = signUps;
        sign.signIns = signIns;
    };
    W['signApp'] = new signApp();
})(window, jQuery);