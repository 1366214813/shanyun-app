package HPRTAndroidSDK.bean;

/* JADX INFO: loaded from: classes.dex */
public class RFIDInfo {
    public String equipmentModel;
    public String gap;
    public String height;
    public String model;
    public String remainMileage;
    public String totalMileage;
    public String uid;
    public String width;

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEquipmentModel() {
        return this.equipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTotalMileage() {
        return this.totalMileage;
    }

    public void setTotalMileage(String totalMileage) {
        this.totalMileage = totalMileage;
    }

    public String getRemainMileage() {
        return this.remainMileage;
    }

    public void setRemainMileage(String remainMileage) {
        this.remainMileage = remainMileage;
    }

    public String getGap() {
        return this.gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }

    public String toString() {
        return "RFIDInfo{uid='" + this.uid + "', equipmentModel='" + this.equipmentModel + "', width='" + this.width + "', height='" + this.height + "', model='" + this.model + "', totalMileage='" + this.totalMileage + "', remainMileage='" + this.remainMileage + "'}";
    }
}
