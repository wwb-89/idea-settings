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
            signUps: []
        };
    };

    /**
     * 创建报名
     * @param activityFlagSignModule
     * @param btnName
     * @param btnNameKeyword
     * @returns {{btnName: string, fillInfoFormId: null, limitPerson: boolean, publicList: boolean, moduleName: (*|string), signId: null, deleted: boolean, openAudit: boolean, personLimit: number, fillInfo: boolean, startTime: number, id: null, endTime: number, endTimestamp: string, startTimestamp: string}}
     */
    signApp.prototype.newSignUp = function (tplComponent, btnName, btnNameKeyword) {
        return {
            id: null,
            signId: null,
            name: tplComponent.name ? tplComponent.name : "报名",
            openAudit: false,
            startTime: activityApp.generateActivityDefaultStartTimeStamp(),
            endTime: (new Date(activityApp.generateActivityDefaultEndTimeStamp() - 24 * 60 * 60 * 1000)).getTime(),
            limitPerson: false,
            personLimit: 100,
            fillInfo: false,
            formType: 'form',
            fillInfoFormId: null,
            openAddr: null,
            pcUrl: null,
            wechatUrl: null,
            publicList: false,
            btnName: btnName ? btnName : "报名参与",
            keyword: btnNameKeyword ? btnNameKeyword : "报名",
            endAllowCancel: true,
            endNotAllowCancel: false,
            notAllowCancelType: "after_sign_up_end",
            notAllowCancelTime: null,
            wfwOrgScope: 'no_limit',    // 仅作为参与范围前端选择项展示字段
            contactScope: 'no_limit',   // 仅作为参与范围前端选择项展示字段
            wfwOnlySelfUnit: false,
            contactOnlySelfUnit: false,
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
            endTime: (new Date(now.getTime() + 2 * 60 * 60 * 1000)).getTime(),
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
    W['signApp'] = new signApp();
})(window, jQuery);