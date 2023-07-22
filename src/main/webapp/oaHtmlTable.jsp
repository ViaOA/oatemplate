<%@ include file="include/jspHeader.jspf"%>

<%
formId = "oaHtmlTable";
form = oasession.getRequestForm(formId);

if (form == null) {
    form = oasession.createRequestForm(formId);

    Hub<AppUser> hub = ModelDelegate.getAppUsers();

    String compId = "table";
    OAHtmlTable comp = new OAHtmlTable(compId, hub) {
        int cnt;

        public void onSubmitCompleted(OAFormSubmitEvent formSubmitEvent) {
    System.out.println((++cnt) + ") onSubmitCompleted");
        }
    };
    form.add(comp);
    HtmlElementPropertyEditor hpe = new HtmlElementPropertyEditor(form, comp);

    comp.addCounterColumn("Counter");

    HtmlCol htmlCol;
    HtmlTH htmlTh;
    htmlCol = new HtmlCol();
    htmlTh = new HtmlTH();
    htmlTh.setInnerHtml("First Name");

    OAHtmlElement ele = new OAHtmlElement("tcFullName", hub, AppUser.P_FullName);
    comp.addColumn(htmlCol, htmlTh, ele);

    htmlCol = new HtmlCol();
    htmlTh = new HtmlTH();
    htmlTh.setInnerHtml("Full Name");

    ele = new OAHtmlElement("tcFirstName", hub, AppUser.P_FirstName);
    comp.addColumn(htmlCol, htmlTh, ele);

    OAInputText txt = new OAInputText("tcLastName", hub, AppUser.P_LastName);
    comp.addColumn("Last Name", txt);
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
<a id="cmdOAReset" href="oareset.jsp">oareset</a>
&nbsp;&nbsp;&nbsp;
<input type=submit value="submit">
<input id="cmdDoAjaxSubmit" type=button value="xxx">
debug
<input id="chkFormDebug" type="checkbox">

<fieldset>
  <legend>Demo HtmlTable</legend>
  <label id="lbl">Test HtmlTable
    <table id="table" border="2">
    </table>
  </label> <br>

  <p>
    <br> <br>
    <%@ include file="htmlElementPropertyEditor.jsp"%>
</fieldset>

<%@ include file="include/htmlFooter.jspf"%>



