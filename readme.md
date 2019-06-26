#### 指纹识别插件

**安装**

cordova plugin add fingerprint-plugin

<!--注：该插件需要配合ultralow-plugin或ordinart-plugin使用，先添加ultralow-plugin或ordinart-plugin再添加该插件-->

**使用**

###### 1.打开指纹

`fingerprintPlugin.openFingerprint(param, successCallback, errorCallback);`

<!--param[0]	--	路径(string)-->

<!--param[1]	--	波特率(int)-->

###### 2.关闭指纹

`fingerprintPlugin.closeFingerprint(successCallback, errorCallback);`

###### 3.读取指纹模块数据

`fingerprintPlugin.readFingerprint(successCallback, errorCallback);`

###### 4.中断指令

`fingerprintPlugin.interrupt(successCallback, errorCallback);`

###### 5.查询手指状态

`fingerprintPlugin.checkFingerStatus(successCallback, errorCallback);`

###### 6.注册指纹

`fingerprintPlugin.registerFinger(param, successCallback, errorCallback);`

<!--param[0]	--	第n次(int 注册指纹需要执行三次)-->

<!--param[1]	--	起始ID(int)-->

<!--param[2]	--	结束ID(int)-->

<!--注：先查询手指状态 存在手指再执行注册-->

###### 7.匹配指纹

`fingerprintPlugin.matchFinger(param, successCallback, errorCallback);`

<!--param[0]	--	起始ID(int)-->

<!--param[1]	--	结束ID(int)-->

<!--注：先查询手指状态 存在手指再执行匹配-->

###### 8.删除指纹

`fingerprintPlugin.deleteFinger(param, successCallback, errorCallback);`

<!--param[0]	--	起始ID(int)-->

<!--param[1]	--	结束ID(int)-->

<!--注：当起始ID和结束ID相等时，则表示删除指定的ID号-->

**示例**

```
onDeviceReady: function() {
    fingerprintPlugin.openFingerprint(["/dev/ttysWK1", 115200], function(success) {
        alert(success);
        fingerprintPlugin.readFingerprint(function(data){
            alert(data);
        }, function(error) {
            alert(error);
        })
    }, function(error) {
        alert(error);
    });
},
```

**字段说明**

```
type	--	0x00 中断指令
		--	0x02 查询手指状态
		--	0x03 注册指纹
		--	0x04 匹配指纹
		--	0x05 删除指纹
		
reply	--	0x00 成功
		--	0x8F01 XOR校验错误
		--	0x8F02 SUM校验错误
		--	0x8F03 指令错误
		--	0x8F04 参数错误
		--	0x8F06 无系统文件
		--	0x8F07 系统错误
		--	0x8101 传感器初始化失败
		--	0x8102 传感器校正失败
		--	0x8201 手指检测超时
		--	0x8301 指纹已注册
		--	0x8304 指纹注册满
		--	0x8401 无注册指纹
		--	0x8402 匹配失败
		--	0x8501 删除指定的指纹模板失败
		
result	--	查询手指状态时 0-有手指 1-无手指
		--	注册指纹时(第三次) 注册的指纹ID
		--	匹配指纹时 匹配的指纹ID
```

