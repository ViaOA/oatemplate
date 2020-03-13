@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=OADateTime.class, value=OADateTimeXmlAdapter.class),
    @XmlJavaTypeAdapter(type=OADate.class, value=OADateXmlAdapter.class),
    @XmlJavaTypeAdapter(type=OATime.class, value=OATimeXmlAdapter.class)
})
package com.template.model.oa;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import com.viaoa.util.*;
import com.viaoa.util.xmladapter.*;


