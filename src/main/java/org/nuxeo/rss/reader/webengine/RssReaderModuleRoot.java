package org.nuxeo.rss.reader.webengine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.rss.reader.FeedHelper;

@Path("/rssreader")
@WebObject(type = "rssreader")
@Produces("text/html; charset=UTF-8")
public class RssReaderModuleRoot extends ModuleRoot {

    // XXX : to be removed when we'll use RssFeed documents
    private static final String[] URLS = {"http://rss.lemonde.fr/c/205/f/3052/index.rss",
            "http://actu.voila.fr/Magic/XML/rss-a-la-une.xml",
            "http://feeds.feedburner.com/fubiz"};

    @GET
    @Path("/item")
    public Object getItemContent(@QueryParam("i") String id) {
        return getView("item").args(FeedHelper.searchFeedEntry(URLS, id));
    }

    @GET
    @Path("/sample")
    public Object getSample() {
        return getView("sample");
    }

}
