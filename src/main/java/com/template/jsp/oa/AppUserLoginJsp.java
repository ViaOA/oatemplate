package com.template.jsp.oa;

import com.template.model.*;
import com.template.model.oa.*;
import com.viaoa.hub.Hub;
import com.viaoa.jsp.*;

public class AppUserLoginJsp extends AppUserLoginJspBase {

    public AppUserLoginJsp(Hub<AppUserLogin> hub, OAForm form) {
        this(new AppUserLoginModel(hub), form, "");
    }    
    public AppUserLoginJsp(AppUserLoginModel model, OAForm form) {
        this(model, form, "");
    }
    public AppUserLoginJsp(Hub<AppUserLogin> hub, OAForm form, String idPrefix) {
        this(new AppUserLoginModel(hub), form, idPrefix);
    }    
    public AppUserLoginJsp(AppUserLoginModel model, OAForm form, String idPrefix) {
        super(model, form, idPrefix);
    }
    
    
}
