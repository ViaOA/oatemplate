<%@ include file="include/jspHeader.jspf"%>

<%
formId = "htmlTextArea";
form = oasession.getRequestForm(formId);

if (form == null) {
    form = oasession.createRequestForm(formId);

    HtmlTextArea comp = new HtmlTextArea("txta") {
        int cnt;
        public void onSubmitCompleted(OAFormSubmitEvent formSubmitEvent) {
            System.out.println((++cnt)+") onSubmitCompleted" + 
                    ", value=" + getValue()  
                    );
        }
    };
    // comp.setValue(""); 
    comp.setLabelId("lbl");
    comp.setDebug(true);
    comp.setCols(44);
    comp.setRows(10);
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

<fieldset>
  <legend>Demo HtmlTextArea</legend>
  <label id="lbl">Test HtmlTextArea <textarea id="txta"></textarea></label> <br>

<p><br><br>
<%@ include file="htmlElementPropertyEditor.jsp"%>
</fieldset>

<%@ include file="include/htmlFooter.jspf"%>


