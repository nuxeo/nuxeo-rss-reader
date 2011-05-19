<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
  <title>${Context.getMessage('label.rss.feed.item')}</title>

  <link type="text/css" rel="stylesheet" href="${skinPath}/css/rssreader-item.css" />

  <script type="text/javascript" src="${skinPath}/script/jquery/jquery.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $('.itemContent a').attr("target", "_blank").attr("rel", "external");
    });
  </script>
</head>
<body>
  <div class="popupContent">
    <div class="title">
      <a href="${entry.link}" target="_blank">${entry.title}</a>
    </div
    <div class="entryInfo">
      ${entry.publishedDate} - <a class="source" href="${entry.source.link}" target="_blank">${entry.source.title}</a>
    </div>
    <div class="navLinks">
      <#if previous??>
      <a class="itemNav previousItem" href="${Context.modulePath}/item?i=${previous}">${Context.getMessage('label.rss.feed.item.previous')}</a>
      <#else>
      <span class="disableItemNav previousItem">${Context.getMessage('label.rss.feed.item.previous')}</span>
      </#if>
      <#if next??>
      <a class="itemNav nextItem" href="${Context.modulePath}/item?i=${next}">${Context.getMessage('label.rss.feed.item.next')}</a>
      <#else>
      <span class="disableItemNav nextItem">${Context.getMessage('label.rss.feed.item.next')}</span>
      </#if>
      <div style="clear:both;"></div>
    </div>
    <div class="itemContent">
      <#if entry.contents?size &gt; 0>
      <#list entry.contents as content>
      ${content.value}
      </#list>
      <#else>
      ${entry.description.value}
      </#if>
    </div>
  </div>
</body>
</html>
