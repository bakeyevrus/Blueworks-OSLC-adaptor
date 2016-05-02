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
    Michael Fiedler	 - adapted for OSLC4J
--%>
<%@ page contentType="application/json" language="java" pageEncoding="UTF-8" %>
<%@page import="uk.ibm.com.oslc.resources.OSLCRequirement"%>
<%@ page import="java.net.*,java.util.*" %> 

{
<% 
List<OSLCRequirement> results = (List<OSLCRequirement>)request.getAttribute("results");
%>
results: [
<% int i = 0; for (OSLCRequirement b : results) { %>
   <% if (i > 0) { %>,<% } %>
   {  "title" : "<%=b.getDisplayIndent()%><%= b.getTitle() %> (<%=b.getRequirementType().toLowerCase()%>)",
      "shape" : "<%= b.getInstanceShape() %>",
      "resource" : "<%= b.getAbout() %>"
   }
<% i++; } %>
]
}
