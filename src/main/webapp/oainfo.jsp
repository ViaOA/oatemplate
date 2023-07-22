<%@ include file="include/jspImport.jspf"%> 
<%
    if (false || oasession.getCalcDebug()) {
        System.out.println("JSP oainfo.jsp");
        OAWebUtil.debug(application, oasession, request, response);
    }

    form = oasession.getCurrentForm();     
    out.println("<P>");
    for (String s : Util.getInfo()) {
        out.println(s+"<BR>");
    }
%>
