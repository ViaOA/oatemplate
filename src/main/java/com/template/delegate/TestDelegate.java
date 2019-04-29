package com.template.delegate;

import com.template.model.*;
import com.template.util.DataGenerator;
import com.viaoa.ds.objectcache.OADataSourceObjectCache;
import com.viaoa.hub.Hub;
import com.viaoa.hub.HubEvent;
import com.viaoa.hub.HubListenerAdapter;
import com.viaoa.object.OAObject;
import com.viaoa.object.OAObjectEditQueryDelegate;
import com.viaoa.object.OAThreadLocalDelegate;
import com.viaoa.util.*;

public class TestDelegate {

    private OADataSourceObjectCache ds;
    private DataGenerator dg;

    public void createSampleData(String xx) {
        if (dg == null) {
            ds = new OADataSourceObjectCache();
            ds.setAssignIdOnCreate(true);
            dg = new DataGenerator();
            try {
                OAObjectEditQueryDelegate.demoAllowAllToPass(true);
                dg.createSamples();
            }
            finally {
                OAObjectEditQueryDelegate.demoAllowAllToPass(false);
            }
        }
    }
}



