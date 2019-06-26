package com.zkmeiling.serialport.fingerprint;

import android.util.Log;

import com.google.gson.Gson;
import com.zkmeiling.serialport.model.FingerReceived;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * @author: chenl
 * @data: 2018/10/30
 * @description:
 */
public class FingerprintPlugin extends CordovaPlugin {

    private final String TAG = "FingerprintPlugin";

    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "openFingerprint":
                String path = args.getString(0);
                int baudrate = args.getInt(1);
                openFingerprint(path, baudrate, callbackContext);
                return true;
            case "closeFingerprint":
                closeFingerprint(callbackContext);
                return true;
            case "readFingerprint":
                readFingerprint(callbackContext);
                return true;
            case "interrupt":
                interrupt(callbackContext);
                return true;
            case "checkFingerStatus":
                checkFingerStatus(callbackContext);
                return true;
            case "registerFinger":
                int step = args.getInt(0);
                byte startId[] = int2Byte(args.getInt(1));
                byte endId[] = int2Byte(args.getInt(2));
                registerFinger(step, startId, endId, callbackContext);
                return true;
            case "matchFinger":
                byte startId2[] = int2Byte(args.getInt(1));
                byte endId2[] = int2Byte(args.getInt(2));
                matchFinger(startId2, endId2, callbackContext);
                return true;
            case "deleteFinger":
                byte startId3[] = int2Byte(args.getInt(1));
                byte endId3[] = int2Byte(args.getInt(2));
                deleteFinger(startId3, endId3, callbackContext);
                return true;
            default:
                callbackContext.error("error");
                return false;
        }
    }

    public SerialPort openFingerprint(String path, int baudrate, CallbackContext callbackContext) {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0);

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开指纹串口异常：" + e.toString());
            callbackContext.error("open fingerprint error");
        } catch (SecurityException e) {
            Log.e(TAG, "openSerialPort: 没有指纹串口读写权限：" + e.toString());
            callbackContext.error("no read-write permission");
        }
        callbackContext.success("open fingerprint success");
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeFingerprint(CallbackContext callbackContext) {
        try {
            inputStream.close();
            outputStream.close();

            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭指纹串口异常：" + e.toString());
            callbackContext.error("close fingerprint error");
            return;
        }
        callbackContext.success("close fingerprint success");
    }

    /**
     * 发送串口指令（byte数组）
     *
     * @param sendData
     */
    public void sendFingerprint(byte[] sendData, CallbackContext callbackContext) {
        try {
            if (outputStream != null && sendData.length > 0) {
                outputStream.write(sendData);
                Log.d(TAG, "sendFingerprint: 指纹串口数据发送成功");
                callbackContext.success("send data success");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendFingerprint: 指纹串口数据发送失败：" + e.toString());
            callbackContext.error("send data error");
        }
    }

    public void readFingerprint(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (inputStream.available() <= 0) {
                        Thread.sleep(200);
                    }else {
                        onDataReceived(inputStream, callbackContext);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    callbackContext.error("read fingerprint data error");
                    return;
                }
            }
        });
    }

    private void onDataReceived(InputStream inputStream, CallbackContext callbackContext) {
        try {
            FingerReceived received = new FingerReceived();
            while (true) {
                int head = inputStream.read();
                if (head == 0x3a) {
                    break;
                }
            }
            int type = inputStream.read();
            int reply = ((inputStream.read()&0xff)<<8) + (inputStream.read()&0xff);
            int length = ((inputStream.read()&0xff)<<8) + (inputStream.read()&0xff);
            int xor = inputStream.read();
            received.setType(type);
            received.setReply(reply);
            received.setLength(length);
            received.setXor(xor);

            byte[] content = new byte[length];
            inputStream.read(content);
            received.setContent(content);
            int sum = inputStream.read();
            received.setSum(sum);
            received.setResult(bytes2Int(content));

            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, new Gson().toJson(received)));
        } catch (IOException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "read fingerprint data error"));
        }
    }

    /**
     * 中断指令 中断采集图像任务
     */
    public void interrupt(CallbackContext callbackContext) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0x3a;
        bytes[1] = 0x00;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        bytes[6] = (byte) 0x3a;
        bytes[7] = (byte) 0x8b;
        sendFingerprint(bytes, callbackContext);
    }

    /**
     * 查询手指状态
     */
    public void checkFingerStatus(CallbackContext callbackContext) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0x3a;
        bytes[1] = 0x02;
        bytes[2] = (byte) 0xc0;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        bytes[6] = (byte) 0xf8;
        byte[] result = new byte[8];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        result[7] = getSum(bytes);
        sendFingerprint(result, callbackContext);
    }

    /**
     * 注册指纹 需要三步
     *
     * @param startId 16 byte
     * @param endId   16 byte
     */
    public void registerFinger(int step, byte startId[], byte endId[], CallbackContext callbackContext) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0x3a;
        bytes[1] = 0x03;
        bytes[2] = (byte) 0xa6;
        bytes[4] = 0x00;
        bytes[5] = 0x04;
        switch (step) {
            case 1:
                bytes[3] = (byte) 0xa0;
                bytes[6] = (byte) 0x3b;
                break;
            case 2:
                bytes[3] = (byte) 0x60;
                bytes[6] = (byte) 0xfb;
                break;
            case 3:
                bytes[3] = (byte) 0x1c;
                bytes[6] = (byte) 0x87;
                break;
        }
        bytes[7] = startId[0];
        bytes[8] = startId[1];
        bytes[9] = endId[0];
        bytes[10] = endId[1];
        byte[] result = new byte[12];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        result[11] = getSum(bytes);
        sendFingerprint(result, callbackContext);
    }

    /**
     * 匹配指纹 只匹配已注册指纹的主模板
     */
    public void matchFinger(byte startId[], byte endId[], CallbackContext callbackContext) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0x3a;
        bytes[1] = 0x04;
        bytes[2] = (byte) 0xa6;
        bytes[3] = (byte) 0x80;
        bytes[4] = 0x00;
        bytes[5] = 0x04;
        bytes[6] = (byte) 0x1c;
        bytes[7] = startId[0];
        bytes[8] = startId[1];
        bytes[9] = endId[0];
        bytes[10] = endId[1];
        byte[] result = new byte[12];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        result[11] = getSum(bytes);
        sendFingerprint(result, callbackContext);
    }

    /**
     * 删除指纹
     */
    public void deleteFinger(byte startId[], byte endId[], CallbackContext callbackContext) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0x3a;
        bytes[1] = 0x05;
        bytes[2] = (byte) 0xe0;
        bytes[3] = 0x00;
        bytes[4] = 0x00;
        bytes[5] = 0x04;
        bytes[6] = (byte) 0xdb;
        bytes[7] = startId[0];
        bytes[8] = startId[1];
        bytes[9] = endId[0];
        bytes[10] = endId[1];
        byte[] result = new byte[12];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        result[11] = getSum(bytes);
        sendFingerprint(result, callbackContext);
    }

    /**
     * byte数组转int
     * @param bytes
     * @return
     */
    private int bytes2Int(byte[] bytes){
        int value = ((bytes[0] & 0xff)<<8) | (bytes[1] & 0xff);
        return value;
    }

    /**
     * 计算前面7byte和扩展域的算术和，然后低字节取反
     * @param bytes
     * @return
     */
    private byte getSum(byte[] bytes) {
        if (bytes.length > 0) {
            byte sum = bytes[0];
            for (int i = 1; i < bytes.length; i++) {
                sum += bytes[i];
            }
            return (byte)(~sum);
        }
        return 0;
    }

    /**
     * int转byte数组
     * @param value
     * @return
     */
    public static byte[] int2Byte(int value) {
        byte[] result = new byte[2];
        result[0] = (byte)((value >> 8) & 0xFF);
        result[1] = (byte)(value & 0xFF);
        return result;
    }

}
