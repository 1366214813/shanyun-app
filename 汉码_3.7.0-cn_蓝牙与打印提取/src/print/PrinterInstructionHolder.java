package com.prt.print.attribute;

import android.content.Context;
import hprt.com.hmark.release.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
public class PrinterInstructionHolder {
    private static final int DATA_TYPE_COUNT = 2;
    private Context context;
    private List<String> instructionList;

    public PrinterInstructionHolder(Context context) {
        this.context = context;
    }

    public List<String> getInstructionList() {
        ArrayList arrayList = new ArrayList(2);
        this.instructionList = arrayList;
        arrayList.add(this.context.getString(R.string.print_express_sheet_printer));
        this.instructionList.add(this.context.getString(R.string.print_bill_printer));
        return this.instructionList;
    }

    public int getExpressSheetIndex() {
        return this.instructionList.indexOf(this.context.getString(R.string.print_express_sheet_printer));
    }

    public String getPrinterTypeBySelectText(String selectText) {
        if (selectText.equals(this.context.getString(R.string.print_express_sheet_printer))) {
            return "TSPL";
        }
        return "ESC";
    }
}
