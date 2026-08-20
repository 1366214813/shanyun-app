package HPRTAndroidSDK;

import com.blankj.utilcode.util.LogUtils;
import utils.ByteUtils;

/* JADX INFO: loaded from: classes.dex */
public class DataFilter {
    public static final String DISCONNECT_BT = "Disconnect_BT";
    public static final String DITHERING_FINISH = "dithering_finish";
    public static final String auto_status = "70 6F 6F 6C 69 5F 73 74 61 3D ";
    public static final String HEAD_POOLI_STA = "pooli_sta=";
    public static byte[] pooliStaHead = HEAD_POOLI_STA.getBytes();
    public static final String HEAD_POOLI_STUDY = "pooli_study=";
    public static byte[] pooliStudyHead = HEAD_POOLI_STUDY.getBytes();
    public static final String HEAD_NFC_UID = "nfc_uid";
    public static byte[] nfcUidHead = HEAD_NFC_UID.getBytes();
    public static final String HEAD_NFC_VERIFY_INFO = "nfc_verify_info=";
    static byte[] nfcVerifyInfoHead = HEAD_NFC_VERIFY_INFO.getBytes();
    public static final String HEAD_NFC_RIBBON_TYPE = "nfc_ribbon_type";
    static byte[] nfcRibbonTypeHead = HEAD_NFC_RIBBON_TYPE.getBytes();
    public static final String HEAD_NFC_RIBBON_LEN = "nfc_ribbon_len=";
    static byte[] nfcRibbonLenHead = HEAD_NFC_RIBBON_LEN.getBytes();
    public static final String HEAD_NFC_RIBBON_INFO = "nfc_ribbon_info=";
    static byte[] nfcRibbonInfoHead = HEAD_NFC_RIBBON_INFO.getBytes();
    public static final String MT_STATE = "rtsts";
    static byte[] mtStateHead = MT_STATE.getBytes();
    public static final String MT_RBBND = "rbbnd";
    static byte[] mtRbbndHead = MT_RBBND.getBytes();
    public static final String MT_STATUS = "status:";
    static byte[] mtStatusHead = MT_STATUS.getBytes();

