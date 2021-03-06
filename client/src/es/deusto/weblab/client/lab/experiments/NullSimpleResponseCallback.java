/*
* Copyright (C) 2012 onwards University of Deusto
* All rights reserved.
*
* This software is licensed as described in the file COPYING, which
* you should have received as part of this distribution.
*
* This software consists of contributions made by many individuals, 
* listed below:
*
* Author: FILLME
*
*/

package es.deusto.weblab.client.lab.experiments;

final class NullSimpleResponseCallback implements ISimpleResponseCallback {
	final static ISimpleResponseCallback NULL_RESPONSE_CALLBACK = new NullSimpleResponseCallback();


	@Override
	public void onFailure(String e) {
	}
	@Override
	public void onSuccess(String responseCommand) {
	}
}