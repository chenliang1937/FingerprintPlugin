var fingerprintPlugin = {
    /** 打开指纹 */
    openFingerprint: function (arg0, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'openFingerprint',
            arg0
        );
    },

    /** 关闭指纹 */
    closeFingerprint: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'closeFingerprint',
            []
        );
    },

    /** 读取指纹模块数据 */
    readFingerprint: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'readFingerprint',
            []
        );
    },

    /** 中断指令 */
    interrupt: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'interrupt',
            []
        );
    },

    /** 查询手指状态 */
    checkFingerStatus: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'checkFingerStatus',
            []
        );
    },

    /** 注册指纹 */
    registerFinger: function (arg0, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'registerFinger',
            arg0
        );
    },

    /** 匹配指纹 */
    matchFinger: function (arg0, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'matchFinger',
            arg0
        );
    },

    /** 删除指纹 */
    deleteFinger: function (arg0, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'FingerprintPlugin',
            'deleteFinger',
            arg0
        );
    },
};

module.exports = fingerprintPlugin;