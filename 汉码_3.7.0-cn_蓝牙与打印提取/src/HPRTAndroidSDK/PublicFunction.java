package HPRTAndroidSDK;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Environment;
import com.fasterxml.aalto.util.CharsetNames;
import com.taobao.weex.ui.module.WXModalUIModule;
import io.dcloud.common.adapter.util.DeviceInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.openxml4j.opc.ContentTypes;

/* JADX INFO: loaded from: classes.dex */
public class PublicFunction {
    public static final String PREFS_NAME = "MyPrefsFile";
    private List<String> PathDisplay;
    private List<String> PathTag;
    private ArrayList<HashMap<String, String>> PicList;
    private String SDPath;
    private Context context;

    public PublicFunction() {
        this.context = null;
        this.PathDisplay = new ArrayList();
        this.PathTag = new ArrayList();
        this.PicList = new ArrayList<>();
        this.SDPath = "";
    }

    public PublicFunction(Context context) {
        this.context = null;
        this.PathDisplay = new ArrayList();
        this.PathTag = new ArrayList();
        this.PicList = new ArrayList<>();
        this.SDPath = "";
        this.context = context;
    }

    public String EnableDevice(String strDeviceType) {
        String strEnableWiFi;
        if (!strDeviceType.equals("WiFi")) {
            strEnableWiFi = "";
        } else {
            strEnableWiFi = EnableWiFi();
        }
        return strDeviceType.equals("Bluetooth") ? EnableBluetooth() : strEnableWiFi;
    }

    private String EnableWiFi() {
        try {
            Context context = this.context;
            if (context != null) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                    return WXModalUIModule.OK;
                }
                return WXModalUIModule.OK;
            }
            return "Invalid Context";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String EnableBluetooth() {
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!defaultAdapter.isEnabled()) {
                defaultAdapter.enable();
                return WXModalUIModule.OK;
            }
            return WXModalUIModule.OK;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String DisableDevice(String strDeviceType) {
        String strDisableWiFi;
        if (!strDeviceType.equals("WiFi")) {
            strDisableWiFi = "";
        } else {
            strDisableWiFi = DisableWiFi();
        }
        return strDeviceType.equals("Bluetooth") ? DisableBluetooth() : strDisableWiFi;
    }

