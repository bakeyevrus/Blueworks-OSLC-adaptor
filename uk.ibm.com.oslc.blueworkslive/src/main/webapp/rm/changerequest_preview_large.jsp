<!DOCTYPE html>
<%--
 Copyright (c) 2011, 2012 IBM Corporation.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 
 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.
 
 Contributors:
 
    Sam Padgett		 - initial API and implementation
    Michael Fiedler	 - adapt for OSLC4J
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.net.*,java.util.*" %>
<%@ page import="java.net.*,java.util.*,java.text.SimpleDateFormat" %>
<%@ page import="uk.ibm.com.oslc.resources.OSLCRequirement" %>
<%@ page import="uk.ibm.com.oslc.resources.Person" %>
<%
OSLCRequirement requirement = (OSLCRequirement)request.getAttribute("changeRequest");

String title = requirement.getTitle();
String identifier = requirement.getIdentifier()+""; 

%>
<html>
<head>
<title>Change Request: <%=title %> (<%=identifier %>)</title>

</head>
<body>
Not supported in this version of the blueworks live oslc adapter.
</body>
</html>