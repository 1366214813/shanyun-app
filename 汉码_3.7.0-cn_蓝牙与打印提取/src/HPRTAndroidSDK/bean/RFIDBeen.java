package HPRTAndroidSDK.bean;

/* JADX INFO: loaded from: classes.dex */
public class RFIDBeen {
    public int address;
    public String data;
    public int length;
    public int memory;

    public int getMemory() {
        return this.memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getAddress() {
        return this.address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toString() {
        return "RFIDBeen{memory=" + this.memory + ", address=" + this.address + ", length=" + this.length + ", data='" + this.data + "'}";
    }
}
