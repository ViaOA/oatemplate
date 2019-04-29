package com.template.webservice.server;

import java.rmi.Remote;

public interface HelloInterface extends Remote {
    public String getHello(String name);
}
