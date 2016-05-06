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
 
    Sam Padgett 	 - initial API and implementation
    Michael Fiedler	 - adapted for OSLC4J
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%
String baseUri = (String) request.getAttribute("baseUri");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>BlueworksLive OSLC Adapter: Resource Creator</title>

<link href="https://static.blueworkslive.com/skins/Default/04C3BFDA52AB3354D8D2D6C9D072BF96.cache.css" rel="stylesheet" type="text/css">
<style type='text/css'>#pageHeaderContent .customTextLogo, #page-header .customTextLogo, .headerLogoOnPlayback .customTextLogo, .headerLogoOnViewer .customTextLogo {display: none;}</style>
<link href="<%=baseUri %>/stylesheets/theme.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div id="pageHeader" role="banner">
        <div id="pageHeaderLeftImage"></div>
        <div id="pageHeaderContent">
            <a title="Home" href="/home"><div id="pageHeaderCompanyName"></div></a>
        </div>
    </div>
	<div id="contents">
	    <div id="bwlLogin">
	       <div id="contents">
           <p id="errorMessage"><label for="id" class="labelText">Creating new processes is not supported through the BlueworksLive API.</label></p>
		   </div>
		</div>
	</div>
</body>
</html>

