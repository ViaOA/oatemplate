package com.template.delegate.oa;

import com.template.model.oa.*;

public class ReportDefDelegate {

    public static Class getTemplateTemplateRoot(ReportDef reportDef) {
        if (reportDef == null) return null;
        
        ReportClass rc = reportDef.getReportClass();
        if (rc == null) return null;
        
        Class cz = ReportClassDelegate.getClassToUse(rc);
        return cz;
    }
    
}
