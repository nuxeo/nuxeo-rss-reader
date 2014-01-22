<!DOCTYPE html>
<html>
<head>
<title>Sample: Nuxeo RSS Reader</title>
  <link rel="stylesheet" href="${contextPath}/css/opensocial/light-container-gadgets.css">

  <link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery.fancybox.css" />

  <script type="text/javascript" src="${skinPath}/script/jquery/jquery.js"></script>
  <script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.fancybox.pack.js"></script>
  <script type="text/javascript" src="${contextPath}/opensocial/gadgets/js/rpc.js?c=1"></script>
  <script type="text/javascript" src="${contextPath}/js/?scripts=opensocial/cookies.js|opensocial/util.js|opensocial/gadgets.js|opensocial/cookiebaseduserprefstore.js|opensocial/jquery.opensocial.gadget.js"></script>

  <script type="text/javascript">
    $(document).ready(function() {
      $('.gadgets-gadget-chrome').openSocialGadget({
        baseURL: '${contextPath}' + '/',
        language: '${Context.locale.language?js_string?html}',
        gadgetDefs: [ {specUrl : '${Runtime.getProperty('nuxeo.loopback.url')}/site/gadgets/rssreader/rssreader.xml',
                    title : 'Nuxeo RSS Reader',
                    width : '100%'}]
      });
    });

    var nuxeo = {
      rssReader: {
        addFancyBoxTo: function(a, onClosedCallback) {
          var config = {
            'width'             : '75%',
            'height'            : '75%',
            'autoScale'         : true,
            'transitionIn'      : 'none',
            'transitionOut'     : 'none',
            'type'              : 'iframe',
            'enableEscapeButton': true,
            'centerOnScroll'    : true
          };
          if ( typeof onClosedCallback != 'undefined' ) {
            config['onClosed'] = onClosedCallback;
          }
          jQuery(a).fancybox(config);
        }
      }
    };

  </script>
</head>
<body>
    <h2>Nuxeo RSS Reader</h2>
    <div class="gadgets-gadget-chrome"></div>
</body>
</html>
