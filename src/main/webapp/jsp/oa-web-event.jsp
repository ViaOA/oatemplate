<%@ include file="include/jspHeader.jspf"%>


<%
StringBuilder sb = new StringBuilder();
try (BufferedReader reader = request.getReader()) {
    String line;
    while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
    }
}
String json = sb.toString();;

webApp.onClientEvent(json);
String js = webApp.getJavaScriptForClient();
if (OAStr.isNotEmpty(js)) {
    System.out.println(""+js);
    out.write(js);
}
%>

<%
out.write("console.log('call to oa-web-event.jsp qs=" + request.getQueryString() + "');");
%>

