<%@page trimDirectiveWhitespaces="true"%>
<%@page language="java" errorPage="oaerror.jsp"%>
<%@page import="java.io.*, java.util.*"%> 
<%@page import="java.util.logging.* "%>
<%@page import="com.template.util.* "%>
<%@page import="com.viaoa.object.*, com.viaoa.hub.*, com.viaoa.util.*, com.viaoa.jsp.*"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%-- OAJSP hierarchy: System/Application/Session/Form --%>
<jsp:useBean id="oasystem" scope="application" class="com.viaoa.jsp.OASystem" />

<%!static Logger LOG = Logger.getLogger("oajsp");%>

<%
    String applicationId = "oajsp";
    OAApplication oaapplication = oasystem.getApplication(applicationId, application);
    OASession oasession = oaapplication.getSession(session);
%>

<%
    request.setCharacterEncoding("UTF-8");

    out.print("oainfo.jsp ----------------- START -------------<BR>");
    out.println("<P>");
    out.print("realPath="+application.getRealPath("test"+"<BR>"));
    out.print("servletPath="+request.getServletPath()+"<BR>");
    out.print("pathInfo="+request.getPathInfo()+"<BR>");
    out.println("pathTranslated="+request.getPathTranslated()+"<BR>");
    out.print("requestURI="+request.getRequestURI()+"<BR>");
    out.print("serverName="+request.getServerName()+"<BR>");
    out.println("serverPort="+request.getServerPort()+"<BR>");
    Enumeration enumx = request.getParameterNames();
    while ( enumx.hasMoreElements()) {
        String name = (String) enumx.nextElement();
        String[] values = request.getParameterValues(name);
        out.println( "param: name=" + name + "  value[0]=" + ((values.length==0)?"":values[0]+"<BR>") );
    }

    out.println("<P>");
    for (String s : Util.getInfo()) {
        out.println(s+"<BR>");
    }
%>
