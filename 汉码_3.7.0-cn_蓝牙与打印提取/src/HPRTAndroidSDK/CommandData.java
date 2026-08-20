package HPRTAndroidSDK;

import com.taobao.weex.el.parse.Operators;
import org.apache.poi.ss.formula.ptg.RefNPtg;

/* JADX INFO: loaded from: classes.dex */
public class CommandData {

    public static class CommandString {
        public static String rfid_calibrate = "rfid_calibrate";
        public static String rfid_print = "rfid_print";
        public static String rfid_wr = "rfid_wr";
    }

    public static byte[] getWifiCommand() {
        return new byte[]{27, 28, 38, 32, 86, 49, 32, 103, 101, 116, 118, 97, 108, 32, 34, 97, 117, 116, 111, 95, 119, 105, 102, 105, 34, 13, 10};
    }

    public static byte[] setWifiCommand(WIFIBean wifiBean) {
        String str = "WM:" + wifiBean.getMode();
        byte[] wifiData = getWifiData(str.length() + str);
        StringBuilder sb = new StringBuilder("WS:");
        sb.append(wifiBean.getSsid());
        String string = sb.toString();
        byte[] wifiData2 = getWifiData(string.getBytes().length + string);
        StringBuilder sb2 = new StringBuilder("WP:");
        sb2.append(wifiBean.getPassword());
        String string2 = sb2.toString();
        byte[] wifiData3 = getWifiData(string2.getBytes().length + string2);
        StringBuilder sb3 = new StringBuilder("WA:");
        sb3.append(wifiBean.getCertificationModel());
        String string3 = sb3.toString();
        return Tools.mergeBytes(Tools.mergeBytes(Tools.mergeBytes(wifiData, wifiData2), wifiData3), getWifiData(string3.length() + string3));
    }

    private static byte[] getWifiData(String data) {
        return Tools.mergeBytes(Tools.mergeBytes(new byte[]{27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 118, 97, 108, 32, 34, 97, 117, 116, 111, 95, 119, 105, 102, 105, 34, 32, 34}, data.getBytes()), new byte[]{34, 13, 10});
    }

    public static byte[] setRestartWifi() {
        return new byte[]{27, 28, 38, 32, 86, 49, 32, 100, 111, 32, 34, 114, 101, 115, 116, 97, 114, 116, 95, 119, 105, 102, 105, 34, 13, 10};
    }

    public static byte[] commandDoString(String command) {
        return Tools.mergeBytes(new byte[]{27, 28, 38, 32, 86, 49, 32, 100, 111, 32, 34}, command.getBytes(), new byte[]{34, 13, 10});
    }

    public static byte[] commandSetString(String command, String... val) {
        byte[] bArr = {27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 118, 97, 108, 32, 34};
        byte[] bArr2 = {34, 32, 34};
        byte[] bArr3 = {34, 13, 10};
        byte[] bArrMergeBytes = new byte[0];
        for (int i = 0; i < val.length; i++) {
            try {
                bArrMergeBytes = Tools.mergeBytes(bArrMergeBytes, val[i].getBytes("GB2312"));
                if (i != val.length - 1) {
                    bArrMergeBytes = Tools.mergeBytes(bArrMergeBytes, new byte[]{RefNPtg.sid});
                }
            } catch (Exception unused) {
                return null;
            }
        }
        return Tools.mergeBytes(bArr, command.getBytes(), bArr2, bArrMergeBytes, bArr3);
    }

    public static byte[] getLayerCommand(int layer) {
        return ("SETLAYER " + layer + "\r\n").getBytes();
    }

    public static byte[] getStartCPCLCommand(int height, int number) {
        return ("! 0 300 300 " + height + Operators.SPACE_STR + number + "\r\n").getBytes();
    }

    public static byte[] getToneCPCLCommand(int density) {
        return ("TONE " + density + "\r\n").getBytes();
    }

    public static byte[] getFormCPCLCommand() {
        return "FORM\r\n".getBytes();
    }

    public static byte[] getPrintCPCLCommand() {
        return "PRINT\r\n".getBytes();
    }
}
