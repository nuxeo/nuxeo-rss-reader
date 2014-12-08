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

package org.nuxeo.rss.reader.runner;

import static org.nuxeo.rss.reader.manager.api.Constants.RSS_READER_MANAGEMENT_ROOT_NAME;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_READER_MANAGEMENT_ROOT_TYPE;

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
import org.nuxeo.runtime.api.Framework;

/**
 * Unrestricted runner to create Rss Reader Management Containers. This runner can create the administration container
 * and the container for user. For user, you give the userworkspace path into the contructor and call the
 * willSetRightsForAdminitrators(false) to let the default rights inheritated. For the Administration container creation
 * simply call the constructor with the root management path , and call the unrestricted run.
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @author <a href="mailto:bjalon@nuxeo.com">Benjamin JALON</a>
 * @since 5.4.2
 */
public class UnrestrictedRssReaderManagementRootGenerator extends UnrestrictedSessionRunner {

    private static Log log = LogFactory.getLog(UnrestrictedRssReaderManagementRootGenerator.class);

    protected boolean isACPNeeded = true;

    protected String containerPath;

    /**
     * Create Unrestricted runner that will create into the containerPath a rss reader management root
     *
     * @param session
     * @param containerPath
     */
    public UnrestrictedRssReaderManagementRootGenerator(CoreSession session, String containerPath) {
        super(session);
        this.containerPath = containerPath;
    }

    @Override
    public void run() throws ClientException {
        if (!session.exists(new PathRef(containerPath + RSS_READER_MANAGEMENT_ROOT_NAME))) {
            DocumentModel doc = session.createDocumentModel(containerPath, RSS_READER_MANAGEMENT_ROOT_NAME,
                    RSS_READER_MANAGEMENT_ROOT_TYPE);
            doc.setPropertyValue("dc:title", "Rss Feed Models");
            doc = session.createDocument(doc);
            setACP(doc);
            session.save();
        }

    }

    /**
     * Specify if the during the creation of the rss reader container we add rights for administrator groups.
     *
     * @param addACP
     * @return
     */
    public UnrestrictedRssReaderManagementRootGenerator willSetRightsForAdminitrators(boolean addACP) {
        this.isACPNeeded = addACP;
        return this;
    }

    /**
     * This methods set Right Manage everything for all administration groups and gives access read for everyones.
     *
     * @param doc
     * @throws ClientException
     */
    protected void setACP(DocumentModel doc) throws ClientException {
        if (!isACPNeeded) {
            return;
        }

        ACL acl = doc.getACP().getOrCreateACL();
        try {
            for (String administratorGroup : getUserManager().getAdministratorsGroups()) {
                ACE ace = new ACE(administratorGroup, SecurityConstants.EVERYTHING, true);
                acl.add(ace);
            }
        } catch (Exception e) {
            log.error("Cannot set default ACE on " + containerPath, e);
        }
        ACE ace = new ACE(SecurityConstants.EVERYONE, SecurityConstants.READ, true);
        acl.add(ace);

        session.setACP(doc.getRef(), doc.getACP(), true);
        session.save();
    }

    protected UserManager getUserManager() throws Exception {
        return Framework.getService(UserManager.class);
    }

}
