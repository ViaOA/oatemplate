package com.template.remote;

import java.util.ArrayList;

import com.template.model.oa.AppUser;
import com.template.model.oa.AppUserLogin;
import com.template.model.oa.cs.ClientRoot;
import com.template.model.oa.cs.ServerRoot;
import com.template.resource.Resource;
import com.viaoa.util.OAProperties;

public abstract class RemoteAppImpl implements RemoteAppInterface {

    @Override
    public abstract void saveData();

    @Override
    public abstract AppUserLogin getUserLogin(int clientId, String userId, String password, String location, String userComputerName);

    @Override
    public abstract ServerRoot getServerRoot();

    @Override
    public abstract ClientRoot getClientRoot(int clientId);

    @Override
    public String getRelease() {
        int release = Resource.getInt(Resource.APP_Release);
        return release+""; // expecting a String
    }

    @Override
    public abstract boolean isRunningAsDemo();

    @Override
    public Object testBandwidth(Object data) {
        return data;
    }

    @Override
    public long getServerTime() {
        return System.currentTimeMillis();
    }

    @Override
    public abstract boolean disconnectDatabase();

    @Override
    public abstract OAProperties getServerProperties();

    @Override
    public String getResourceValue(String name) {
        return Resource.getValue(name);
    }

    @Override
    public abstract boolean writeToClientLogFile(int clientId, ArrayList al);

}

