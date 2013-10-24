var nuxeo = (function(nuxeo) {
  nuxeo.rssReader = {
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
  };

  return nuxeo;
})(nuxeo || {});
