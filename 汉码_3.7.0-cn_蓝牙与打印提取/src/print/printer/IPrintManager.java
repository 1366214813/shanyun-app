package com.prt.print.utils.printer;

import android.graphics.Bitmap;
import com.prt.base.common.DeviceInfo;
import java.io.File;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
public interface IPrintManager {
    public static final int CONNECT_SUCCESS = 0;
    public static final int PAPER_LEARN_NOT_SUPPORT = 0;
    public static final int SEND_DATA_ERROR = -1;
    public static final int SEND_DATA_OUT_OF_COUNT = -2;
    public static final int SEND_DATA_SUCCESS = 0;
    public static final int SEND_IMAGE_POSITION_ERROR = -3;
    public static final int SEND_RFID_UN_SUPPORT_ERROR = -6;
    public static final int SEND_RFID_WRITE_ERROR = -5;
    public static final int SEND_SET_PAPER_TYPE_ERROR = -4;
    public static final int SET_DENSITY_FAIL = -1;

    public interface FirmwareProgressListener {
        void onFailure();

        void onFinish();

        void onProgress(int progress);
    }

    boolean connectBluetooth(DeviceInfo deviceInfo, boolean save);

    boolean connectBluetoothNoFailEvent(DeviceInfo deviceInfo, boolean updateDeviceInfoByPrinter);

    boolean connectWifi(DeviceInfo deviceInfo);

    boolean disConnect();

    boolean isConnected();

    int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4);

    int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint);

    int savePrinterSetting(List<String> list);

    boolean setDensity(DeviceInfo deviceInfo, int density);

    int setPaperLearn(DeviceInfo deviceInfo, int paperType);

    boolean setPaperType(DeviceInfo deviceInfo, int paperType);

    void stopPrint();

    void updateFirmware(File firmwareFile, FirmwareProgressListener listener);
}
