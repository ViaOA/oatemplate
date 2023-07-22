<%@ include file="include/jspHeader.jspf"%>

<%
formId = "oaInputTime";
form = oasession.getRequestForm(formId);

if (form == null) {
    form = oasession.createRequestForm(formId);

    Hub<AppUser> hub = ModelDelegate.getAppUsers();
    if (hub.getAO() == null) hub.setPos(0);
    
    OAInputTime comp = new OAInputTime("time", hub, AppUser.P_CurrentTime) {
        int cnt;
        public void onSubmitCompleted(OAFormSubmitEvent formSubmitEvent) {
            System.out.println((++cnt)+") onSubmitCompleted, value="+getValue());
        }
    };
    // comp.setValue(""); // 2023-06-25
    comp.setLabelId("lbl");
    comp.setDebug(true);
    form.add(comp);

    HtmlElementPropertyEditor hpe = new HtmlElementPropertyEditor(form, comp);
}
%>


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

<p>
Note: qqqqqqqq this does not have a value oaproperty ... using calc for Now()
<p>


<fieldset>
  <legend>Demo OAInputTime</legend>
  <label id="lbl">Test OAInputTime <input id="time" type="time"></label> <br>

<p><br><br>
<%@ include file="htmlElementPropertyEditor.jsp"%>
</fieldset>

<%@ include file="include/htmlFooter.jspf"%>


