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

package org.nuxeo.rss.reader.manager.seam;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webapp.base.InputController;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.rss.reader.service.RSSFeedService;

/**
 * Action bean to manage RSS Gadget preference
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
@Name("rssGadgetPreferenceActions")
@Scope(ScopeType.PAGE)
@Install(precedence = FRAMEWORK)
public class RssGadgetPreferenceActions extends InputController implements
        Serializable {

    private static final long serialVersionUID = -1L;

    @In(create = true)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient RSSFeedService rssFeed;

    protected DocumentModel preference = null;

    protected boolean showForm = false;

    public boolean isShowForm() {
        return showForm;
    }

    public void toggleForm() {
        showForm = !showForm;
        preference = null;
    }

    public DocumentModel getPreference() throws ClientException {
        if (preference == null) {
            preference = rssFeed.getRssReaderManagementContainer(documentManager);
        }
        return preference;
    }

    public String getRssReaderManagementContainerPath() throws ClientException {
        return rssFeed.getRssReaderManagementContainer(documentManager).getPathAsString();
    }

    public void saveDocument() throws ClientException {
        Events.instance().raiseEvent(EventNames.BEFORE_DOCUMENT_CHANGED,
                getPreference());
        preference = documentManager.saveDocument(getPreference());
        documentManager.save();

        facesMessages.add(StatusMessage.Severity.INFO,
                resourcesAccessor.getMessages().get("document_modified"),
                resourcesAccessor.getMessages().get(getPreference().getType()));
        EventManager.raiseEventsOnDocumentChange(getPreference());
        toggleForm();
    }

    public void reset() {
        preference = null;
    }
}
