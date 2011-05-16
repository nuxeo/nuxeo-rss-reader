package org.nuxeo.rss.reader.webengine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

@Path("/rssreader")
@WebObject(type = "rssreader")
@Produces("text/html; charset=UTF-8")
public class RssReaderModuleRoot extends ModuleRoot {

    @GET
    @Path("/item")
    public Object getItemContent() {
        return getView("item");
    }
}
