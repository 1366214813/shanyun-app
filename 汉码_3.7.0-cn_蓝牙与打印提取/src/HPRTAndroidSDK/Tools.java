package HPRTAndroidSDK;

import LZO_Compress.LZOCompress;
import android.util.Log;
import com.baidu.ocr.sdk.utils.LogUtil;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import utils.ConvertUtil;

/* JADX INFO: loaded from: classes.dex */
public class Tools {
    public static long[] crc32tab = {0, 1996959894, 3993919788L, 2567524794L, 124634137, 1886057615, 3915621685L, 2657392035L, 249268274, 2044508324, 3772115230L, 2547177864L, 162941995, 2125561021, 3887607047L, 2428444049L, 498536548, 1789927666, 4089016648L, 2227061214L, 450548861, 1843258603, 4107580753L, 2211677639L, 325883990, 1684777152, 4251122042L, 2321926636L, 335633487, 1661365465, 4195302755L, 2366115317L, 997073096, 1281953886, 3579855332L, 2724688242L, 1006888145, 1258607687, 3524101629L, 2768942443L, 901097722, 1119000684, 3686517206L, 2898065728L, 853044451, 1172266101, 3705015759L, 2882616665L, 651767980, 1373503546, 3369554304L, 3218104598L, 565507253, 1454621731, 3485111705L, 3099436303L, 671266974, 1594198024, 3322730930L, 2970347812L, 795835527, 1483230225, 3244367275L, 3060149565L, 1994146192, 31158534, 2563907772L, 4023717930L, 1907459465, 112637215, 2680153253L, 3904427059L, 2013776290, 251722036, 2517215374L, 3775830040L, 2137656763, 141376813, 2439277719L, 3865271297L, 1802195444, 476864866, 2238001368L, 4066508878L, 1812370925, 453092731, 2181625025L, 4111451223L, 1706088902, 314042704, 2344532202L, 4240017532L, 1658658271, 366619977, 2362670323L, 4224994405L, 1303535960, 984961486, 2747007092L, 3569037538L, 1256170817, 1037604311, 2765210733L, 3554079995L, 1131014506, 879679996, 2909243462L, 3663771856L, 1141124467, 855842277, 2852801631L, 3708648649L, 1342533948, 654459306, 3188396048L, 3373015174L, 1466479909, 544179635, 3110523913L, 3462522015L, 1591671054, 702138776, 2966460450L, 3352799412L, 1504918807, 783551873, 3082640443L, 3233442989L, 3988292384L, 2596254646L, 62317068, 1957810842, 3939845945L, 2647816111L, 81470997, 1943803523, 3814918930L, 2489596804L, 225274430, 2053790376, 3826175755L, 2466906013L, 167816743, 2097651377, 4027552580L, 2265490386L, 503444072, 1762050814, 4150417245L, 2154129355L, 426522225, 1852507879, 4275313526L, 2312317920L, 282753626, 1742555852, 4189708143L, 2394877945L, 397917763, 1622183637, 3604390888L, 2714866558L, 953729732, 1340076626, 3518719985L, 2797360999L, 1068828381, 1219638859, 3624741850L, 2936675148L, 906185462, 1090812512, 3747672003L, 2825379669L, 829329135, 1181335161, 3412177804L, 3160834842L, 628085408, 1382605366, 3423369109L, 3138078467L, 570562233, 1426400815, 3317316542L, 2998733608L, 733239954, 1555261956, 3268935591L, 3050360625L, 752459403, 1541320221, 2607071920L, 3965973030L, 1969922972, 40735498, 2617837225L, 3943577151L, 1913087877, 83908371, 2512341634L, 3803740692L, 2075208622, 213261112, 2463272603L, 3855990285L, 2094854071, 198958881, 2262029012L, 4057260610L, 1759359992, 534414190, 2176718541L, 4139329115L, 1873836001, 414664567, 2282248934L, 4279200368L, 1711684554, 285281116, 2405801727L, 4167216745L, 1634467795, 376229701, 2685067896L, 3608007406L, 1308918612, 956543938, 2808555105L, 3495958263L, 1231636301, 1047427035, 2932959818L, 3654703836L, 1088359270, 936918000, 2847714899L, 3736837829L, 1202900863, 817233897, 3183342108L, 3401237130L, 1404277552, 615818150, 3134207493L, 3453421203L, 1423857449, 601450431, 3009837614L, 3294710456L, 1567103746, 711928724, 3020668471L, 3272380065L, 1510334235, 755167117};
    private static long crcKey = 4294967295L;

