package org.nuxeo.rss.reader.manager.seam;

import static org.jboss.seam.annotations.Install.FRAMEWORK;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_FEED_TYPE;
import static org.nuxeo.rss.reader.manager.api.Constants.RSS_READER_MANAGEMENT_ROOT_PATH;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.base.InputController;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.rss.reader.service.RSSFeedService;

@Name("rssFeedActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK)
public class RssFeedActions extends InputController implements Serializable {

    private static final long serialVersionUID = 8882417548656036277L;

    protected static final Log log = LogFactory.getLog(RssFeedActions.class);

    @In(create = true)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient DocumentActions documentActions;

    @In(create = true)
    protected transient UserManager userManager;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true)
    protected transient RSSFeedService rssFeed;

    protected DocumentModel currentDocument = null;

    protected boolean showForm = false;

    public DocumentModel getBareFeedReaderModel() throws ClientException {
        return documentManager.createDocumentModel(RSS_FEED_TYPE);
    }

    public DocumentModel getCurrentDocument() throws ClientException {
        if (currentDocument == null) {
            currentDocument = getBareFeedReaderModel();
        }
        return currentDocument;
    }

    public void saveDocument() throws ClientException {
        rssFeed.getRssReaderManagementContainer(documentManager);
        if (currentDocument.getId() == null) {
            // save document
            documentActions.saveDocument(currentDocument);
            EventManager.raiseEventsOnDocumentChange(currentDocument);
        } else {
            // update an existing one
            Events.instance().raiseEvent(EventNames.BEFORE_DOCUMENT_CHANGED,
                    currentDocument);
            currentDocument = documentManager.saveDocument(currentDocument);
            documentManager.save();
            facesMessages.add(
                    StatusMessage.Severity.INFO,
                    resourcesAccessor.getMessages().get("document_modified"),
                    resourcesAccessor.getMessages().get(
                            currentDocument.getType()));
            EventManager.raiseEventsOnDocumentChange(currentDocument);
        }
        resetDocument();
        toggleForm();
    }

    public void setCurrentDocument(String path) throws ClientException {
        if (!(path == null || "".equals(path))) {
            DocumentRef ref = new PathRef(path);
            if (documentManager.exists(ref)) {
                currentDocument = documentManager.getDocument(new PathRef(path));
            } else {
                facesMessages.add(
                        StatusMessage.Severity.WARN,
                        resourcesAccessor.getMessages().get(
                                "Error.Document.Not.Found"));
            }
        }
    }

    protected void resetDocument() {
        currentDocument = null;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public boolean isShowCreateForm() {
        return isShowForm() && currentDocument != null
                && currentDocument.getId() == null;
    }

    public boolean isShowEditForm() {
        return isShowForm() && currentDocument != null
                && currentDocument.getId() != null;
    }

    public void toggleForm() {
        showForm = !showForm;
    }

    public void toggleAndReset() {
        toggleForm();
        resetDocument();
    }

    public String getLink(String link) {

        try {
            navigationContext.setCurrentDocument(rssFeed.getRssReaderManagementContainer(documentManager));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format(
                        "Unable to set the current document to \"%s\"",
                        RSS_READER_MANAGEMENT_ROOT_PATH));
            }
        }
        return link;

    }
}
