package com.template.delegate;

import com.template.model.*;
import com.template.util.DataGenerator;
import com.viaoa.datasource.objectcache.OADataSourceObjectCache;
import com.viaoa.hub.Hub;
import com.viaoa.hub.HubEvent;
import com.viaoa.hub.HubListenerAdapter;
import com.viaoa.object.OAObject;
import com.viaoa.runtime.OARuntime;

public class TestDelegate {

    private OADataSourceObjectCache ds;
    private DataGenerator dg;

    public void createSampleData(String xx) {
        if (dg == null) {
            ds = new OADataSourceObjectCache();
            ds.setAssignIdOnCreate(true);
            OARuntime.datasource().register(ds);
            dg = new DataGenerator();
            try {
                // OARuntime.graph().internal().objects().callbacks().demoAllowAllToPass(true);
                dg.createSamples();
            }
            finally {
                // OAObjectCallbackDelegate.demoAllowAllToPass(false);
            }
        }
    }
}



