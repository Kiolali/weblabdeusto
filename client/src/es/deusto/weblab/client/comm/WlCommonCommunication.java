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
*         Jaime Irurzun <jaime.irurzun@gmail.com>
*
*/

package es.deusto.weblab.client.comm;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.DOM;

import es.deusto.weblab.client.comm.callbacks.ISessionIdCallback;
import es.deusto.weblab.client.comm.callbacks.IUserInformationCallback;
import es.deusto.weblab.client.comm.callbacks.IVoidCallback;
import es.deusto.weblab.client.comm.callbacks.IWlAsyncCallback;
import es.deusto.weblab.client.comm.exceptions.CommunicationException;
import es.deusto.weblab.client.comm.exceptions.SerializationException;
import es.deusto.weblab.client.comm.exceptions.WlServerException;
import es.deusto.weblab.client.comm.exceptions.core.SessionNotFoundException;
import es.deusto.weblab.client.comm.exceptions.login.InvalidCredentialsException;
import es.deusto.weblab.client.comm.exceptions.login.LoginException;
import es.deusto.weblab.client.configuration.IConfigurationManager;
import es.deusto.weblab.client.dto.SessionID;
import es.deusto.weblab.client.dto.users.User;

public abstract class WlCommonCommunication implements IWlCommonCommunication {
	
	public static final String WEBLAB_LOGIN_SERVICE_URL_PROPERTY = "weblab.service.login.url";
	public static final String DEFAULT_WEBLAB_LOGIN_SERVICE_URL = "/weblab/login/json/";
	public static final String WEBLAB_SERVICE_URL_PROPERTY = "weblab.service.url";
	public static final String DEFAULT_WEBLAB_SERVICE_URL = "/weblab/json/";

	protected final IConfigurationManager configurationManager;
	protected IWlCommonSerializer serializer;
	
	protected WlCommonCommunication(IConfigurationManager configurationManager){
		this.configurationManager = configurationManager;
		this.serializer = this.createSerializer();
	}

	protected abstract IWlCommonSerializer createSerializer();

	protected void setSerializer(IWlCommonSerializer serializer){
		this.serializer = serializer;
	}
	
	private String getServiceUrl(){
		return this.configurationManager.getProperty(
					WlCommonCommunication.WEBLAB_SERVICE_URL_PROPERTY,
					WlCommonCommunication.DEFAULT_WEBLAB_SERVICE_URL
				);
	}
	
	protected void performRequest(String requestSerialized, IWlAsyncCallback failureCallback, RequestCallback rci){
		final RequestBuilder rb = this.createRequestBuilder(RequestBuilder.POST, this.getServiceUrl());
		try {
			rb.sendRequest(requestSerialized, rci);
		} catch (final RequestException e) {
			failureCallback.onFailure(new CommunicationException(e.getMessage(), e));
		}
	}	
	
	private String getLoginServiceUrl(){
		return this.configurationManager.getProperty(
					WlCommonCommunication.WEBLAB_LOGIN_SERVICE_URL_PROPERTY,
					WlCommonCommunication.DEFAULT_WEBLAB_LOGIN_SERVICE_URL
				);
	}
	
	// For testing purposes
	protected RequestBuilder createRequestBuilder(RequestBuilder.Method method, String url){
		return new RequestBuilder(method, url);
	}
	
	private void performLoginRequest(String requestSerialized,
			IWlAsyncCallback failureCallback, RequestCallback rci) {
	        	final RequestBuilder rb = this.createRequestBuilder(RequestBuilder.POST, this.getLoginServiceUrl());
	        	try {
	        		rb.sendRequest(requestSerialized, rci);
	        	} catch (final RequestException e) {
	        		failureCallback.onFailure(new CommunicationException(e.getMessage(), e));
	        	}
		}

	private class LoginRequestCallback extends WlRequestCallback{
		private final ISessionIdCallback sessionIdCallback;
		private final String username;
		private final String password;
		
		public LoginRequestCallback(ISessionIdCallback sessionIdCallback, String username, String password){
			super(sessionIdCallback);
			this.sessionIdCallback = sessionIdCallback;
			this.username = username;
			this.password = password;
		}
		
