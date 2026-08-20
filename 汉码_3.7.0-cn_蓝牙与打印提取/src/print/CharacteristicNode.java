package com.prt.print.data.bean;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: CharacteristicNode.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001e\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001BW\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\n¢\u0006\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0013R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0011\"\u0004\b\u0017\u0010\u0013R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0011\"\u0004\b\u0019\u0010\u0013R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u001a\u0010\u000b\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u001f\"\u0004\b#\u0010!R\u001a\u0010\f\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u001f\"\u0004\b%\u0010!R\u001a\u0010\r\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\u001f\"\u0004\b'\u0010!R\u001c\u0010(\u001a\n\u0012\u0004\u0012\u00020*\u0018\u00010)8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b+\u0010,¨\u0006-"}, d2 = {"Lcom/prt/print/data/bean/CharacteristicNode;", "Lcom/chad/library/adapter/base/entity/node/BaseExpandNode;", "characteristicName", "", "serviceUUID", "characteristicUUID", "characteristicProperties", "characteristicIntProperties", "", "enableNotify", "", "enableIndicate", "enableWrite", "enableRead", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZZZ)V", "getCharacteristicName", "()Ljava/lang/String;", "setCharacteristicName", "(Ljava/lang/String;)V", "getServiceUUID", "setServiceUUID", "getCharacteristicUUID", "setCharacteristicUUID", "getCharacteristicProperties", "setCharacteristicProperties", "getCharacteristicIntProperties", "()I", "setCharacteristicIntProperties", "(I)V", "getEnableNotify", "()Z", "setEnableNotify", "(Z)V", "getEnableIndicate", "setEnableIndicate", "getEnableWrite", "setEnableWrite", "getEnableRead", "setEnableRead", "childNode", "", "Lcom/chad/library/adapter/base/entity/node/BaseNode;", "getChildNode", "()Ljava/util/List;", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class CharacteristicNode extends BaseExpandNode {
    private int characteristicIntProperties;
    private String characteristicName;
    private String characteristicProperties;
    private String characteristicUUID;
    private boolean enableIndicate;
    private boolean enableNotify;
    private boolean enableRead;
    private boolean enableWrite;
    private String serviceUUID;

    @Override // com.chad.library.adapter.base.entity.node.BaseNode
    public List<BaseNode> getChildNode() {
        return null;
    }

    public /* synthetic */ CharacteristicNode(String str, String str2, String str3, String str4, int i, boolean z, boolean z2, boolean z3, boolean z4, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, str4, i, (i2 & 32) != 0 ? false : z, (i2 & 64) != 0 ? false : z2, (i2 & 128) != 0 ? false : z3, (i2 & 256) != 0 ? false : z4);
    }

    public final String getCharacteristicName() {
        return this.characteristicName;
    }

    public final void setCharacteristicName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.characteristicName = str;
    }

    public final String getServiceUUID() {
        return this.serviceUUID;
    }

    public final void setServiceUUID(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.serviceUUID = str;
    }

    public final String getCharacteristicUUID() {
        return this.characteristicUUID;
    }

    public final void setCharacteristicUUID(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.characteristicUUID = str;
    }

    public final String getCharacteristicProperties() {
        return this.characteristicProperties;
    }

    public final void setCharacteristicProperties(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.characteristicProperties = str;
    }

    public final int getCharacteristicIntProperties() {
        return this.characteristicIntProperties;
    }

    public final void setCharacteristicIntProperties(int i) {
        this.characteristicIntProperties = i;
    }

    public final boolean getEnableNotify() {
        return this.enableNotify;
    }

    public final void setEnableNotify(boolean z) {
        this.enableNotify = z;
    }

    public final boolean getEnableIndicate() {
        return this.enableIndicate;
    }

    public final void setEnableIndicate(boolean z) {
        this.enableIndicate = z;
    }

    public final boolean getEnableWrite() {
        return this.enableWrite;
    }

    public final void setEnableWrite(boolean z) {
        this.enableWrite = z;
    }

    public final boolean getEnableRead() {
        return this.enableRead;
    }

    public final void setEnableRead(boolean z) {
        this.enableRead = z;
    }

    public CharacteristicNode(String characteristicName, String serviceUUID, String characteristicUUID, String characteristicProperties, int i, boolean z, boolean z2, boolean z3, boolean z4) {
        Intrinsics.checkNotNullParameter(characteristicName, "characteristicName");
        Intrinsics.checkNotNullParameter(serviceUUID, "serviceUUID");
        Intrinsics.checkNotNullParameter(characteristicUUID, "characteristicUUID");
        Intrinsics.checkNotNullParameter(characteristicProperties, "characteristicProperties");
        this.characteristicName = characteristicName;
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.characteristicProperties = characteristicProperties;
        this.characteristicIntProperties = i;
        this.enableNotify = z;
        this.enableIndicate = z2;
        this.enableWrite = z3;
        this.enableRead = z4;
        setExpanded(false);
    }
}
