/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package uk.ibm.com.oslc.exception;

import javax.servlet.http.HttpServletResponse;

import uk.ibm.com.oslc.rm.IRequirementsConnector;

/**
 * Corresponds to an HTTP 401 response.
 * 
 * @author Samuel Padgett <spadgett@us.ibm.com>
 */
public class UnauthorizedException extends RestException {
	public UnauthorizedException() {
		super(HttpServletResponse.SC_UNAUTHORIZED,
				"You must authenticate with " + IRequirementsConnector.getInstance().getToolName() +  " for this request.");
	}
	
	public UnauthorizedException(String message) {
		super(HttpServletResponse.SC_UNAUTHORIZED, message);
	}
	
	public UnauthorizedException(Throwable t) {
		super(t, HttpServletResponse.SC_UNAUTHORIZED);
	}
}
