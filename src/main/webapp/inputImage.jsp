<%@ include file="include/jspHeader.jspf"%>

<%
formId = "inputImage";
form = oasession.getRequestForm(formId);

if (form == null) {
    form = oasession.createRequestForm(formId);

    InputImage comp = new InputImage("img") {
        int cnt;
        public void onSubmitCompleted(OAFormSubmitEvent formSubmitEvent) {
            System.out.println((++cnt)+") onSubmitCompleted" + 
                    ", imageClickX=" + formSubmitEvent.getImageClickX() + 
                    ", imageClickY=" + formSubmitEvent.getImageClickY() 
                    );
        }
    };
    // comp.setValue(""); // 
    comp.setSource("image/icon.gif");
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
  <legend>Demo InputImage</legend>
  <label id="lbl">Test InputImage <input id="img" type="image"></label> <br>

<p><br><br>
<%@ include file="htmlElementPropertyEditor.jsp"%>
</fieldset>

<%@ include file="include/htmlFooter.jspf"%>


