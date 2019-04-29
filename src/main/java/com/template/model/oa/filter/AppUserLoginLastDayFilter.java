// Generated by OABuilder
package com.template.model.oa.filter;

import java.util.logging.*;
import com.template.model.oa.*;
import com.template.model.oa.propertypath.*;
import com.viaoa.annotation.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;
import java.util.*;
import com.template.model.search.*;
import com.template.model.oa.search.*;

@OAClass(useDataSource=false, localOnly=true)
@OAClassFilter(name = "LastDay", displayName = "Last Day", hasInputParams = false)
public class AppUserLoginLastDayFilter extends OAObject implements CustomHubFilter<AppUserLogin> {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(AppUserLoginLastDayFilter.class.getName());

    public static final String PPCode = ":LastDay()";
    private Hub<AppUserLogin> hubMaster;
    private Hub<AppUserLogin> hub;
    private HubFilter<AppUserLogin> hubFilter;
    private OAObjectCacheFilter<AppUserLogin> cacheFilter;
    private boolean bUseObjectCache;

    public AppUserLoginLastDayFilter() {
        this(null, null, false);
    }
    public AppUserLoginLastDayFilter(Hub<AppUserLogin> hub) {
        this(null, hub, true);
    }
    public AppUserLoginLastDayFilter(Hub<AppUserLogin> hubMaster, Hub<AppUserLogin> hub) {
        this(hubMaster, hub, false);
    }
    public AppUserLoginLastDayFilter(Hub<AppUserLogin> hubMaster, Hub<AppUserLogin> hubFiltered, boolean bUseObjectCache) {
        this.hubMaster = hubMaster;
        this.hub = hubFiltered;
        this.bUseObjectCache = bUseObjectCache;
        if (hubMaster != null) getHubFilter();
        if (bUseObjectCache) getObjectCacheFilter();
    }


    public void reset() {
    }

    public boolean isDataEntered() {
        return false;
    }
    public void refresh() {
        if (hubFilter != null) getHubFilter().refresh();
        if (cacheFilter != null) getObjectCacheFilter().refresh();
    }

    @Override
    public HubFilter<AppUserLogin> getHubFilter() {
        if (hubFilter != null) return hubFilter;
        if (hubMaster == null) return null;
        hubFilter = new HubFilter<AppUserLogin>(hubMaster, hub) {
            @Override
            public boolean isUsed(AppUserLogin appUserLogin) {
                return AppUserLoginLastDayFilter.this.isUsed(appUserLogin);
            }
        };
        hubFilter.addDependentProperty(AppUserLoginPP.created(), false);
        hubFilter.refresh();
        return hubFilter;
    }

    public OAObjectCacheFilter<AppUserLogin> getObjectCacheFilter() {
        if (cacheFilter != null) return cacheFilter;
        if (!bUseObjectCache) return null;
        cacheFilter = new OAObjectCacheFilter<AppUserLogin>(hub) {
            @Override
            public boolean isUsed(AppUserLogin appUserLogin) {
                return AppUserLoginLastDayFilter.this.isUsed(appUserLogin);
            }
            @Override
            protected void reselect() {
                AppUserLoginLastDayFilter.this.reselect();
            }
        };
        cacheFilter.addDependentProperty(AppUserLoginPP.created(), false);
        cacheFilter.refresh();
        return cacheFilter;
    }

    public void reselect() {
        // can be overwritten to query datasource
    }

    // ==================
    // this method has custom code that will need to be put into the OABuilder filter

    @Override
    public boolean isUsed(AppUserLogin appUserLogin) {
        OADateTime created = appUserLogin.getCreated();
        if (created == null) return false;
        OADateTime d1 = created.addDays(1);
        OADateTime d2 = new OADateTime();
        if (d1.before(d2)) return false;
        return true;
    }
}
