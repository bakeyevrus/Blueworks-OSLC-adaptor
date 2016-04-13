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
 
    Sam Padgett	  	- initial API and implementation
--%>
<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored ="false" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">

<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <meta name="viewport" content="width=device-width">
    <meta name="description" content="Blueworks Live helps you discover, design, automate and manage your business processes in the cloud." />
    <title>Connect to BlueworksLive</title>
    <style type='text/css'>#pageHeaderContent .customTextLogo, #page-header .customTextLogo, .headerLogoOnPlayback .customTextLogo, .headerLogoOnViewer .customTextLogo {display: none;}</style>
    <link href="<%=request.getContextPath()%>/oauth/stylesheets/blueworkslive.css" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath()%>/oauth/stylesheets/theme.css" rel="stylesheet" type="text/css">
    <jsp:include page="/oauth/common.jsp"/>
    <script
	  data-dojo-config="async: true"
      type="text/javascript"
      src="//ajax.googleapis.com/ajax/libs/dojo/1.7.1/dojo/dojo.js">
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/oauth/scripts/login.js"></script>
</head>

<body class="login">
    
	<div id="mainContent" role="main">
	    <div id="bwlLogo" role="banner"></div>
        <div id="bwlLogin">

				<form id="loginForm" name="loginForm">
				
					 <a href="http://www.blueworkslive.com/home"><img id="logo" src="https://static.blueworkslive.com/skins/Default/images/newimages/logo/blueworkslive_logo_292x38.png"/></a>
           
		
					<div id="error" class="error" style="display: none;"></div>
					<input type="hidden" name="requestToken" value="<c:out value="${requestToken}"/>">
					<input type="hidden" id="callback" value="<c:out value="${callback}"/>">
					
					<div>
						<label for="id" class="loginLabel">Email</label>
					</div>
					<div>
					 	<input id="id" name="id" type="text" class="text" required autofocus></input>
						<script type="text/javascript">
							// If no native HTML5 autofocus support, focus the ID field using JavaScript.
				    		if (!("autofocus" in document.createElement("input"))) {
				      			document.getElementById("id").focus();
				    		}
				  		</script>
					</div>
			
					<div>
						<label for="password" class="loginLabel">Password</label>
					</div>
						<input id="password" name="password" type="password" class="password"></input>
					<div>
					</div>
					<div>
						<input type="submit"  id="loginButton" value="Login to BlueworksLive" autocomplete="off" style="color: white; background-color: #008ABF; border-style: none">
						<button type="button" class="squareTextButton" id="cancel">Cancel</button>
					</div>
				</form>
			
		</div>

</body>

</html>