    public static byte[] filter(byte[] data, OnDataFilterListener filterListener) {
        if (data.length > 0) {
            LogUtils.d("---test0---data=" + ByteUtils.INSTANCE.bytetohex(data));
        }
        byte[] bArr = null;
        byte[] bArr2 = null;
        while (getByteIndexOf(data, pooliStaHead) != -1) {
            bArr2 = new byte[1];
            int byteIndexOf = getByteIndexOf(data, pooliStaHead);
            LogUtils.d("---test1---start_pooliStaHead=" + byteIndexOf);
            System.arraycopy(data, pooliStaHead.length + byteIndexOf, bArr2, 0, 1);
            LogUtils.d("---test1---status=" + ByteUtils.INSTANCE.bytetohex(bArr2));
            data = removeRange(data, byteIndexOf, byteIndexOf + 1 + pooliStaHead.length);
            LogUtils.d("---test1---data=" + ByteUtils.INSTANCE.bytetohex(data));
        }
        if (bArr2 != null && filterListener != null) {
            filterListener.onFilter(HEAD_POOLI_STA, Tools.byteToHex(bArr2), bArr2);
        }
        byte[] bArr3 = null;
        while (getByteIndexOf(data, pooliStudyHead) != -1) {
            bArr3 = new byte[1];
            int byteIndexOf2 = getByteIndexOf(data, pooliStudyHead);
            LogUtils.d("---test2---start_pooliStudyHead=" + byteIndexOf2);
            System.arraycopy(data, pooliStudyHead.length + byteIndexOf2, bArr3, 0, 1);
            LogUtils.d("---test2---resultStudy=" + ByteUtils.INSTANCE.bytetohex(bArr3));
            data = removeRange(data, byteIndexOf2, byteIndexOf2 + 1 + pooliStudyHead.length);
        }
        if (bArr3 != null && filterListener != null) {
            filterListener.onFilter(HEAD_POOLI_STUDY, Tools.byteToHex(bArr3), bArr3);
        }
        byte[] bArr4 = null;
        while (getByteIndexOf(data, nfcUidHead) != -1) {
            byte[] bArr5 = new byte[4];
            int byteIndexOf3 = getByteIndexOf(data, nfcUidHead);
            LogUtils.d("---test2---start_nfcUidHead=" + byteIndexOf3);
            System.arraycopy(data, nfcUidHead.length + byteIndexOf3, bArr5, 0, 4);
            LogUtils.d("---test2---uid=" + ByteUtils.INSTANCE.bytetohex(bArr5));
            data = removeRange(data, byteIndexOf3, byteIndexOf3 + 5 + nfcUidHead.length);
            LogUtils.d("---test2---data=" + ByteUtils.INSTANCE.bytetohex(data));
            bArr4 = bArr5;
        }
        if (bArr4 != null && filterListener != null) {
            filterListener.onFilter(HEAD_NFC_UID, Tools.byteToHex(bArr4), bArr4);
        }
        byte[] bArr6 = null;
        while (getByteIndexOf(data, nfcRibbonTypeHead) != -1) {
            byte[] bArr7 = new byte[16];
            int byteIndexOf4 = getByteIndexOf(data, nfcRibbonTypeHead);
            LogUtils.d("---test3---start_nfcRibbonTypeHead=" + byteIndexOf4);
            if (data.length < 32) {
                data = removeRange(data, byteIndexOf4, nfcRibbonTypeHead.length + byteIndexOf4 + 1);
                LogUtils.d("---test3---data=" + ByteUtils.INSTANCE.bytetohex(data));
                if (data.length == 0 && filterListener != null) {
                    filterListener.onFilter(HEAD_NFC_RIBBON_TYPE, "", new byte[0]);
                }
            } else {
                byte[] bArr8 = nfcRibbonTypeHead;
                System.arraycopy(data, bArr8.length + byteIndexOf4, bArr7, 0, bArr8.length);
                LogUtils.d("---test3---ribbonType=" + ByteUtils.INSTANCE.bytetohex(bArr7));
                data = removeRange(data, byteIndexOf4, byteIndexOf4 + 17 + nfcRibbonTypeHead.length);
                LogUtils.d("---test3---data2=" + ByteUtils.INSTANCE.bytetohex(data));
                bArr6 = bArr7;
            }
        }
        if (bArr6 != null && filterListener != null) {
            filterListener.onFilter(HEAD_NFC_RIBBON_TYPE, Tools.byteToHex(bArr6), bArr6);
        }
        byte[] bArr9 = null;
        while (getByteIndexOf(data, mtStateHead) != -1) {
            bArr9 = new byte[18];
            byte[] bArr10 = new byte[12];
            int byteIndexOf5 = getByteIndexOf(data, mtStateHead);
            LogUtils.d("---test4---start_mtStateHead=" + byteIndexOf5);
            System.arraycopy(data, mtStateHead.length + byteIndexOf5, bArr10, 0, 12);
            LogUtils.d("---test4---mtStateData=" + ByteUtils.INSTANCE.bytetohex(bArr10));
            System.arraycopy(data, byteIndexOf5, bArr9, 0, 18);
            data = removeRange(data, byteIndexOf5, byteIndexOf5 + 13 + mtStateHead.length);
            LogUtils.d("---test4---data=" + ByteUtils.INSTANCE.bytetohex(data));
        }
        if (bArr9 != null && filterListener != null) {
            filterListener.onFilter(MT_STATE, Tools.byteToHex(bArr9), bArr9);
        }
        byte[] bArr11 = null;
        while (getByteIndexOf(data, mtRbbndHead) != -1) {
            bArr11 = new byte[18];
            byte[] bArr12 = new byte[12];
            int byteIndexOf6 = getByteIndexOf(data, mtRbbndHead);
            LogUtils.d("---test6---start_mtRbbndHead=" + byteIndexOf6);
            System.arraycopy(data, mtRbbndHead.length + byteIndexOf6, bArr12, 0, 12);
            LogUtils.d("---test6---mtRbbndData=" + ByteUtils.INSTANCE.bytetohex(bArr12));
            System.arraycopy(data, byteIndexOf6, bArr11, 0, 18);
            data = removeRange(data, byteIndexOf6, byteIndexOf6 + 13 + mtRbbndHead.length);
            LogUtils.d("---test6---data=" + ByteUtils.INSTANCE.bytetohex(data));
        }
        if (bArr11 != null && filterListener != null) {
            filterListener.onFilter(MT_RBBND, Tools.byteToHex(bArr11), bArr11);
        }
        while (getByteIndexOf(data, mtStatusHead) != -1) {
            bArr = new byte[1];
            int byteIndexOf7 = getByteIndexOf(data, mtStatusHead);
            LogUtils.d("---test5---startMtStatusHead=" + byteIndexOf7);
            System.arraycopy(data, mtStatusHead.length + byteIndexOf7, bArr, 0, 1);
            LogUtils.d("---test5---status=" + ByteUtils.INSTANCE.bytetohex(bArr));
            data = removeRange(data, byteIndexOf7, byteIndexOf7 + 2 + mtStatusHead.length);
            LogUtils.d("---test5---data=" + ByteUtils.INSTANCE.bytetohex(data));
        }
        if (bArr != null && filterListener != null) {
            filterListener.onFilter(MT_STATUS, Tools.byteToHex(bArr), bArr);
        }
        if (getByteIndexOf(data, DITHERING_FINISH.getBytes()) != -1) {
            if (filterListener != null) {
                filterListener.onFilter(DITHERING_FINISH, DITHERING_FINISH, DITHERING_FINISH.getBytes());
            }
            data = removeRange(data, getByteIndexOf(data, DITHERING_FINISH.getBytes()), getByteIndexOf(data, DITHERING_FINISH.getBytes()) + DITHERING_FINISH.getBytes().length + 1);
        }
        if (getByteIndexOf(data, DISCONNECT_BT.getBytes()) == -1) {
            return data;
        }
        if (filterListener != null) {
            filterListener.onFilter(DISCONNECT_BT, DISCONNECT_BT, DISCONNECT_BT.getBytes());
        }
        return removeRange(data, getByteIndexOf(data, DISCONNECT_BT.getBytes()), getByteIndexOf(data, DISCONNECT_BT.getBytes()) + DISCONNECT_BT.getBytes().length);
    }

    public static byte[] removeRange(byte[] array, int start, int end) {
        byte[] bArr = new byte[array.length - (end - start)];
        int i = 0;
        for (int i2 = 0; i2 < array.length; i2++) {
            if (i2 < start || i2 >= end) {
                bArr[i] = array[i2];
                i++;
            }
        }
        return bArr;
    }

    public static int getByteIndexOf(byte[] sources, byte[] src) {
        if (sources != null && src != null && sources.length != 0 && src.length != 0) {
            for (int i = 0; i < sources.length; i++) {
                if (sources[i] == src[0] && src.length + i <= sources.length) {
                    int i2 = 1;
                    while (i2 < src.length && sources[i + i2] == src[i2]) {
                        i2++;
                    }
                    if (i2 == src.length) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
