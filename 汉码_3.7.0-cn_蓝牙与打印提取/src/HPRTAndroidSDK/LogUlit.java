package HPRTAndroidSDK;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import com.prt.base.utils.DateUtils;
import com.prt.provider.common.App;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: classes.dex */
public class LogUlit {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00f8 A[Catch: IOException -> 0x0116, TRY_ENTER, TryCatch #2 {IOException -> 0x0116, blocks: (B:34:0x00f8, B:36:0x00fd, B:46:0x010d, B:48:0x0112), top: B:63:0x00a3 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00fd A[Catch: IOException -> 0x0116, TRY_LEAVE, TryCatch #2 {IOException -> 0x0116, blocks: (B:34:0x00f8, B:36:0x00fd, B:46:0x010d, B:48:0x0112), top: B:63:0x00a3 }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x010d A[Catch: IOException -> 0x0116, TRY_ENTER, TryCatch #2 {IOException -> 0x0116, blocks: (B:34:0x00f8, B:36:0x00fd, B:46:0x010d, B:48:0x0112), top: B:63:0x00a3 }] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0112 A[Catch: IOException -> 0x0116, TRY_LEAVE, TryCatch #2 {IOException -> 0x0116, blocks: (B:34:0x00f8, B:36:0x00fd, B:46:0x010d, B:48:0x0112), top: B:63:0x00a3 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0126 A[Catch: IOException -> 0x0122, TRY_LEAVE, TryCatch #6 {IOException -> 0x0122, blocks: (B:54:0x011e, B:58:0x0126), top: B:66:0x011e }] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x011e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v10 */
    /* JADX WARN: Type inference failed for: r6v11 */
    /* JADX WARN: Type inference failed for: r6v13 */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v3, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v4 */
    /* JADX WARN: Type inference failed for: r6v5, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v6, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v7, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void writeFileToSDCard(Context context, byte[] bArr, String str, String str2, boolean z, boolean z2) {
        String str3;
        File file;
        ?? fileOutputStream;
        ?? r6;
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            str3 = Environment.getExternalStorageDirectory() + File.separator;
        } else {
            str3 = Environment.getExternalStorageDirectory() + File.separator + str + File.separator;
        }
        File file2 = new File(str3);
        if (Build.VERSION.SDK_INT >= 29) {
            file2 = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }
        if (!file2.exists() && !file2.mkdirs()) {
            return;
        }
        if (TextUtils.isEmpty(str2)) {
            file = new File(file2.getPath() + "/SDK_log.txt");
        } else {
            file = new File(file2.getPath() + "/" + str2);
        }
        RandomAccessFile randomAccessFile = null;
        try {
            try {
                if (z) {
                    RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
                    try {
                        randomAccessFile2.seek(file.length());
                        randomAccessFile2.write(DateUtils.getInstance().format(System.currentTimeMillis()).getBytes());
                        randomAccessFile2.write(":".getBytes());
                        randomAccessFile2.write(bArr);
                        if (z2) {
                            randomAccessFile2.write("\n".getBytes());
                        }
                        r6 = 0;
                        randomAccessFile = randomAccessFile2;
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                        }
                        if (r6 != 0) {
                            r6.close();
                        }
                    } catch (IOException e) {
                        e = e;
                        fileOutputStream = 0;
                        randomAccessFile = randomAccessFile2;
                        e.printStackTrace();
                        if (randomAccessFile != null) {
                        }
                        if (fileOutputStream == 0) {
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream = 0;
                        randomAccessFile = randomAccessFile2;
                        if (randomAccessFile != null) {
                        }
                        if (fileOutputStream != 0) {
                        }
                        throw th;
                    }
                } else {
                    fileOutputStream = new FileOutputStream(file);
                    try {
                        try {
                            fileOutputStream.write(bArr);
                            fileOutputStream.flush();
                            r6 = fileOutputStream;
                            if (randomAccessFile != null) {
                            }
                            if (r6 != 0) {
                            }
                        } catch (IOException e2) {
                            e = e2;
                            e.printStackTrace();
                            if (randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                            if (fileOutputStream == 0) {
                                fileOutputStream.close();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (randomAccessFile != null) {
                            try {
                                randomAccessFile.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                                throw th;
                            }
                        }
                        if (fileOutputStream != 0) {
                            fileOutputStream.close();
                        }
                        throw th;
                    }
                }
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        } catch (IOException e5) {
            e = e5;
            fileOutputStream = randomAccessFile;
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = randomAccessFile;
        }
    }

    public static void writeFileToSDCard(final byte[] buffer) {
        writeFileToSDCard(App.INSTANCE.getCONTEXT(), buffer, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
    }
}
