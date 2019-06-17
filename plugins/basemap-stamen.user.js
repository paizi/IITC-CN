// ==UserScript==
// @id             iitc-plugin-basemap-stamen@jonatkins
// @name           IITC plugin: Stamen.com map layers
// @category       Map Tiles
// @version        0.2.1.@@DATETIMEVERSION@@
// @description    [@@BUILDNAME@@-@@BUILDDATE@@] Add the 'Toner' and 'Watercolor' map layers from maps.stamen.com.
@@METAINFO@@
// ==/UserScript==

@@PLUGINSTART@@

// PLUGIN START ////////////////////////////////////////////////////////


// see API here http://maps.stamen.com/
// https://stamen-maps.a.ssl.fastly.net/js/tile.stamen.js (overcomplicated)

function setup () {
  var baseUrl = 'https://stamen-tiles-{s}.a.ssl.fastly.net/{layer}/{z}/{x}/{y}.{type}';
  var L_StamenTileLayer = L.TileLayer.extend({
    options: {
      subdomains: 'abcd',
      type: 'png',
      minZoom: 0,
      maxZoom: 21,
      attribution: [
        'Map tiles by <a href="http://stamen.com/">Stamen Design</a>, ',
        'under <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a>. ',
        'Data by <a href="http://openstreetmap.org/">OpenStreetMap</a>, ',
        'under <a href="http://creativecommons.org/licenses/by-sa/3.0">CC BY SA</a>.'
      ].join('')
    },
    initialize: function (name, options) {
      options.layer = name.replace(' ','-').toLowerCase();
      L.TileLayer.prototype.initialize.call(this, baseUrl, options);
    }
  });

  function addLayer (name,options) {
    layerChooser.addBaseLayer(new L_StamenTileLayer(name,options),'Stamen ' + name);
  }

  var options = { minZoom: 0, maxNativeZoom: 20 };
  addLayer('Toner',options);
  addLayer('Toner Background',options);
  addLayer('Toner Lite',options);
  // transparent layers. could be useful over satellite imagery or similar
  // addLayer('Toner Hybrid',options);
  // addLayer('Toner Labels',options);
  // addLayer('Toner Lines',options);

  options = { minZoom: 1, maxNativeZoom: 13 }; // Should support up to 18, but too many 404 on zoom > 13
  // addLayer('Terrain',options);
  // addLayer('Terrain Labels',options);
  // addLayer('Terrain Lines',options);
  // addLayer('Terrain Background',options);

  options = {
    minZoom: 1,
    maxZoom: 21,
    maxNativeZoom: 18,
    type: 'jpg',
    attribution: [
      'Map tiles by <a href="http://stamen.com/">Stamen Design</a>, ',
      'under <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a>. ',
      'Data by <a href="http://openstreetmap.org">OpenStreetMap</a>, ',
      'under <a href="http://www.openstreetmap.org/copyright">ODbL</a>.'
    ].join('')
  };
  addLayer('Watercolor',options);
}

// PLUGIN END //////////////////////////////////////////////////////////

@@PLUGINEND@@
