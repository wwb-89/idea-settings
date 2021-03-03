(function (W, $) {
    if (typeof W.signApp != 'undefined') {
        return;
    }

    function signApp() {
    }

    /**
     * 创建新的报名签到
     * @returns {{notes: string, defaultSignUpId: null, coverCloudId: string, signIn: {btnName: string, fillInfoFormId: null, address: string, publicList: boolean, startTimeStr: string, scanCodeWay: number, signId: null, way: number, endTimeStr: string, fillInfo: boolean, name: string, startTime: null, id: null, endTime: null, dimension: null, longitude: null}, name: string, id: null, signUp: {btnName: string, fillInfoFormId: null, limitPerson: boolean, publicList: boolean, startTimeStr: string, signId: null, openAudit: boolean, endTimeStr: string, personLimit: number, fillInfo: boolean, startTime: null, id: null, endTime: null}, defaultSignInId: null}}
     */
    signApp.prototype.newSign = function () {
        var $this = this;
        return {
            id: null,
            name: "",
            notes: "",
            defaultSignUpId: null,
            defaultSignInId: null,
            signUp: $this.newSignUp(),
            signIn: $this.newSignIn(),
            signOut: $this.newSignOut()
        };
    };

    /**
     * 创建报名
     * @returns {{btnName: string, fillInfoFormId: null, limitPerson: boolean, publicList: boolean, startTimeStr: string, signId: null, openAudit: boolean, endTimeStr: string, personLimit: number, fillInfo: boolean, startTime: null, id: null, endTime: null}}
     */
    signApp.prototype.newSignUp = function () {
        return {
            id: null,
            signId: null,
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
            btnName: "报名"
        };
    };

    /**
     * 创建签到
     * @returns {{btnName: string, fillInfoFormId: null, address: string, publicList: boolean, startTimeStr: string, scanCodeWay: number, signId: null, way: number, endTimeStr: string, fillInfo: boolean, name: string, startTime: null, id: null, endTime: null, dimension: null, longitude: null}}
     */
    signApp.prototype.newSignIn = function () {
        var now = new Date();
        return {
            id: null,
            signId: null,
            name: "",
            type: "sign_in",
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
            btnName: "签到"
        };
    };

    /**
     * 创建签退
     * @returns {*}
     */
    signApp.prototype.newSignOut = function () {
        var $this = this;
        return $.extend({}, $this.newSignIn(), {
            type: "sign_out",
            btnName: "签退"
        });
    };
    W['signApp'] = new signApp();
})(window, jQuery);