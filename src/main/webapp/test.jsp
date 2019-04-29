<%@ include file="include/jspHeader.jspf"%>

<%
String formId = oasession.getRequest().getParameter("formId");
String compId= oasession.getRequest().getParameter("compId");

EmployeeModel model = null;

form = oasession.getForm(formId);
if (form != null && form.getComponent("txtLastName2") == null) {
    OATextField txt = new OATextField("txtLastName2", model.getHub(), AppUser.P_LastName, 10, 35);
    form.add(txt);
}



%>

<%=form==null?"NO Form found":form.getUpdateScript()%>


HEY ... this is from test.jsp <%= (new OADateTime()).toString("MM/dd/yyyy HH:mm:ss") %>
<%=formId %>, <%=compId %>
Last Name2: <input id="txtLastName2" type="text" value="test" size="12" maxlength="3">

