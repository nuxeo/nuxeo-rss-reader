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

package org.nuxeo.rss.reader;

import static org.junit.Assert.assertEquals;import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEEDS_FOLDER;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEED_CONTAINER_PATH;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.rss.reader.service.RSSFeedService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 */
@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(type = BackendType.H2, user = "Administrator", cleanup = Granularity.METHOD)
@Deploy( { "org.nuxeo.rss.reader", "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.platform.userworkspace.types",
        "org.nuxeo.ecm.automation.features", "org.nuxeo.ecm.platform.query.api",
"org.nuxeo.ecm.platform.userworkspace.api", "org.nuxeo.ecm.platform.userworkspace.core"})
public class TestFeedReaderService {
    @Inject
    RSSFeedService rssFeedService;

    @Inject
    CoreSession session;

    @Test
    public void testServiceRegistration() {
        assertNotNull(rssFeedService);
    }

    @Test
    public void testFeedRootsCreation() throws ClientException {

        DocumentModel management = session.createDocumentModel("Workspace");
        management.setPropertyValue("dc:title", "management");
        management.setPathInfo("/", "management");
        session.createDocument(management);

        DocumentModel domain = session.createDocumentModel("Domain");
        domain.setPropertyValue("dc:title", "default-domain");
        domain.setPathInfo("/", "default-domain");
        session.createDocument(domain);

        DocumentRef feedContainer = new PathRef(RSS_FEED_CONTAINER_PATH);
        assertFalse(session.exists(feedContainer));
        rssFeedService.createRssFeedModelContainerIfNeeded(session);
        assertTrue(session.exists(feedContainer));

        DocumentRef userFeedContainer = new PathRef(
                "/default-domain/UserWorkspaces/Administrator/" + RSS_FEEDS_FOLDER);
        assertFalse(session.exists(userFeedContainer));
        rssFeedService.getCurrentUserRssFeedModelContainerPath("Administrator",
                session.getDocument(new PathRef("/default-domain")));
        assertTrue(session.exists(userFeedContainer));
    }
}
