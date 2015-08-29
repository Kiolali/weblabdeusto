/*
* Copyright (C) 2005 onwards University of Deusto
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
package es.deusto.weblab.client.lab.ui;

import es.deusto.weblab.client.configuration.IConfigurationManager;
import es.deusto.weblab.client.lab.controller.ILabController;

public class LabThemeFactory {
	
	public interface IWlLabThemeLoadedCallback{
		public void onThemeLoaded(LabThemeBase theme);
		public void onFailure(Throwable reason);
	}
	
	public static void themeFactory(final IConfigurationManager configurationManager, final ILabController controller, String themeName, boolean mobile, final IWlLabThemeLoadedCallback callback){
	}
}