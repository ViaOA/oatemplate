<%@ include file="include/jspHeader.jspf"%>

<%
formId = "inputSearch";
form = oasession.getRequestForm(formId);

if (form == null) {
    form = oasession.createRequestForm(formId);

    InputSearch comp = new InputSearch("search") {
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

<fieldset>
  <legend>Demo InputSearch</legend>
  <label id="lbl">Test InputSearch <input id="search" type="search"></label> <br>

<p><br><br>
<%@ include file="htmlElementPropertyEditor.jsp"%>
</fieldset>

<%@ include file="include/htmlFooter.jspf"%>


