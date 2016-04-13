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
<%@ page import="java.net.URI" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.Service" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.Dialog" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.CreationFactory" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ResourceShape" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.QueryCapability" %>


<%
String baseURI = (String)request.getAttribute("baseUri");
Service service = (Service)request.getAttribute("service");
String target = (String)request.getAttribute("targetUri");
String toolName = (String)request.getAttribute("targetToolName");
ServiceProvider serviceProvider = (ServiceProvider)request.getAttribute("serviceProvider");

//OSLC Dialogs
Dialog [] selectionDialogs = service.getSelectionDialogs();
String selectionDialog = selectionDialogs[0].getDialog().toString();
Dialog [] creationDialogs = service.getCreationDialogs();
String creationDialog = creationDialogs[0].getDialog().toString();

//OSLC CreationFactory and shape
CreationFactory [] creationFactories = service.getCreationFactories();
String creationFactory = creationFactories[0].getCreation().toString();
URI[] creationShapes = creationFactories[0].getResourceShapes();
String creationShape = creationShapes[0].toString();

//OSLC QueryCapability and shape
QueryCapability [] queryCapabilities= service.getQueryCapabilities();
String queryCapability = queryCapabilities[0].getQueryBase().toString();
String queryShape = queryCapabilities[0].getResourceShape().toString();


%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		<title><%=toolName%> OSLC Adapter: Service Provider for <%= serviceProvider.getTitle() + "(" + serviceProvider.getIdentifier() + ")" %></title>
		
	</head>
	<body onload="">
	
		<div id="header">
			<div id="banner"></div>
			<table border="0" cellspacing="0" cellpadding="0" id="titles">
				<tr>
					<td id="title">
						<p>
							IBM BlueworksLive OSLC Adapter: Service Provider
						</p>
					</td>
					<td id="information">
						<p class="header_addl_info">
							version 1.0
						</p>
					</td>
				</tr>
			</table>
		</div>
		
		<div id="bugzilla-body">
			<div id="page-index">
			
				<img src="<%=baseURI %>/images/resources/BlueworksPreviewLogo.gif" alt="icon" width="156" height="28" />
	
				<h1>Service Provider for <%= serviceProvider.getTitle() + "(" + serviceProvider.getIdentifier() + ")" %></h1>
				
				<p>Enables navigation to OSLC-RM Resource Selector Dialogs, to enable linking to any BlueworksLive process or activity.</p>

	            <table>
		            <tr>
			            <td><b>This document</b>:</td>
			            <td><a href="<%= serviceProvider.getAbout() %>">
			            <%= serviceProvider.getAbout() %></a></td>
		            </tr>
		            <tr>
			            <td><b>"<%= toolName %>"</b>:</td>
			            <td><a href="<%= target %>"><%= target %></a></td>
		            </tr>
		            <tr>
			            <td><b>Adapter Publisher</b>:</td>
			            <td><%= serviceProvider.getPublisher().getTitle() %></td>
		            </tr>
		            <tr>
			            <td><b>Adapter Identity:</b>:</td>
			            <td><%= serviceProvider.getIdentifier() %></td>
		            </tr>
	            </table>
						
				<h2>OSLC-RM Resource Selector Dialog</h2>
				<p><a href="<%= selectionDialog %>">
				            <%= selectionDialog %></a></p>
				
				<h2>OSLC-RM Resource Creator Dialog</h2>
				<p><a href="<%= creationDialog %>">
				            <%= creationDialog %></a></p>
			
				<h2>OSLC-RM Resource Creation Factory and Resource Shape</h2>
				<p><a href="<%= creationFactory %>">
				            <%= creationFactory %></a></p>
				<p><a href="<%= creationShape %>">
				            <%= creationShape %></a></p>
				
				<h2>OSLC-RM Resource Query Capability and Resource Shape</h2>
				<p><a href="<%= queryCapability %>">
				            <%= queryCapability %></a></p>
				<p><a href="<%= queryShape %>">
				            <%= queryShape %></a></p>
			</div>
		</div>
		
		<div id="footer">
			<div class="intro"></div>
			<div class="outro">
				<div style="margin: 0 1em 1em 1em; line-height: 1.6em; text-align: left">
					<b>Based on</b> the OSLC Tools Adapter Server 0.1 brought to you by <a href="http://eclipse.org/lyo">Eclipse Lyo</a><br>
				</div>
			</div>
		</div>
	</body>
</html>