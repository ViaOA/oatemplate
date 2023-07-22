<%@ include file="include/jspHeader.jspf"%>

<%

formId = "bsTypeAhead";
form = oasession.getRequestForm(formId);

if (form == null) {
    form = oasession.createRequestForm(formId);

    BsTypeAhead comp = new BsTypeAhead("txtta") {
        int cnt;
        public void onSubmitCompleted(OAFormSubmitEvent formSubmitEvent) {
            processOnSubmitCompleted(formSubmitEvent, this, ++cnt);
        }

        protected String onGetJson(OASession session) {
            return processGetJson(session.getRequest().getParameter("term"));
        }
    };
    comp.setMultiValue(false);
    // comp.setValue("a,b,c");
    
    OATypeAhead ta = new OATypeAhead(Arrays.asList(new OAObject()));
    ta.setMinimumInputLength(3);
    ta.setMaxResults(35);
    ta.setShowHint(true);
    
    comp.setTypeAhead(ta);
    
    // comp.setValue(""); 
    comp.setLabelId("lbl");
    // comp.setDebug(true);
    form.add(comp);

    HtmlElementPropertyEditor hpe = new HtmlElementPropertyEditor(form, comp); 
}
%>

<%!public void processOnSubmitCompleted(OAFormSubmitEvent formSubmitEvent, BsTypeAhead comp, int cnt) {
    System.out.println((++cnt)+") onSubmitCompleted, value=" + comp.getValue() );
    OAForm form = formSubmitEvent.getForm();
}

// overwrite matching values
protected String processGetJson(String term) {
    String id = "id."+term;
    String displayValue = term + " display value here ";
    String dd = term + " dropdown <br><b>display</> here ";
    
    String json = "";
    int x = (int) (Math.random() * 54);
    for (int i=0; i<x; i++) {
        if (json.length() > 0) json += ", ";
        json += "{\"id\":\"" + (id + i) + "\",\"display\":\"" + (displayValue + i) + "\",\"dropdowndisplay\":\"" + (dd + i) + "\"}";
    }

    return "[" + json + "]";
}%>



<%@ include file="include/htmlHeader.jspf"%>
<style>
body {
  margin-left: 15px;
}

#div {
  border: solid thin;
}
</style>

<span id="cmdReloadMessage">Page reload is needed</span> 
<a id="cmdOAReset" href="oareset.jsp">oareset</a> &nbsp;&nbsp;&nbsp;
<input type=submit value="submit">
<input id="cmdDoAjaxSubmit" type=button value="xxx">
debug <input id="chkFormDebug" type="checkbox">

<fieldset>
  <legend>Demo BsTypeAhead</legend>
  <label id="lbl">Test BsTypeAhead <input id="txtta" type="Text"></label> <br>

<p><br><br>
<%@ include file="htmlElementPropertyEditor.jsp"%>
</fieldset>

<%@ include file="include/htmlFooter.jspf"%>


