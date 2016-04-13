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
    Michael Fiedler	 - adapted for OSLC4J
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.j2bugzilla.base.*" %>

<%
	String productId = (String) request.getAttribute("productId");
	String selectionUri = (String) request.getAttribute("selectionUri");
	String baseURL = (String)request.getAttribute("baseURL");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>BlueworksLive OSLC Adapter: Resource Selector</title>


<script type="text/javascript" src="../../../bugzilla.js"></script>

<!--  link href="<%=baseURL %>/stylesheets/blueworkslive.css" rel="stylesheet" type="text/css"-->
<style type='text/css'>#pageHeaderContent .customTextLogo, #page-header .customTextLogo, .headerLogoOnPlayback .customTextLogo, .headerLogoOnViewer .customTextLogo {display: none;}</style>
<link href="<%=baseURL %>/stylesheets/theme.css" rel="stylesheet" type="text/css">
<!--  link href="../stylesheets/theme.css" rel="stylesheet" type="text/css"-->
</head>
<body>
	<div id=searchContents>
	    <div id="bwlRequirementProperties" style="width: 425px; height:395px">
       
			<p id="searchMessage"><label for="id" class="headingText"  style="width: 400px">Search for BlueworksLive process by name.</label></p>
	
			<p id="loadingMessage" style="display: none;"><label for="id" class="labelText">Currently searching BlueWorksLive. 
				Please wait...</label></p>
	
			<div>
				<input type="search" style="width: 340px" id="searchTerms" placeholder="Enter Process Name" autofocus>
				<button type="button"
					onclick="search( '<%= selectionUri %>' )">Search</button>
			</div>
	
			<div style="margin-top: 5px;">
				<select id="results" size=7 style="width: 425px; height:310px"></select>
			</div>
	
			<div style="width: 425px; margin-top: 5px;">
				<button style="float: right;" type="button"
					onclick="javascript: cancel()">Cancel</button>
				<button style="float: right;margin-right: 10px;" type="button"
					onclick="javascript: select();">OK</button>
			</div>
			
			<%-- So the buttons don't float outside the content area. --%>
			<div style="clear: both;"></div>
        </div>
     </div>
</body>
</html>