    public static List<byte[]> addBytesToList(byte[] dataBytes) {
        ArrayList arrayList = new ArrayList();
        int length = dataBytes.length / 2016;
        int i = 0;
        while (i < length) {
            byte[] bArr = new byte[2016];
            System.arraycopy(dataBytes, i * 2016, bArr, 0, 2016);
            arrayList.add(bArr);
            i++;
        }
        int i2 = i * 2016;
        int length2 = dataBytes.length - i2;
        if (length2 > 0) {
            byte[] bArr2 = new byte[length2];
            System.arraycopy(dataBytes, i2, bArr2, 0, length2);
            arrayList.add(bArr2);
        }
        return arrayList;
    }

    public static List<byte[]> addBytesToList(byte[] dataBytes, int separatedCount) {
        ArrayList arrayList = new ArrayList();
        int length = dataBytes.length / separatedCount;
        int i = 0;
        while (i < length) {
            byte[] bArr = new byte[separatedCount];
            System.arraycopy(dataBytes, i * separatedCount, bArr, 0, separatedCount);
            arrayList.add(bArr);
            i++;
        }
        int i2 = i * separatedCount;
        int length2 = dataBytes.length - i2;
        if (length2 > 0) {
            byte[] bArr2 = new byte[length2];
            System.arraycopy(dataBytes, i2, bArr2, 0, length2);
            arrayList.add(bArr2);
        }
        return arrayList;
    }

    public static byte[] packPerBytes(List<byte[]> bytes, int i, int packageSize) {
        ArrayList arrayList = new ArrayList();
        arrayList.add((byte) 27);
        arrayList.add((byte) 28);
        arrayList.add((byte) 38);
        arrayList.add((byte) 32);
        arrayList.add((byte) 86);
        arrayList.add((byte) 49);
        arrayList.add((byte) 32);
        arrayList.add((byte) 100);
        arrayList.add((byte) 111);
        arrayList.add((byte) 32);
        arrayList.add((byte) 34);
        arrayList.add((byte) 111);
        arrayList.add((byte) 116);
        arrayList.add((byte) 97);
        arrayList.add((byte) 34);
        arrayList.add((byte) 13);
        arrayList.add((byte) 10);
        for (byte b : intTo4Bytes(packageSize * i)) {
            arrayList.add(Byte.valueOf(b));
        }
        for (byte b2 : intTo4Bytes(bytes.get(i).length)) {
            arrayList.add(Byte.valueOf(b2));
        }
        for (byte b3 : intTo4Bytes((int) crc32(bytes.get(i), bytes.get(i).length))) {
            arrayList.add(Byte.valueOf(b3));
        }
        for (byte b4 : bytes.get(i)) {
            arrayList.add(Byte.valueOf(b4));
        }
        return byteListToByteArray(arrayList);
    }

    public static byte[] byteListToByteArray(List<Byte> perBytes) {
        int size = perBytes.size();
        byte[] bArr = new byte[size];
        for (int i = 0; i < size; i++) {
            bArr[i] = perBytes.get(i).byteValue();
        }
        return bArr;
    }

    public static byte[] intTo4Bytes(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
    }

