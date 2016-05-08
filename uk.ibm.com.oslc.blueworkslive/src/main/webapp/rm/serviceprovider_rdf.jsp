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
<%
String baseUri = (String) request.getAttribute("baseUri");
%>
<?xml version="1.0" encoding="UTF-8"?><oslc_rm:ServiceDescriptor xmlns:oslc_rm="http://open-services.net/xmlns/rm/1.0/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" rdf:about="https://clmwb.ibm.com:9443/dm/oslc_rm/_sJM6grsLEeOXetwkZewzsw" xmlns:dcterms="http://purl.org/dc/terms/">
  <dcterms:title>Blueworks</dcterms:title>
  <dcterms:description>Blueworks</dcterms:description>
  <oslc_rm:requirements>
    <oslc_rm:SelectionDialog>
      <dcterms:title>Blueworks</dcterms:title>
      <oslc_rm:widget rdf:resource="<%= baseUri + "/services/1/requirements/selector" %>" />
      <oslc_rm:widthHint>800px</oslc_rm:widthHint>
      <oslc_rm:heightHint>475px</oslc_rm:heightHint>
    </oslc_rm:SelectionDialog>
  </oslc_rm:requirements>
  <oslc_rm:collections/>
  <oslc_rm:links>
    <oslc_rm:LinkCreationServices>
      <oslc_rm:implementedByLinkFactory rdf:resource="<%= baseUri + "/services/1/requirements/foo" %>"/>
    </oslc_rm:LinkCreationServices>
  </oslc_rm:links>
</oslc_rm:ServiceDescriptor>