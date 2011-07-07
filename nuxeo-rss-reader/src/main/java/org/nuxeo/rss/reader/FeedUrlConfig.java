/*
 * (C) Copyright 2006-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.rss.reader;

import org.nuxeo.runtime.api.Framework;

/**
 *
 * Helper to manage URL configuration
 *
 * @author <a href="mailto:qlamerand@nuxeo.com">Quentin Lamerand</a>
 */
public class FeedUrlConfig {

    public static final String FEED_PROXY_HOST_PROPERTY = "org.nuxeo.rss.reader.proxy.host";

    public static final String FEED_PROXY_PORT_PROPERTY = "org.nuxeo.rss.reader.proxy.port";

    public static final String FEED_PROXY_LOGIN_PROPERTY = "org.nuxeo.rss.reader.proxy.login";

    public static final String FEED_PROXY_PASSWORD_PROPERTY = "org.nuxeo.rss.reader.proxy.password";

    public static final String NUXEO_PROXY_HOST_PROPERTY = "nuxeo.http.proxy.host";

    public static final String NUXEO_PROXY_PORT_PROPERTY = "nuxeo.http.proxy.port";

    public static final String NUXEO_PROXY_LOGIN_PROPERTY = "nuxeo.http.proxy.login";

    public static final String NUXEO_PROXY_PASSWORD_PROPERTY = "nuxeo.http.proxy.password";

    protected static Boolean useProxy = null;

    protected static Boolean isProxyAuthenticated = null;

    public static boolean useProxy() {
        if (useProxy == null) {
            String host = getProxyHost();
            if (host == null || host.isEmpty() || host.startsWith("$")) {
                useProxy=false;
            } else {
                useProxy = true;
            }
        }
        return useProxy;
    }

    public static boolean isProxyAuthenticated() {
        if (isProxyAuthenticated == null) {
            String login = getProxyLogin();
            if (login == null || login.isEmpty() || login.startsWith("$")) {
                isProxyAuthenticated=false;
            } else {
                isProxyAuthenticated = true;
            }
        }
        return isProxyAuthenticated;
    }

    public static String getProxyHost() {
        return Framework.getProperty(FEED_PROXY_HOST_PROPERTY,
                Framework.getProperty(NUXEO_PROXY_HOST_PROPERTY, null));
    }

    public static int getProxyPort() {
        String portAsString = Framework.getProperty(
                FEED_PROXY_PORT_PROPERTY, Framework.getProperty(
                        NUXEO_PROXY_PORT_PROPERTY, null));
        if (portAsString == null || portAsString.isEmpty() || portAsString.startsWith("$")) {
            return 80;
        }
        try {
           return Integer.parseInt(portAsString);
        } catch (NumberFormatException e) {
           return 80;
        }
    }

    public static String getProxyLogin() {
        return Framework.getProperty(
                FEED_PROXY_LOGIN_PROPERTY,
                Framework.getProperty(NUXEO_PROXY_LOGIN_PROPERTY, null));
    }

    public static String getProxyPassword() {
        return Framework.getProperty(FEED_PROXY_PASSWORD_PROPERTY,
                Framework.getProperty(NUXEO_PROXY_PASSWORD_PROPERTY,
                        null));
    }

}
