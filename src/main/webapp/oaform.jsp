<%@page trimDirectiveWhitespaces="true"%>
<%@page language="java" errorPage="oaerror.jsp"%>
<%@page import="java.io.*, java.util.*"%> 
<%@page import="java.util.logging.* "%>
<%@page import="com.viaoa.object.*, com.viaoa.hub.*, com.viaoa.util.*, com.viaoa.jsp.*"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%-- OAJSP hierarchy: System/Application/Session/Form --%>
<jsp:useBean id="oasystem" scope="application" class="com.viaoa.jsp.OASystem" />

<%!static Logger LOG = Logger.getLogger("oajsp");%>

<%
    String applicationId = "oajsp";
    OAApplication oaapplication = oasystem.getApplication(applicationId, application);
    OASession oasession = oaapplication.getSession(session);
    OAForm form = null;
%>

<%
    request.setCharacterEncoding("UTF-8");

    if (!true) {
        System.out.println("oaform.jsp ----------------- START -------------");
        System.out.print("realPath="+application.getRealPath("test"));
        System.out.print(", servletPath="+request.getServletPath());
        System.out.print(", pathInfo="+request.getPathInfo());
        System.out.println(", pathTranslated="+request.getPathTranslated());
        System.out.print("requestURI="+request.getRequestURI());
        System.out.print(", serverName="+request.getServerName());
        System.out.println(" serverPort="+request.getServerPort());
        Enumeration enumx = request.getParameterNames();
        while ( enumx.hasMoreElements()) {
            String name = (String) enumx.nextElement();
            String[] values = request.getParameterValues(name);
            System.out.println( "param: name=" + name + "  value[0]=" + ((values.length==0)?"":values[0]) );
        }
    }

    String forwardPage = null;
    String id = request.getParameter("oaform");
    if (id != null) form = oasession.getForm(id);
    if (form != null) {
        forwardPage = form.processSubmit(oasession, request, response);
        // System.out.println("FormId="+id+", forward="+forwardPage);
        
        if (forwardPage == null) forwardPage = id;
        response.sendRedirect(forwardPage);
    }
    else {
    	System.out.println("Form "+id+" not found for user session");
        response.sendRedirect("login.jsp");
    }
%>
