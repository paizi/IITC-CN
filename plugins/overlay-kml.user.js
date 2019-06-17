// ==UserScript==
// @id             overlay-kml@danielatkins
// @name           IITC plugin: Overlay KML
// @category       Layer
// @version        0.2.0.@@DATETIMEVERSION@@
// @description    [@@BUILDNAME@@-@@BUILDDATE@@] Allow users to overlay their own KML / GPX files on top of IITC.
@@METAINFO@@
// ==/UserScript==

@@PLUGINSTART@@

// PLUGIN START ////////////////////////////////////////////////////////

// use own namespace for plugin
window.plugin.overlayKML = function() {};

window.plugin.overlayKML.loadExternals = function() {
  try { console.log('Loading leaflet.filelayer JS now'); } catch(e) {}
  @@INCLUDERAW:external/leaflet.filelayer.js@@
  try { console.log('done loading leaflet.filelayer JS'); } catch(e) {}

  if (window.requestFile !== undefined) {
    try { console.log('Loading android webview extensions for leaflet.filelayer JS now'); } catch(e) {}
    var FileLoaderMixin = {
      parse: function (fileContent, fileName) {
        // Check file extension
        var ext = fileName.split('.').pop(),
          parser = this._parsers[ext];
        if (!parser) {
          window.alert("Unsupported file type " + file.type + '(' + ext + ')');
          return;
        }
        this.fire('data:loading', {filename: fileName, format: ext});
        var layer = parser.call(this, fileContent, ext);
        this.fire('data:loaded', {layer: layer, filename: fileName, format: ext});
      }
    };
    FileLoader.include(FileLoaderMixin);

    var FileLayerLoadMixin = {
      getLoader: function () {
        return this.loader;
      },
      _initContainer: function () {
        // Create a button, and bind click on hidden file input
        var zoomName = 'leaflet-control-filelayer leaflet-control-zoom',
          barName = 'leaflet-bar',
          partName = barName + '-part',
          container = L.DomUtil.create('div', zoomName + ' ' + barName);
        var link = L.DomUtil.create('a', zoomName + '-in ' + partName, container);
        link.innerHTML = L.Control.FileLayerLoad.LABEL;
        link.href = '#';
        link.title = L.Control.FileLayerLoad.TITLE;

        var stop = L.DomEvent.stopPropagation;
        L.DomEvent
          .on(link, 'click', stop)
          .on(link, 'mousedown', stop)
          .on(link, 'dblclick', stop)
          .on(link, 'click', L.DomEvent.preventDefault)
          .on(link, 'click', function (e) {
            window.requestFile(function(filename, content) {
              _fileLayerLoad.getLoader().parse(content, filename);
            });
            e.preventDefault();
          });
        return container;
      }
    };
    L.Control.FileLayerLoad.include(FileLayerLoadMixin);

    try { console.log('done loading android webview extensions for leaflet.filelayer JS'); } catch(e) {}
  }

  try { console.log('Loading KML JS now'); } catch(e) {}
  @@INCLUDERAW:external/KML.js@@
  try { console.log('done loading KML JS'); } catch(e) {}

  try { console.log('Loading togeojson JS now'); } catch(e) {}
  @@INCLUDERAW:external/togeojson.js@@
  try { console.log('done loading togeojson JS'); } catch(e) {}

  window.plugin.overlayKML.load();
}

var _fileLayerLoad = null;

window.plugin.overlayKML.load = function() {
  // Provide popup window allow user to select KML to overlay
	
  var KMLIcon = L.icon(L.extend({},L.Icon.Default.prototype.options,{
    iconSize:     [16, 24],
    iconAnchor:   [8, 24],
    popupAnchor:  [-3, 16],
    shadowSize:   [24, 24]
  }));
  
  L.Control.FileLayerLoad.LABEL = '<img src="@@INCLUDEIMAGE:images/open-folder-icon_sml.png@@" alt="Open" />';
  _fileLayerLoad = L.Control.fileLayerLoad({
    fitBounds: true,
    layerOptions: {
      pointToLayer: function (data, latlng) {
      return L.marker(latlng, {icon: KMLIcon});
    }},
  });
  _fileLayerLoad.addTo(map);
}

var setup =  function() {
  window.plugin.overlayKML.loadExternals();
}

// PLUGIN END //////////////////////////////////////////////////////////

@@PLUGINEND@@

