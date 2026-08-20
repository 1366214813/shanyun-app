package HPRTAndroidSDK.bean;

import anet.channel.strategy.dispatch.DispatchConstants;
import com.prt.print.utils.TemplateOperation;
import com.taobao.weex.el.parse.Operators;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ResultSendData.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0013\u001a\u00020\u0005H\u0016J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0019"}, d2 = {"LHPRTAndroidSDK/bean/ResultSendData;", "", "data", "", "height", "", "<init>", "([BI)V", "getData", "()[B", "setData", "([B)V", "getHeight", "()I", "setHeight", "(I)V", "equals", "", DispatchConstants.OTHER, "hashCode", "component1", "component2", TemplateOperation.COPY, "toString", "", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class ResultSendData {
    private byte[] data;
    private int height;

    /* JADX WARN: Multi-variable type inference failed */
    public ResultSendData() {
        this(null, 0, 3, 0 == true ? 1 : 0);
    }

    public static /* synthetic */ ResultSendData copy$default(ResultSendData resultSendData, byte[] bArr, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            bArr = resultSendData.data;
        }
        if ((i2 & 2) != 0) {
            i = resultSendData.height;
        }
        return resultSendData.copy(bArr, i);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final byte[] getData() {
        return this.data;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int getHeight() {
        return this.height;
    }

    public final ResultSendData copy(byte[] data, int height) {
        Intrinsics.checkNotNullParameter(data, "data");
        return new ResultSendData(data, height);
    }

    public String toString() {
        return "ResultSendData(data=" + Arrays.toString(this.data) + ", height=" + this.height + Operators.BRACKET_END_STR;
    }

    public ResultSendData(byte[] data, int i) {
        Intrinsics.checkNotNullParameter(data, "data");
        this.data = data;
        this.height = i;
    }

    public /* synthetic */ ResultSendData(byte[] bArr, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new byte[0] : bArr, (i2 & 2) != 0 ? 0 : i);
    }

    public final byte[] getData() {
        return this.data;
    }

    public final void setData(byte[] bArr) {
        Intrinsics.checkNotNullParameter(bArr, "<set-?>");
        this.data = bArr;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void setHeight(int i) {
        this.height = i;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!Intrinsics.areEqual(getClass(), other != null ? other.getClass() : null)) {
            return false;
        }
        Intrinsics.checkNotNull(other, "null cannot be cast to non-null type HPRTAndroidSDK.bean.ResultSendData");
        ResultSendData resultSendData = (ResultSendData) other;
        return this.height == resultSendData.height && Arrays.equals(this.data, resultSendData.data);
    }

    public int hashCode() {
        return (this.height * 31) + Arrays.hashCode(this.data);
    }
}
