package HPRTAndroidSDK;

/* JADX INFO: loaded from: classes.dex */
public class WIFIBean {
    String mac = "";
    String mode = "";
    String ssid = "";
    String ip = "";
    String password = "";
    int certificationModel = 0;

    public int getCertificationModel() {
        return this.certificationModel;
    }

    public void setCertificationModel(int certificationModel) {
        this.certificationModel = certificationModel;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSsid() {
        return this.ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "WIFIBean{mac='" + this.mac + "', mode='" + this.mode + "', ssid='" + this.ssid + "', ip='" + this.ip + "', password='" + this.password + "', certificationModel='" + this.certificationModel + "'}";
    }
}
