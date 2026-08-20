package HPRTAndroidSDK;

import kotlin.Metadata;

/* JADX INFO: compiled from: PackLengthListener.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001a\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005H&¨\u0006\u0007À\u0006\u0003"}, d2 = {"LHPRTAndroidSDK/PackLengthListener;", "", "getLen", "", "code", "", "len", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public interface PackLengthListener {
    void getLen(int code, int len);

    /* JADX INFO: renamed from: HPRTAndroidSDK.PackLengthListener$-CC, reason: invalid class name */
    /* JADX INFO: compiled from: PackLengthListener.kt */
    public final /* synthetic */ class CC {
        public static /* synthetic */ void getLen$default(PackLengthListener packLengthListener, int i, int i2, int i3, Object obj) {
            if (obj != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getLen");
            }
            if ((i3 & 2) != 0) {
                i2 = 0;
            }
            packLengthListener.getLen(i, i2);
        }
    }

    /* JADX INFO: compiled from: PackLengthListener.kt */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final class DefaultImpls {
    }
}
