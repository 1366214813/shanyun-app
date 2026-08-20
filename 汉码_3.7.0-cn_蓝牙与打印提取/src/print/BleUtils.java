package utils;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.bhm.ble.BleManager;
import com.bhm.ble.device.BleDevice;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.prt.print.data.bean.CharacteristicNode;
import com.prt.print.data.bean.ServiceNode;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BleUtils.kt */
/* JADX INFO: loaded from: classes11.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002¨\u0006\r"}, d2 = {"Lutils/BleUtils;", "", "<init>", "()V", "getListData", "", "Lcom/chad/library/adapter/base/entity/node/BaseNode;", "bleDevice", "Lcom/bhm/ble/device/BleDevice;", "getOperateType", "", "characteristic", "Landroid/bluetooth/BluetoothGattCharacteristic;", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class BleUtils {
    public static final BleUtils INSTANCE = new BleUtils();

    private BleUtils() {
    }

    public final List<BaseNode> getListData(BleDevice bleDevice) {
        List<BluetoothGattService> services;
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        BluetoothGatt bluetoothGatt = BleManager.INSTANCE.get().getBluetoothGatt(bleDevice);
        ArrayList arrayList = new ArrayList();
        if (bluetoothGatt != null && (services = bluetoothGatt.getServices()) != null) {
            int i = 0;
            for (Object obj : services) {
                int i2 = i + 1;
                if (i < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                BluetoothGattService bluetoothGattService = (BluetoothGattService) obj;
                ArrayList arrayList2 = new ArrayList();
                List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                if (characteristics != null) {
                    int i3 = 0;
                    for (Object obj2 : characteristics) {
                        int i4 = i3 + 1;
                        if (i3 < 0) {
                            CollectionsKt.throwIndexOverflow();
                        }
                        BluetoothGattCharacteristic bluetoothGattCharacteristic = (BluetoothGattCharacteristic) obj2;
                        String strValueOf = String.valueOf(i3);
                        String string = bluetoothGattService.getUuid().toString();
                        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
                        String string2 = bluetoothGattCharacteristic.getUuid().toString();
                        Intrinsics.checkNotNullExpressionValue(string2, "toString(...)");
                        BleUtils bleUtils = INSTANCE;
                        Intrinsics.checkNotNull(bluetoothGattCharacteristic);
                        arrayList2.add(new CharacteristicNode(strValueOf, string, string2, bleUtils.getOperateType(bluetoothGattCharacteristic), bluetoothGattCharacteristic.getProperties(), (bluetoothGattCharacteristic.getProperties() & 16) != 0, (bluetoothGattCharacteristic.getProperties() & 32) != 0, (bluetoothGattCharacteristic.getProperties() & 8) != 0, (bluetoothGattCharacteristic.getProperties() & 2) != 0));
                        i3 = i4;
                    }
                }
                String strValueOf2 = String.valueOf(i);
                String string3 = bluetoothGattService.getUuid().toString();
                Intrinsics.checkNotNullExpressionValue(string3, "toString(...)");
                arrayList.add(new ServiceNode(strValueOf2, string3, arrayList2));
                i = i2;
            }
        }
        return arrayList;
    }

    private final String getOperateType(BluetoothGattCharacteristic characteristic) {
        StringBuilder sb = new StringBuilder();
        int properties = characteristic.getProperties();
        if ((properties & 2) != 0) {
            sb.append("Read , ");
        }
        if ((properties & 8) != 0) {
            sb.append("Write , ");
        }
        if ((properties & 4) != 0) {
            sb.append("Write No Response , ");
        }
        if ((properties & 16) != 0) {
            sb.append("Notify , ");
        }
        if ((properties & 32) != 0) {
            sb.append("Indicate , ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
        if (sb.length() > 0) {
            String string = sb.toString();
            Intrinsics.checkNotNull(string);
            return string;
        }
        return "";
    }
}
