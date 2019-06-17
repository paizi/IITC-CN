// ==UserScript==
// @id             iitc-plugin-highlight-portals-missing-resonators@vita10gy
// @name           IITC plugin: Highlight portals missing resonators
// @category       Highlighter
// @version        0.1.2.@@DATETIMEVERSION@@
// @description    [@@BUILDNAME@@-@@BUILDDATE@@] Use the portal fill color to denote if the portal is missing resonators. 
@@METAINFO@@
// ==/UserScript==

@@PLUGINSTART@@

// PLUGIN START ////////////////////////////////////////////////////////

// use own namespace for plugin
window.plugin.portalsMissingResonators = function() {};

window.plugin.portalsMissingResonators.highlight = function(data) {

  if(data.portal.options.team != TEAM_NONE) {
    var res_count = data.portal.options.data.resCount;

    if(res_count !== undefined && res_count < 8) {
      var fill_opacity = ((8-res_count)/8)*.85 + .15;
      var color = 'red';
      var params = {fillColor: color, fillOpacity: fill_opacity};

      // Hole per missing resonator
      var dash = new Array((8 - res_count) + 1).join("1,4,") + "100,0"
      params.dashArray = dash;

      data.portal.setStyle(params);
    } 
  }
}

var setup =  function() {
  window.addPortalHighlighter('Portals Missing Resonators', window.plugin.portalsMissingResonators.highlight);
}

// PLUGIN END //////////////////////////////////////////////////////////

@@PLUGINEND@@
