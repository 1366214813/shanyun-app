package com.prt.print.data.bean;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ServiceNode.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u000e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006¢\u0006\u0004\b\b\u0010\tR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000b\"\u0004\b\u000f\u0010\rR\"\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0011¨\u0006\u0016"}, d2 = {"Lcom/prt/print/data/bean/ServiceNode;", "Lcom/chad/library/adapter/base/entity/node/BaseExpandNode;", "serviceName", "", "serviceUUID", "characteristicList", "", "Lcom/chad/library/adapter/base/entity/node/BaseNode;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getServiceName", "()Ljava/lang/String;", "setServiceName", "(Ljava/lang/String;)V", "getServiceUUID", "setServiceUUID", "getCharacteristicList", "()Ljava/util/List;", "setCharacteristicList", "(Ljava/util/List;)V", "childNode", "getChildNode", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class ServiceNode extends BaseExpandNode {
    private List<BaseNode> characteristicList;
    private String serviceName;
    private String serviceUUID;

    public final String getServiceName() {
        return this.serviceName;
    }

    public final void setServiceName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.serviceName = str;
    }

    public final String getServiceUUID() {
        return this.serviceUUID;
    }

    public final void setServiceUUID(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.serviceUUID = str;
    }

    public final List<BaseNode> getCharacteristicList() {
        return this.characteristicList;
    }

    public final void setCharacteristicList(List<BaseNode> list) {
        this.characteristicList = list;
    }

    public ServiceNode(String serviceName, String serviceUUID, List<BaseNode> list) {
        Intrinsics.checkNotNullParameter(serviceName, "serviceName");
        Intrinsics.checkNotNullParameter(serviceUUID, "serviceUUID");
        this.serviceName = serviceName;
        this.serviceUUID = serviceUUID;
        this.characteristicList = list;
        setExpanded(false);
    }

    @Override // com.chad.library.adapter.base.entity.node.BaseNode
    public List<BaseNode> getChildNode() {
        return this.characteristicList;
    }
}