    public static int getIntBy2Byte(byte[] bytes) {
        int i = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            i += (bytes[i2] & 255) << ((1 - i2) * 8);
        }
        return i;
    }

    public static int getIntBy4Byte(byte[] bytes) {
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            i += (bytes[i2] & 255) << (i2 * 8);
        }
        return i;
    }

    public static long crc32(byte[] buffer, int len) {
        return crc32(crcKey, buffer, len);
    }

    public static long crc32(long crc, byte[] buffer, int len) {
        long j = crc ^ 4294967295L;
        for (int i = 0; i < len; i++) {
            j = (j >>> 8) ^ crc32tab[(int) ((((long) buffer[i]) ^ j) & 255)];
        }
        long j2 = j ^ 4294967295L;
        System.out.println("CRC:" + j2);
        return j2;
    }

    public static int getNumber(String data) {
        int i = 0;
        for (int i2 = 0; i2 < data.length(); i2++) {
            if (Character.isDigit(data.charAt(i2))) {
                i = i2 + 1;
            } else if (i != 0) {
                return Integer.parseInt(data.substring(0, i));
            }
        }
        return 0;
    }

    public static WIFIBean setWifiBean(WIFIBean wifiBean, String data) {
        while (!data.isEmpty()) {
            byte[] bytes = data.getBytes();
            int number = getNumber(data);
            if (number != 0) {
                int length = ("" + number).length();
                byte[] bArr = new byte[number];
                if (data.getBytes().length < number + length) {
                    break;
                }
                System.arraycopy(bytes, length, bArr, 0, number);
                String str = new String(bArr);
                Log.d("Print", "substring: ".concat(str));
                if (str.startsWith("WD")) {
                    wifiBean.setMac(str.substring(3));
                }
                if (str.startsWith("WM")) {
                    wifiBean.setMode(str.substring(3));
                }
                if (str.startsWith("WS")) {
                    wifiBean.setSsid(str.substring(3));
                }
                if (str.startsWith("WI")) {
                    wifiBean.setIp(str.substring(3));
                }
                if (str.startsWith("WP")) {
                    wifiBean.setPassword(str.substring(3));
                }
                if (str.startsWith("WA")) {
                    wifiBean.setCertificationModel(Integer.parseInt(str.substring(3)));
                }
                data = data.substring(str.length() + length);
            } else {
                break;
            }
        }
        return wifiBean;
    }

    public static byte[] mergeBytes(byte[] arg1, byte[] arg2) {
        byte[] bArr = new byte[arg1.length + arg2.length];
        System.arraycopy(arg1, 0, bArr, 0, arg1.length);
        System.arraycopy(arg2, 0, bArr, arg1.length, arg2.length);
        return bArr;
    }

    public static byte[] listToBytes(List<byte[]> bytes) {
        if (bytes == null || bytes.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < bytes.size(); i++) {
            for (int i2 = 0; i2 < bytes.get(i).length; i2++) {
                arrayList.add(Byte.valueOf(bytes.get(i)[i2]));
            }
        }
        return byteListToByteArray(arrayList);
    }

    public static byte[] mergeBytes(byte[]... values) {
        int length = 0;
        for (byte[] bArr : values) {
            length += bArr.length;
        }
        byte[] bArr2 = new byte[length];
        int length2 = 0;
        for (byte[] bArr3 : values) {
            System.arraycopy(bArr3, 0, bArr2, length2, bArr3.length);
            length2 += bArr3.length;
        }
        return bArr2;
    }

    public static String byteToHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        for (byte b : data) {
            sb.append(String.format("%02X", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static String byteToHexWithEmpty(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        for (byte b : data) {
            sb.append(String.format("%02X ", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static byte[] hexToByte(String path) {
        String strReplaceAll = Pattern.compile("\\s*|\t|\r|\n").matcher(path).replaceAll("");
        int length = strReplaceAll.length() / 2;
        if (length * 2 < strReplaceAll.length()) {
            length++;
        }
        String[] strArr = new String[length];
        int i = 0;
        for (int i2 = 0; i2 < strReplaceAll.length(); i2++) {
            if (i2 % 2 == 0) {
                strArr[i] = "" + strReplaceAll.charAt(i2);
            } else {
                strArr[i] = strArr[i] + strReplaceAll.charAt(i2);
                i++;
            }
        }
        byte[] bArr = new byte[length];
        for (int i3 = 0; i3 < length; i3++) {
            if (strArr[i3].length() == 2) {
                bArr[i3] = Integer.valueOf(strArr[i3], 16).byteValue();
            }
        }
        return bArr;
    }

    public static String getStr(byte[] data, int start, int end, int tag) {
        if (start <= data.length && end <= data.length && start <= end) {
            int i = (end - start) + 1;
            byte[] bArr = new byte[i];
            int i2 = 0;
            while (true) {
                if (i2 >= i) {
                    i2 = 0;
                    break;
                }
                byte b = data[(start - 1) + i2];
                if (tag == b) {
                    break;
                }
                bArr[i2] = b;
                i2++;
            }
            if (i2 != 0) {
                i = i2;
            }
            byte[] bArr2 = new byte[i];
            for (int i3 = 0; i3 < i; i3++) {
                bArr2[i3] = bArr[i3];
            }
            try {
                return new String(bArr2, "GB2312");
            } catch (UnsupportedEncodingException unused) {
            }
        }
        return "";
    }

    public static byte[] getBytes(byte[] data, int start, int end, int tag) {
        if (start > data.length || end > data.length || start > end) {
            return null;
        }
        int i = (end - start) + 1;
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (true) {
            if (i2 >= i) {
                i2 = 0;
                break;
            }
            byte b = data[(start - 1) + i2];
            if (tag == b) {
                break;
            }
            bArr[i2] = b;
            i2++;
        }
        if (i2 != 0) {
            i = i2;
        }
        byte[] bArr2 = new byte[i];
        for (int i3 = 0; i3 < i; i3++) {
            bArr2[i3] = bArr[i3];
        }
        return bArr2;
    }

    public static int convertNum(String str) {
        String[] strArr = {"A", "B", "C", LogUtil.D, "E", "F"};
        HashMap map = new HashMap();
        int iIntValue = 0;
        for (int i = 0; i <= 9; i++) {
            map.put(i + "", Integer.valueOf(i));
        }
        for (int i2 = 10; i2 <= 15; i2++) {
            map.put(strArr[i2 - 10], Integer.valueOf(i2));
        }
        int length = str.length();
        String[] strArr2 = new String[length];
        int i3 = 0;
        while (i3 <= str.length() - 1) {
            int i4 = i3 + 1;
            strArr2[i3] = str.substring(i3, i4);
            i3 = i4;
        }
        int i5 = 2;
        while (true) {
            if (i5 > length - 1) {
                return iIntValue;
            }
            iIntValue = (int) (((double) iIntValue) + (((double) ((Integer) map.get(strArr2[i5])).intValue()) * Math.pow(16.0d, r4 - i5)));
            i5++;
        }
    }

    public static byte[] intTo2Bytes(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255)};
    }

    public static List<byte[]> addBytesToList3(byte[] dataBytes) {
        ArrayList arrayList = new ArrayList();
        int length = dataBytes.length / 9344;
        int i = 0;
        while (i < length) {
            byte[] bArr = new byte[9344];
            System.arraycopy(dataBytes, i * 9344, bArr, 0, 9344);
            arrayList.add(bArr);
            i++;
        }
        int i2 = i * 9344;
        int length2 = dataBytes.length - i2;
        if (length2 > 0) {
            byte[] bArr2 = new byte[length2];
            System.arraycopy(dataBytes, i2, bArr2, 0, length2);
            arrayList.add(bArr2);
        }
        return arrayList;
    }

    public static byte[] packPerBytes3(List<byte[]> bytes, int i) {
        byte[] bArr = bytes.get(i);
        return ConvertUtil.byteMergerAll(new byte[]{27, 18, 118}, intTo2Bytes(i), intTo2Bytes(bArr.length), bArr, intTo4Bytes((int) crc32(bytes.get(i), bytes.get(i).length)));
    }

    public static byte[] packPerBytes4(List<byte[]> bytes, int i) {
        byte[] bArr = {27, 18, 118};
        byte[] bArr2 = bytes.get(i);
        byte[] bArr3 = new byte[bArr2.length * 2];
        int[] iArr = new int[1];
        new LZOCompress().lzoCompressData(bArr2, bArr2.length, bArr3, iArr, new byte[64000]);
        int i2 = iArr[0];
        byte[] bArr4 = new byte[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            bArr4[i3] = bArr3[i3];
        }
        return ConvertUtil.byteMergerAll(bArr, intTo2Bytes(i), intTo2Bytes(i2), bArr4, intTo4Bytes((int) crc32(bArr4, i2)));
    }
}
