/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.rss.reader.service;

import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEEDS_FOLDER;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEED_CONTAINER_PATH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.rss.reader.runner.UnrestrictedDefaultRssFeedsCopier;
import org.nuxeo.rss.reader.runner.UnrestrictedRssFeedContainerCreator;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Default RSSFeed component implementation, it also default implementation of
 * {@code org.nuxeo.rss.reader.service.RSSFeedService}
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
public class RSSFeedComponent extends DefaultComponent implements
        RSSFeedService {

    private static Log log = LogFactory.getLog(RSSFeedComponent.class);

    @Override
    public void createRssFeedModelContainerIfNeeded(CoreSession session)
            throws ClientException {
        if (!session.exists(new PathRef(RSS_FEED_CONTAINER_PATH))) {
            createRssFeedContainer(session, RSS_FEED_CONTAINER_PATH);
        }
    }

    @Override
    public String getRssFeedModelContainerPath() {
        return RSS_FEED_CONTAINER_PATH;
    }

    protected String getAndCreateUserRssFeedPathContainerIfNeeded(
            String userWorkspace, CoreSession session) throws ClientException {
        String userRssFeedPath = userWorkspace + "/" + RSS_FEEDS_FOLDER;
        if (!session.exists(new PathRef(userRssFeedPath))) {
            new UnrestrictedRssFeedContainerCreator(session, userRssFeedPath).changeACPNeeded(
                    false).runUnrestricted();
            new UnrestrictedDefaultRssFeedsCopier(session, userRssFeedPath,
                    getRssFeedModelContainerPath()).run();
        }
        return userRssFeedPath;
    }

    @Override
    public String getCurrentUserRssFeedModelContainerPath(String userName,
            DocumentModel currentDocument) throws ClientException {
        try {
            DocumentModel userWorkspace = getUserWorkspaceService().getCurrentUserPersonalWorkspace(
                    userName, currentDocument);
            return getAndCreateUserRssFeedPathContainerIfNeeded(
                    userWorkspace.getPathAsString(),
                    CoreInstance.getInstance().getSession(
                            currentDocument.getSessionId()));
        } catch (Exception e) {
            throw ClientException.wrap(e);
        }
    }

    protected void createRssFeedContainer(CoreSession session, String path)
            throws ClientException {
        new UnrestrictedRssFeedContainerCreator(session, path).runUnrestricted();
    }

    protected UserWorkspaceService getUserWorkspaceService() throws Exception {
        return Framework.getService(UserWorkspaceService.class);
    }
}
