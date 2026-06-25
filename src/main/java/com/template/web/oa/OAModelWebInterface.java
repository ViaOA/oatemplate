package com.template.web.oa;

import com.viaoa.hub.Hub;
import com.viaoa.metadata.OAObjectModel;

public interface OAModelWebInterface {

    public Hub<?> getHub();
    
    public OAObjectModel getModel();
    
    

}
