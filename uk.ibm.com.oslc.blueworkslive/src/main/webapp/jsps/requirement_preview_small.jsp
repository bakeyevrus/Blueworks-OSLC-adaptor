<!DOCTYPE html>
<!--
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
 -->
<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>
<%@ page import="uk.ibm.com.oslc.resources.OSLCRequirement" %>


<%
OSLCRequirement requirement = (OSLCRequirement) request.getAttribute("requirement");
String baseUri = (String) request.getAttribute("baseUri");

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />

<script type="text/javascript">
        function showHideDescription() {
            var processType = '${processType}';
            if (processType == "PROCESS")
            {
               var descriptionBlock = document.getElementById("descriptionBlock");
               descriptionBlock.style.display = "none";
            }
              if (processType =="ACTIVITY")
            {
               var modifiedByHeading = document.getElementById("modifiedByHeading");
               modifiedByHeading.style.display = "none";
               var modifiedByValue = document.getElementById("modifiedByValue");
               modifiedByValue.style.display = "none";
            }
        }
</script>
<title>Requirement: <%= requirement.getTitle() %>(<%= requirement.getIdentifier() %>))</title>


<style type='text/css'>#pageHeaderContent .customTextLogo, #page-header .customTextLogo, .headerLogoOnPlayback .customTextLogo, .headerLogoOnViewer .customTextLogo {display: none;}</style>
<link href="<%=baseUri %>/stylesheets/theme.css" rel="stylesheet" type="text/css">
<!-- link href="../stylesheets/theme.css" rel="stylesheet" type="text/css"-->
<link rel="shortcut icon" href="<%=baseUri %>/images/resources/BlueworksPreviewLogo.gif">
<style type="text/css">
td {
	padding-right: 5px;
	min-width: 175px;
}

th {
	padding-right: 5px;
	text-align: right;
}
</style>
</head>
<body onload="showHideDescription()">
    <div id="pageHeader" role="banner">
        <div id="pageHeaderLeftImage"></div>
        <div id="pageHeaderContent">
            <a title="Home" href="/home"><div id="pageHeaderCompanyName"></div></a>
        </div>
    </div>
	<div id=contents>
	    <div id="bwlRequirementProperties">
			<table class="edit_form">
				<tr>
					<th><label for="id" class="headingText">Process Name:</label></th>
					<td><label for="id" class="labelText"><%= requirement.getTitle() %></label></td>
					<th><label for="id" class="headingText">Process Type:</label></th>
					<td><label for="id" class="labelText"><%= requirement.getRequirementType() %></label></td>
					
				</tr>
				
				
				<tr>
					<th><label for="id" class="headingText">Last Modified:</label></th>
					<td><label for="id" class="labelText"><%= requirement.getModifiedDateAsString() %></label></td>
					
					<th id=modifiedByHeading><label for="id" class="headingText">By:</label></th>
					<td id=modifiedByValue><label for="id" class="labelText"><%=requirement.getLastModifiedBy()%></label></td>
					
				</tr>
			
				
				<tr>
					<th><label for="id" class="headingText">Parent:</label></th>
					<td><label for="id" class="labelText"><%= requirement.getParentName() %></label></td>
					<th><label for="id" class="headingText">Children:</label></th>
					<td><ul class="linkText"><%= requirement.getChildrenNames() %></ul></td>
				</tr>
				
				
			</table>
		    <br>
		    <div id=descriptionBlock>
			    <label for="id" class="headingText">Description:</label>
			    <br>
			    <div style="width: 568px"><%=requirement.getDescription()%></div>
			</div>
		</div>
	</div>
</body>
</html>