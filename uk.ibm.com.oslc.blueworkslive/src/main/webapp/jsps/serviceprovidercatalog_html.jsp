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
 
    Sam Padgett 	- initial API and implementation
    Michael Fiedler - OSLC4J adaptation
--%>
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog" %>
<%@ page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider" %>
<%
String targetUri = (String) request.getAttribute("targetUri");
String baseUri = (String) request.getAttribute("baseUri");
ServiceProviderCatalog catalog = (ServiceProviderCatalog)request.getAttribute("catalog");
String toolName = (String)request.getAttribute("toolName");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		<title><%= toolName %> OSLC Adapter: Service Provider Catalog</title>
		
	</head>
	<body onload="">
	
		<div id="header">
			<div id="banner"></div>
			<table border="0" cellspacing="" cellpadding="5" id="titles">
				<tr>
					<td id="title">
						<p>
							<%= toolName %> OSLC Adapter: Service Provider Catalog
						</p>
					</td>
					<td id="information">
						<p class="header_addl_info">
							version 0.2
						</p>
					</td>
				</tr>
			</table>
		</div>
		
		<div id="bugzilla-body">
			<div id="page-index">
			
			
			<h1>Service Provider Catalog</h1>
			<img src="<%= baseUri %>/images/resources/BlueworksPreviewLogo.gif" alt="icon" width="160" height="28" />
			
			<p>Enables navigation to any <%= toolName %> process that is being followed.</p>

            <table>
	            <tr>
		            <td><b>This document</b>:</td>
		            <td><a href="<%= catalog.getAbout() %>">
                            <%= catalog.getAbout().toString() %>
                        </a></td>
	            </tr>
	            <tr>
		            <td><b><%= toolName %></b>:</td>
		            <td><a href="<%= targetUri %>"><%= targetUri %></a></td>
	            </tr>
	            <tr>
		            <td><b>Adapter Publisher</b>:</td>
		            <td><%= catalog.getPublisher().getTitle() %></td>
	            </tr>
	            <tr>
		            <td><b>Adapter Identity</b>:</td>
		            <td><%= catalog.getPublisher().getIdentifier() %></td>
	            </tr>
            </table>
			
			<h2>Service Providers</h2>
			
			<% for (ServiceProvider s : catalog.getServiceProviders()) { %>
			<h3>Service Provider for Product <%= s.getTitle() %></h3>
			<p><a href="<%= s.getAbout() %>">
                    <%= s.getAbout() %></a></p>
			<% } %>			
			
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
