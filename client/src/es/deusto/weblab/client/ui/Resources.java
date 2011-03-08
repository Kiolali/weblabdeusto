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
* Author: Pablo Orduña <pablo@ordunya.com>
*
*/

package es.deusto.weblab.client.ui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	
	@Source("res/logo.png")
	ImageResource logo();
	
	@Source("res/avatar.png")
	ImageResource avatar();
	
	@Source("res/support.png")
	ImageResource support();
	
	@Source("res/demo.png")
	ImageResource demo();
	
	@Source("res/mobile.png")
	ImageResource mobile();
	
	@Source("res/opensource.png")
	ImageResource openSource();
}
