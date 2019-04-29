package com.template.jsp.oa;

import com.template.model.AppUserModel;
import com.template.model.oa.AppUser;
import com.viaoa.hub.Hub;
import com.viaoa.jsp.*;

public class AppUserJsp extends AppUserJspBase {

    public AppUserJsp(Hub<AppUser> hub, OAForm form) {
        this(new AppUserModel(hub), form, "");
    }    
    public AppUserJsp(AppUserModel model, OAForm form) {
        this(model, form, "");
    }
    public AppUserJsp(Hub<AppUser> hub, OAForm form, String idPrefix) {
        this(new AppUserModel(hub), form, idPrefix);
    }    
    public AppUserJsp(AppUserModel model, OAForm form, String idPrefix) {
        super(model, form, idPrefix);
    }
}
