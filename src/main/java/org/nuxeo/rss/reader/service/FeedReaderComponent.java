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

import static org.nuxeo.ecm.core.management.storage.DocumentStoreManager.MANAGEMENT_ROOT_PATH;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEEDS_FOLDER;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEED_CONTAINER_PATH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.rss.reader.manager.api.Constants;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Default FeedReader component implementaion, it also default implemention of
 * {@code org.nuxeo.rss.reader.service.FeedReaderService}
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
public class FeedReaderComponent extends DefaultComponent implements
        FeedReaderService {
    private static Log log = LogFactory.getLog(FeedReaderComponent.class);

    @Override
    public void createRssFeedModelContainerIfNeeded(CoreSession session)
            throws ClientException {
        if (!session.exists(new PathRef(RSS_FEED_CONTAINER_PATH))) {
            createRssFeedContainer(session, RSS_FEED_CONTAINER_PATH);
        }
    }

    protected void createRssFeedContainer(CoreSession session, String path)
            throws ClientException {
        new UnrestrictedRssFeedContainerCreator(session, path).runUnrestricted();
    }

    protected UserManager getUserManager() throws Exception {
        return Framework.getService(UserManager.class);
    }

    protected class UnrestrictedRssFeedContainerCreator extends
            UnrestrictedSessionRunner {

        protected String rssFeedModelContainerPath;

        protected UnrestrictedRssFeedContainerCreator(CoreSession session,
                String reportModelsContainerPath) {
            super(session);
            this.rssFeedModelContainerPath = reportModelsContainerPath;
        }

        @Override
        public void run() throws ClientException {
            if (!session.exists(new PathRef(rssFeedModelContainerPath))) {
                DocumentModel doc = session.createDocumentModel(
                        MANAGEMENT_ROOT_PATH, RSS_FEEDS_FOLDER,
                        Constants.RSS_FEED_ROOT_TYPE);
                doc.setPropertyValue("dc:title", "Rss Feed Models");
                doc = session.createDocument(doc);

                ACP acp = new ACPImpl();
                ACL acl = new ACLImpl();
                try {
                    for (String administratorGroup : getUserManager().getAdministratorsGroups()) {
                        ACE ace = new ACE(administratorGroup,
                                SecurityConstants.EVERYTHING, true);
                        acl.add(ace);
                    }
                } catch (Exception e) {
                    log.error("Cannot set default ACE on FeedReader root path",
                            e);
                }
                acp.addACL(acl);
                doc.setACP(acp, true);
                session.save();
            }
        }
    }
}