		@Override
		public void onSuccessResponseReceived(String response) {
			SessionID sessionId;
			try {
				sessionId = WlCommonCommunication.this.serializer.parseLoginResponse(response);
			} catch (final SerializationException e) {
				this.sessionIdCallback.onFailure(e);
				return;
			} catch (final InvalidCredentialsException e) {
				this.sessionIdCallback.onFailure(e);
				return;
			} catch (final LoginException e) {
				this.sessionIdCallback.onFailure(e);
				return;
			} catch (final WlServerException e) {
				this.sessionIdCallback.onFailure(e);
				return;
			}
			this.launchBrowserPasswordManager();
			this.sessionIdCallback.onSuccess(sessionId);
		}

		private void launchBrowserPasswordManager() {
			final Element usernameField = DOM.getElementById("hiddenUsername");
			final Element passwordField = DOM.getElementById("hiddenPassword");
			final Element submitField  = DOM.getElementById("hiddenLoginFormSubmitButton");
			
			if(usernameField == null || passwordField == null || submitField == null)
				return;
			
			if(usernameField instanceof InputElement)
				((InputElement)usernameField).setValue(this.username);
			else return;
			
			if(passwordField instanceof InputElement)
				((InputElement)passwordField).setValue(this.password);
			else return;
			
			if(submitField instanceof InputElement)
				((InputElement)submitField).click();
		}
	}

	@Override
	public void login(String username, String password, ISessionIdCallback callback) {
		String requestSerialized;
		try {
			requestSerialized = this.serializer.serializeLoginRequest(username, password);
		} catch (final SerializationException e1) {
			callback.onFailure(e1);
			return;
		}
		this.performLoginRequest(
				requestSerialized, 
				callback, 
				new LoginRequestCallback(callback, username, password)
			);
	}
	


	private class LogoutRequestCallback extends WlRequestCallback{
		private final IVoidCallback voidCallback;
		
		public LogoutRequestCallback(IVoidCallback voidCallback){
			super(voidCallback);
			this.voidCallback = voidCallback;
		}
		
		@Override
		public void onSuccessResponseReceived(String response) {
			try {
				WlCommonCommunication.this.serializer.parseLogoutResponse(response);
			} catch (final SerializationException e) {
				this.voidCallback.onFailure(e);
				return;
			} catch (final WlServerException e) {
				this.voidCallback.onFailure(e);
				return;
			}
			this.voidCallback.onSuccess();
		}
	}

	@Override
	public void logout(SessionID sessionId, IVoidCallback callback) {
		String requestSerialized;
		try {
			requestSerialized = this.serializer.serializeLogoutRequest(sessionId);
		} catch (final SerializationException e1) {
			callback.onFailure(e1);
			return;
		}
		this.performRequest(
				requestSerialized, 
				callback, 
				new LogoutRequestCallback(callback)
			);
	}	
	
	private class UserInformationRequestCallback extends WlRequestCallback{
		private final IUserInformationCallback userInformationCallback;
		
		public UserInformationRequestCallback(IUserInformationCallback userInformationCallback){
			super(userInformationCallback);
			this.userInformationCallback = userInformationCallback;
		}
		
		@Override
		public void onSuccessResponseReceived(String response) {
			User user;
			try {
				user = WlCommonCommunication.this.serializer.parseGetUserInformationResponse(response);
			} catch (final SerializationException e) {
				this.userInformationCallback.onFailure(e);
				return;
			} catch (final SessionNotFoundException e) {
				this.userInformationCallback.onFailure(e);
				return;
			} catch (final WlServerException e) {
				this.userInformationCallback.onFailure(e);
				return;
			}
			this.userInformationCallback.onSuccess(user);
		}
	}
	
	@Override
	public void getUserInformation(SessionID sessionId, IUserInformationCallback callback) {
		String requestSerialized;
		try {
			requestSerialized = this.serializer.serializeGetUserInformationRequest(sessionId);
		} catch (final SerializationException e1) {
			callback.onFailure(e1);
			return;
		}
		this.performRequest(
				requestSerialized, 
				callback, 
				new UserInformationRequestCallback(callback)
			);
	}	
	
	
}
