package HPRTAndroidSDK.mt;

import kotlin.Metadata;

/* JADX INFO: compiled from: MtPrinterState.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\bf\u0018\u0000 \u00022\u00020\u0001:\u0001\u0002¨\u0006\u0003À\u0006\u0003"}, d2 = {"LHPRTAndroidSDK/mt/MtPrinterState;", "", "Companion", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public interface MtPrinterState {
    public static final int AUTH_MILEAGE_END = 9;
    public static final int CARBON_MISS = 4;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = Companion.$$INSTANCE;
    public static final int GET_STATE_ERROR = -1;
    public static final int NORMAL = 8;
    public static final int NO_OFFICIAL_CARBON = 5;
    public static final int PAPER_ERROR = 7;
    public static final int PAPER_MISS = 6;
    public static final int POWER_LOW = 2;
    public static final int TEMP_TOO_HIGH = 1;
    public static final int UNCAP = 3;

    /* JADX INFO: compiled from: MtPrinterState.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\n\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"LHPRTAndroidSDK/mt/MtPrinterState$Companion;", "", "<init>", "()V", "TEMP_TOO_HIGH", "", "POWER_LOW", "UNCAP", "CARBON_MISS", "NO_OFFICIAL_CARBON", "PAPER_MISS", "PAPER_ERROR", "NORMAL", "AUTH_MILEAGE_END", "GET_STATE_ERROR", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();
        public static final int AUTH_MILEAGE_END = 9;
        public static final int CARBON_MISS = 4;
        public static final int GET_STATE_ERROR = -1;
        public static final int NORMAL = 8;
        public static final int NO_OFFICIAL_CARBON = 5;
        public static final int PAPER_ERROR = 7;
        public static final int PAPER_MISS = 6;
        public static final int POWER_LOW = 2;
        public static final int TEMP_TOO_HIGH = 1;
        public static final int UNCAP = 3;

        private Companion() {
        }
    }
}
