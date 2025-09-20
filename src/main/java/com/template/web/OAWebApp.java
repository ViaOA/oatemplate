package com.template.web;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.viaoa.json.OAJson;
import com.viaoa.util.*;
import com.viaoa.web.html.*;
import com.viaoa.web.html.input.*;
import com.viaoa.web.html.oa.*;
/*$$Start: OAWebApp.imports $$*/
/*$$End: OAWebApp.imports $$*/

/*
Main user session component.
*/
public class OAWebApp {

    private final List<HtmlElement> alRootHtmlElement = new ArrayList<>();
    private final Map<Integer, HtmlElement> mapHtmlElement = new ConcurrentHashMap();
    private volatile boolean bClosed; 

    public void create() {
        // toolbar control
        final HtmlDiv toolbar = new HtmlDiv("toolbar");
        this.addRootComponent(toolbar);
        final InputRadioGroup radioGroup = new InputRadioGroup("toolbar");
        

        // main panel control
        final OACardPanel cardMain = new OACardPanel("cardPanel");
        this.addRootComponent(cardMain);
        
        final HtmlDiv divSplash = new HtmlDiv("panSplash");
        cardMain.add(divSplash);
        cardMain.setActive(divSplash);
  
        HtmlImg imgBrand = new HtmlImg("#imgBrandIcon") {
            @Override
            public void onClickEvent(Map<String, String> map) {
                cardMain.setActive(divSplash);
                radioGroup.setCheckedInputRadio(null); 
            }
         };
         toolbar.add(imgBrand);
         
         /*$$Start: OAWebApp.panels $$*/
         // Template CODE GEN here
         /*$$End: OAWebApp.panels $$*/
    }

    public void close() {
        bClosed = true;
        for (HtmlElement he : alRootHtmlElement) {
            he.close();
        }        
    }
    
    protected void addRootComponent(HtmlElement he) {
        alRootHtmlElement.add(he);
    }
    
    
    public String getJavaScriptForClient() {
        for (HtmlElement he : alRootHtmlElement) {
            he.beforeGetJavaScriptForClient();
        }
        
        final Set<String> hsVars = new HashSet();
        hsVars.add("ele");
        hsVars.add("comp");
        hsVars.add("jsonObj");
        
        final StringBuilder sb = new StringBuilder();
        for (HtmlElement he : alRootHtmlElement) {
            String js = he.getJavaScriptForClientRecursive(hsVars);
            if (OAStr.isNotEmpty(js)) {
                sb.append(js);
            }
        }

        if (sb.length() == 0) return null;

        String js = "let";
        int cnt = 0;
        for (String s : hsVars) {
            if (cnt++ > 0) js += ",";
            js += " ";
            js += s;
        }
        js += ";\n";
        
        js = "let funcOA = async () => {\n" +
            js +
            sb.toString() + 
            "\n};\n" + 
            "funcOA();\n"; 
        
        return js;
    }

    public void onClientEvent(String json) throws IOException {
        OAJson oj = new OAJson();
        Map<String, String> hm = oj.readMap(json, String.class, String.class, false);
        
        final int id = OAConv.toInt(hm.get("id"));
        final String type = hm.get("type");

        HtmlElement heFound = mapHtmlElement.get(id);
        if (heFound == null) {
            for (HtmlElement he : alRootHtmlElement) {
                heFound = he.findHtmlElement(id);
                if (heFound != null) break;
            }
            if (heFound != null) mapHtmlElement.put(id, heFound);
        }
        
        if (heFound != null) {
            heFound.onClientEvent(type, hm);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (!bClosed) close();
        super.finalize();
    }
    

}
