/*******************************************************************************
 * Copyright (c) 2016 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.vscode.java.internal;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jboss.tools.vscode.java.internal.handlers.LogHandler;

public class JavaClientConnection {


	public interface JavaLanguageClient extends LanguageClient{
		/**
		 * The show message notification is sent from a server to a client to ask
		 * the client to display a particular message in the user interface.
		 */
		@JsonNotification("language/status")
		public abstract void sendStatusReport(StatusReport report);
	}

	private final LogHandler logHandler;
	private final JavaLanguageClient client;

	public JavaClientConnection(JavaLanguageClient client) {
		this.client = client;
		logHandler = new LogHandler();
		logHandler.install(this);
	}

	/**
	 * Sends the logMessage message back to the client as a notification
	 * @param msg The message to send back to the client
	 */
	public void logMessage(MessageType type, String msg) {
		MessageParams $= new MessageParams();
		$.setMessage(msg);
		$.setType(type);
		client.logMessage($);
	}


	/**
	 * Sends the message to the client to be displayed on a UI element.
	 *
	 * @param type
	 * @param msg
	 */
	public void showNotificationMessage(MessageType type, String msg){
		MessageParams $ = new MessageParams();
		$.setMessage(msg);
		$.setType(type);
		client.showMessage($);
	}


	/**
	 * Sends a status to the client to be presented to users
	 * @param msg The status to send back to the client
	 */
	public void sendStatus(ServiceStatus serverStatus, String status) {
		StatusReport $ = new StatusReport();
		client.sendStatusReport( $.withMessage(status).withType(serverStatus.name()));
	}

	public void publishDiagnostics(PublishDiagnosticsParams diagnostics){
		client.publishDiagnostics(diagnostics);
	}

	public void disconnect() {
		if (logHandler != null) {
			logHandler.uninstall();
		}
	}

}