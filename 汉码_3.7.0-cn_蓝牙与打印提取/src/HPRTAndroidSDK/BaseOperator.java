package HPRTAndroidSDK;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseOperator implements IPort {
    int connectState = 0;
    boolean stopPrint = false;

    @Override // HPRTAndroidSDK.IPort
    public boolean reConnect() {
        return false;
    }

    @Override // HPRTAndroidSDK.IPort
    public int getConnectState() {
        return this.connectState;
    }

    @Override // HPRTAndroidSDK.IPort
    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadDataMillisecond(boolean checkConnectState, int millisecond) {
        if (checkConnectState && this.connectState == 1) {
            return new byte[0];
        }
        return ReadDataMillisecond(millisecond);
    }

    public boolean isStopPrint() {
        return this.stopPrint;
    }

    public void setStopPrint(boolean stopPrint) {
        this.stopPrint = stopPrint;
    }
}