    private String DisableWiFi() {
        try {
            Context context = this.context;
            if (context != null) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    return WXModalUIModule.OK;
                }
                return WXModalUIModule.OK;
            }
            return "Invalid Context";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String DisableBluetooth() {
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.isEnabled()) {
                defaultAdapter.disable();
                return WXModalUIModule.OK;
            }
            return WXModalUIModule.OK;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void ShowMessageDialog(String strTitleText, String strMessage) {
        try {
            new AlertDialog.Builder(this.context).setTitle(strTitleText).setMessage(strMessage).setPositiveButton("确定", new DialogInterface.OnClickListener() { // from class: HPRTAndroidSDK.PublicFunction.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void WriteSharedPreferencesData(String strName, String strValue) {
        this.context.getSharedPreferences(PREFS_NAME, 0).edit().putString(strName, strValue).commit();
    }

    public String ReadSharedPreferencesData(String strName) {
        return this.context.getSharedPreferences(PREFS_NAME, 0).getString(strName, "");
    }

    public int CountSubString(String strSource, String strFind) {
        int iIndexOf = strSource.indexOf(strFind);
        while (iIndexOf != -1) {
            iIndexOf = strSource.indexOf(strFind, iIndexOf + 2);
        }
        return 0;
    }

    public int GetStringIndex(String strSource, String strFind, int intFindTime, boolean blnNoStringReturnLenth) {
        int iIndexOf = -1;
        for (int i = 0; i < intFindTime; i++) {
            iIndexOf = strSource.indexOf(strFind, iIndexOf + 1);
            if (iIndexOf == -1) {
                break;
            }
        }
        if (iIndexOf != -1) {
            return iIndexOf;
        }
        if (blnNoStringReturnLenth) {
            return strSource.length();
        }
        return -1;
    }

    public String CreateRepeatString(String strRepeat, int intRepeatTimes) {
        String str = "";
        for (int i = 0; i < intRepeatTimes; i++) {
            str = str + strRepeat;
        }
        return str;
    }

    public String ReverseString(String strIn) {
        String str = "";
        if (strIn.length() > 0) {
            for (int length = strIn.length(); length > 0; length--) {
                str = str + strIn.substring(length - 1, length);
            }
        }
        return str;
    }

    public String ReadTxtFile(String strFilePath) {
        if (strFilePath.substring(0, 7).equals(DeviceInfo.FILE_PROTOCOL)) {
            strFilePath = strFilePath.substring(7);
        }
        File file = new File(strFilePath);
        if (!file.exists() || file.isDirectory()) {
            return "false";
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strSubstring = "";
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                strSubstring = strSubstring + line + "\n";
            }
            if (-17 == strSubstring.substring(0, 1).getBytes()[0]) {
                strSubstring = strSubstring.substring(1);
            }
            fileInputStream.close();
            return strSubstring;
        } catch (FileNotFoundException | IOException unused) {
            return "false";
        }
    }

    public static boolean ExistSDCard() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public ArrayList<HashMap<String, String>> GetSDPicture() {
        try {
            this.PicList.clear();
            this.PathDisplay.clear();
            this.PathTag.clear();
            if (!ExistSDCard()) {
                return null;
            }
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            this.SDPath = externalStorageDirectory.getAbsolutePath();
            HavePicture(externalStorageDirectory);
            Collections.sort(this.PathDisplay, String.CASE_INSENSITIVE_ORDER);
            Collections.sort(this.PathTag, String.CASE_INSENSITIVE_ORDER);
            for (int i = 0; i < this.PathTag.size(); i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("PathDisplay", this.PathDisplay.get(i));
                map.put("PathTag", this.PathTag.get(i));
                this.PicList.add(map);
            }
            return this.PicList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private boolean HavePicture(File InputFile) {
        try {
            File[] fileArrListFiles = InputFile.listFiles();
            for (File file : fileArrListFiles) {
                if (file.isDirectory() && !file.getName().subSequence(0, 1).equals(".") && HavePicture(file)) {
                    this.PathDisplay.add(file.getAbsolutePath().replace(this.SDPath, ""));
                    this.PathTag.add(file.getAbsolutePath());
                }
            }
            for (File file2 : fileArrListFiles) {
                if (!file2.isDirectory() && IsPicture(file2)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
    }

    private boolean IsPicture(File InputFile) {
        String name = InputFile.getName();
        try {
            int iLastIndexOf = name.lastIndexOf(".");
            if (iLastIndexOf <= 0) {
                return false;
            }
            String strSubstring = name.substring(iLastIndexOf + 1, name.length());
            if (!strSubstring.toLowerCase().equals(ContentTypes.EXTENSION_JPG_1) && !strSubstring.toLowerCase().equals(ContentTypes.EXTENSION_JPG_2) && !strSubstring.toLowerCase().equals("bmp") && !strSubstring.toLowerCase().equals(ContentTypes.EXTENSION_PNG)) {
                if (!strSubstring.toLowerCase().equals(".gif")) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

    public byte[] sysCopy(List<byte[]> srcArrays) {
        Iterator<byte[]> it2 = srcArrays.iterator();
        int length = 0;
        while (it2.hasNext()) {
            length += it2.next().length;
        }
        byte[] bArr = new byte[length];
        int length2 = 0;
        for (byte[] bArr2 : srcArrays) {
            System.arraycopy(bArr2, 0, bArr, length2, bArr2.length);
            length2 += bArr2.length;
        }
        return bArr;
    }

    public byte[] ArrayCopy(byte[] A, int APos, byte[] B, int BPos, int Length) {
        byte[] bArr = new byte[(APos + Length) - BPos];
        if (A != null) {
            System.arraycopy(A, 0, bArr, 0, APos);
        }
        System.arraycopy(B, BPos, bArr, APos, Length);
        return bArr;
    }

    public int getCodePageIndex(String codePage) {
        HashMap map = new HashMap();
        map.put("Default", 0);
        map.put("Chinese Simplified", 0);
        map.put("Chinese Traditional", 0);
        map.put("PC437(USA:Standard Europe)", 0);
        map.put("KataKana", 1);
        map.put("PC850(Multilingual)", 2);
        map.put("PC860(Portuguese)", 3);
        map.put("PC863(Canadian-French)", 4);
        map.put("PC865(Nordic)", 5);
        map.put("PC857(Turkish)", 13);
        map.put("PC737(Greek)", 14);
        map.put("ISO8859-7(Greek)", 15);
        map.put("WCP1252", 16);
        map.put("PC866(Cyrillic #2)", 17);
        map.put("PC852(Latin 2)", 18);
        map.put("PC858(Euro)", 19);
        map.put("KU42", 20);
        map.put("TIS11(Thai)", 21);
        map.put("TIS18(Thai)", 26);
        map.put("PC720", 32);
        map.put("WPC775", 33);
        map.put("PC855(Cyrillic)", 33);
        map.put("PC862(Hebrew)", 36);
        map.put("PC864(Arabic)", 37);
        map.put("ISO8859-2(Latin2)", 39);
        map.put("ISO8859-15(Latin9)", 40);
        map.put("WPC1250", 45);
        map.put("WPC1251(Cyrillic)", 46);
        map.put("WPC1253", 47);
        map.put("WPC1254", 48);
        map.put("WPC1255", 49);
        map.put("WPC1256", 50);
        map.put("WPC1257", 51);
        map.put("WPC1258", 52);
        map.put("MIK(Cyrillic/Bulgarian)", 54);
        map.put("CP755(East Europe,Latvian 2)", 55);
        map.put("Iran", 56);
        map.put("Iran II", 57);
        map.put("Latvian", 58);
        map.put("ISO-8859-1(West Europe)", 59);
        map.put("ISO-8859-3(Latin 3)", 60);
        map.put("ISO-8859-4(Baltic)", 61);
        map.put("ISO-8859-5(Cyrillic)", 62);
        map.put("ISO-8859-6(Arabic)", 63);
        map.put("ISO-8859-8(Hebrew)", 64);
        map.put("ISO-8859-9(Turkish)", 65);
        map.put("PC856", 66);
        map.put("ABICOIM", 67);
        return ((Integer) map.get(codePage)).intValue();
    }

    public static String getLanguageEncode(String codePage) {
        HashMap map = new HashMap();
        map.put("Default", "gb2312");
        map.put("Chinese Simplified", "gb2312");
        map.put("Chinese Traditional", "big5");
        map.put("PC437(USA:Standard Europe)", "iso8859-1");
        map.put("KataKana", CharsetNames.CS_SHIFT_JIS);
        map.put("PC850(Multilingual)", "iso8859-3");
        map.put("PC860(Portuguese)", "iso8859-6");
        map.put("PC863(Canadian-French)", "iso8859-1");
        map.put("PC865(Nordic)", "iso8859-1");
        map.put("PC857(Turkish)", "IBM857");
        map.put("PC737(Greek)", "iso8859-7");
        map.put("ISO8859-7(Greek)", "iso8859-7");
        map.put("WCP1252", "iso8859-1");
        map.put("PC866(Cyrillic #2)", "iso8859-5");
        map.put("PC852(Latin 2)", "iso8859-2");
        map.put("PC858(Euro)", "iso8859-15");
        map.put("KU42", "ISO8859-11");
        map.put("TIS11(Thai)", "ISO8859-11");
        map.put("TIS18(Thai)", "ISO8859-11");
        map.put("PC720", "iso8859-6");
        map.put("WPC775", "iso8859-1");
        map.put("PC855(Cyrillic)", "iso8859-5");
        map.put("PC862(Hebrew)", "iso8859-8");
        map.put("PC864(Arabic)", "iso8859-6");
        map.put("ISO8859-2(Latin2)", "iso8859-2");
        map.put("ISO8859-15(Latin9)", "iso8859-15");
        map.put("WPC1250", "iso8859-2");
        map.put("WPC1251(Cyrillic)", "iso8859-5");
        map.put("WPC1253", "iso8859-7");
        map.put("WPC1254", "iso8859-3");
        map.put("WPC1255", "iso8859-8");
        map.put("WPC1256", "iso8859-6");
        map.put("WPC1257", "iso8859-1");
        map.put("WPC1258", "bg2312");
        map.put("MIK(Cyrillic/Bulgarian)", "iso8859-15");
        map.put("CP755(East Europe,Latvian 2)", "iso8859-5");
        map.put("Iran", "iso8859-6");
        map.put("Iran II", "iso8859-6");
        map.put("Latvian", "iso8859-4");
        map.put("ISO-8859-1(West Europe)", "iso8859-1");
        map.put("ISO-8859-3(Latin 3)", "iso8859-3");
        map.put("ISO-8859-4(Baltic)", "iso8859-4");
        map.put("ISO-8859-5(Cyrillic)", "iso8859-5");
        map.put("ISO-8859-6(Arabic)", "iso8859-6");
        map.put("ISO-8859-8(Hebrew)", "iso8859-8");
        map.put("ISO-8859-9(Turkish)", "iso8859-9");
        map.put("PC856", "iso8859-8");
        map.put("ABICOIM", "iso8859-15");
        return (String) map.get(codePage);
    }
}
