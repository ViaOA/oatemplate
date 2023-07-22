// Generated by OABuilder

package com.template.model;

import java.util.logging.*;
import com.viaoa.object.*;
import com.viaoa.annotation.*;
import com.viaoa.datasource.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;
import com.viaoa.web.filter.*;
import com.template.model.oa.*;
import com.template.model.oa.propertypath.*;
import com.template.model.oa.search.*;
import com.template.model.oa.filter.*;
import com.template.model.search.*;
import com.template.model.filter.*;
import com.template.delegate.ModelDelegate;
import com.template.resource.Resource;

public class AppServerModel extends OAObjectModel {
    private static Logger LOG = Logger.getLogger(AppServerModel.class.getName());
    
    // Hubs
    protected Hub<AppServer> hub;
    // selected appServers
    protected Hub<AppServer> hubMultiSelect;
    // detail hubs
    protected Hub<AppUserLogin> hubAppUserLogin;
    
    // ObjectModels
    protected AppUserLoginModel modelAppUserLogin;
    
    // SearchModels used for references
    protected AppUserLoginSearchModel modelAppUserLoginSearch;
    
    public AppServerModel() {
        setDisplayName("App Server");
        setPluralDisplayName("App Servers");
    }
    
    public AppServerModel(Hub<AppServer> hubAppServer) {
        this();
        if (hubAppServer != null) HubDelegate.setObjectClass(hubAppServer, AppServer.class);
        this.hub = hubAppServer;
    }
    public AppServerModel(AppServer appServer) {
        this();
        getHub().add(appServer);
        getHub().setPos(0);
    }
    
    public Hub<AppServer> getOriginalHub() {
        return getHub();
    }
    
    public Hub<AppUserLogin> getAppUserLoginHub() {
        if (hubAppUserLogin != null) return hubAppUserLogin;
        hubAppUserLogin = getHub().getDetailHub(AppServer.P_AppUserLogin);
        return hubAppUserLogin;
    }
    public AppServer getAppServer() {
        return getHub().getAO();
    }
    
    public Hub<AppServer> getHub() {
        if (hub == null) {
            hub = new Hub<AppServer>(AppServer.class);
        }
        return hub;
    }
    
    public Hub<AppServer> getMultiSelectHub() {
        if (hubMultiSelect == null) {
            hubMultiSelect = new Hub<AppServer>(AppServer.class);
        }
        return hubMultiSelect;
    }
    
    public AppUserLoginModel getAppUserLoginModel() {
        if (modelAppUserLogin != null) return modelAppUserLogin;
        modelAppUserLogin = new AppUserLoginModel(getAppUserLoginHub());
        modelAppUserLogin.setDisplayName("App User Login");
        modelAppUserLogin.setPluralDisplayName("App User Logins");
        modelAppUserLogin.setForJfc(getForJfc());
        modelAppUserLogin.setAllowNew(true);
        modelAppUserLogin.setAllowSave(true);
        modelAppUserLogin.setAllowAdd(false);
        modelAppUserLogin.setAllowRemove(true);
        modelAppUserLogin.setAllowClear(true);
        modelAppUserLogin.setAllowDelete(false);
        modelAppUserLogin.setAllowSearch(true);
        modelAppUserLogin.setAllowHubSearch(false);
        modelAppUserLogin.setAllowGotoEdit(true);
        modelAppUserLogin.setViewOnly(getViewOnly());
        // call AppServer.onEditQueryAppUserLogin(AppUserLoginModel) to be able to customize this model
        OAObjectCallbackDelegate.onObjectCallbackModel(AppServer.class, AppServer.P_AppUserLogin, modelAppUserLogin);
    
        return modelAppUserLogin;
    }
    
    public AppUserLoginSearchModel getAppUserLoginSearchModel() {
        if (modelAppUserLoginSearch != null) return modelAppUserLoginSearch;
        modelAppUserLoginSearch = new AppUserLoginSearchModel();
        return modelAppUserLoginSearch;
    }
    
    public HubCopy<AppServer> createHubCopy() {
        Hub<AppServer> hubAppServerx = new Hub<>(AppServer.class);
        HubCopy<AppServer> hc = new HubCopy<>(getHub(), hubAppServerx, true);
        return hc;
    }
    public AppServerModel createCopy() {
        AppServerModel mod = new AppServerModel(createHubCopy().getHub());
        return mod;
    }
}

