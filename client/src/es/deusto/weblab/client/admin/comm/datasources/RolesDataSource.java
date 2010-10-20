/*
* Copyright (C) 2005-2009 University of Deusto
* All rights reserved.
*
* This software is licensed as described in the file COPYING, which
* you should have received as part of this distribution.
*
* This software consists of contributions made by many individuals, 
* listed below:
*
* Author: Luis Rodriguez <luis.rodriguez@opendeusto.es>
*
*/

package es.deusto.weblab.client.admin.comm.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

import es.deusto.weblab.client.dto.SessionID;


public class RolesDataSource extends WebLabRestDataSource {

	public RolesDataSource(SessionID sessionId) {
		super(sessionId);
	}
	
	@Override
	public void initialize() {
		
	    final OperationBinding fetch = new OperationBinding();  
	    
	    fetch.setOperationType(DSOperationType.FETCH);  
	    fetch.setDataProtocol(DSProtocol.GETPARAMS);
	    
	    this.setOperationBindings(fetch);
	    
	    // TODO: Get rid of hard-coded strings.
	    final DataSourceIntegerField idField = new DataSourceIntegerField("id", "ID");  
	    idField.setPrimaryKey(true);
	    idField.setCanEdit(false);  
	    final DataSourceTextField nameDSField = new DataSourceTextField("name", "Name");
	    
	    this.setFields(idField, nameDSField);
	    
	    this.setFetchDataURL("data/roles_fetch.js");
	}
	
}
