package org.nuxeo.rss.reader.webengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.rss.reader.FeedHelper;
import org.nuxeo.rss.reader.service.RSSFeedService;
import org.nuxeo.runtime.api.Framework;

@Path("/rssreader")
@WebObject(type = "rssreader")
@Produces("text/html; charset=UTF-8")
public class RssReaderModuleRoot extends ModuleRoot {

    @GET
    @Path("/item")
    public Object getItemContent(@QueryParam("i") String id,
            @QueryParam("language") String language) throws Exception {
        WebEngine.getActiveContext().setLocale(new Locale(language));
        RSSFeedService rssFeedService = Framework.getLocalService(RSSFeedService.class);
        List<String> urls = rssFeedService.getCurrentUserRssFeedAddresses(ctx.getCoreSession());
        return getView("item").args(
                FeedHelper.searchFeedEntry(
                        urls.toArray(new String[urls.size()]), id));
    }

    @GET
    @Path("/sample")
    public Object getSample() {
        return getView("sample");
    }

    @GET
    @Path("/config")
    public Object getConfigPage(@QueryParam("language") String language)
            throws Exception {
        WebEngine.getActiveContext().setLocale(new Locale(language));
        CoreSession session = ctx.getCoreSession();
        boolean isAbleToCreateNew = false;
        RSSFeedService rssFeedService = Framework.getLocalService(RSSFeedService.class);
        DocumentModelList userFeeds = rssFeedService.getCurrentUserRssFeedDocumentModelList(session);
        DocumentModelList globalFeeds = rssFeedService.getGlobalFeedsDocumentModelList(session);
        List<DocumentModel> options = new ArrayList<DocumentModel>();
        if (userFeeds != null && globalFeeds != null) {
            for (DocumentModel f1 : globalFeeds) {
                boolean found = false;
                String url1 = (String) f1.getPropertyValue("rf:rss_address");
                for (DocumentModel f2 : userFeeds) {
                    String url2 = (String) f2.getPropertyValue("rf:rss_address");
                    if (url2 != null && url2.equals(url1)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    options.add(f1);
                }
            }
            isAbleToCreateNew = userFeeds.size() < rssFeedService.getMaximumFeedsCount(session);
        }

        return getView("feed_configuration").arg("userFeeds", userFeeds).arg(
                "globalFeeds", options).arg("ableToCreateNew",
                isAbleToCreateNew).arg("maxFeedsCount",
                rssFeedService.getMaximumFeedsCount(session));
    }

    @POST
    @Path("/removeFeed")
    public Object removeFeed(@FormParam("id") String id,
            @QueryParam("language") String language) throws Exception {
        CoreSession session = ctx.getCoreSession();
        session.removeDocument(new IdRef(id));
        session.save();
        return getConfigPage(language);
    }

    @POST
    @Path("/addGlobalFeed")
    public Object addGlobalFeed(@FormParam("feedId") String feedId,
            @QueryParam("language") String language) throws Exception {
        CoreSession session = ctx.getCoreSession();
        RSSFeedService rssFeedService = Framework.getLocalService(RSSFeedService.class);
        DocumentModelList userFeeds = rssFeedService.getCurrentUserRssFeedDocumentModelList(session);
        if (userFeeds != null
                && userFeeds.size() >= rssFeedService.getMaximumFeedsCount(session)) {
            return getConfigPage(language);
        }

        String userFeedsContainer = rssFeedService.getCurrentUserRssFeedsContainer(
                session).getPathAsString();
        session.copy(new IdRef(feedId), new PathRef(userFeedsContainer), null);
        return getConfigPage(language);
    }

    @POST
    @Path("/addNewFeed")
    public Object addGlobalFeed(@FormParam("feedName") String feedName,
            @FormParam("feedLink") String url,
            @QueryParam("language") String language) throws Exception {
        CoreSession session = ctx.getCoreSession();
        RSSFeedService rssFeedService = Framework.getLocalService(RSSFeedService.class);
        DocumentModelList userFeeds = rssFeedService.getCurrentUserRssFeedDocumentModelList(session);
        if (userFeeds != null
                && userFeeds.size() >= rssFeedService.getMaximumFeedsCount(session)) {
            return getConfigPage(language);
        }

        String userFeedsContainerPath = rssFeedService.getCurrentUserRssFeedsContainer(
                session).getPathAsString();
        DocumentModel feed = session.createDocumentModel(
                userFeedsContainerPath, feedName, "RssFeed");
        feed.setPropertyValue("dc:title", feedName);
        feed.setPropertyValue("rf:rss_address", url);
        feed = session.createDocument(feed);
        session.save();
        return getConfigPage(language);
    }

}
