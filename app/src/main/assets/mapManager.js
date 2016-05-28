require([

],

function() {

// BASICS
// set osm recognition
var osmAttr = '&copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>';

// init map element
var map = L.map('map').setView([51.505, -0.09], 14);

// Add base layer
var baseLayer = L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
    attribution: osmAttr
}).addTo(map);


var marker = L.marker([51.505, -0.09]).addTo(map);

marker.on('click', onMarkerClick);
map.on('click', onMapClick);


// Call Toast in App
function onMarkerClick() {
    Android.Toast();
}

// GeoJSON
//
//

// line polygon
var myLines = [{
    "type": "LineString",
    "coordinates": [[7.223109, 51.478667], [7.222221, 51.479315], [7.224260, 51.480350],
    [7.225114, 51.482591], [7.221667, 51.483470], [7.227736, 51.484762], [7.234344, 51.488364],
    [7.237243, 51.489338], [7.236540, 51.489900]]
}];

// style for the GeoJSON objects
var myStyle = {
    "color": "#ee0000",
    "weight": 8,
    "opacity": 0.5,
    "transparent": true
};

L.geoJson(myLines, {
    style: myStyle
}).addTo(map);

var geojsonFeature = {
    "type": "Feature",
    "properties": {
        "name": "Rewirpowerstadion",
        "amenity": "Football Stadium",
        "popupContent": "This is the home ground for the VfL Bochum !"
    },
    "geometry": {
        "type": "Point",
        "coordinates": [7.236540, 51.489900]
    }
};

L.geoJson(geojsonFeature, {
    onEachFeature: onEachFeature
}).addTo(map);


function onEachFeature(feature, layer) {
    // does this feature have a property named popupContent?
    if (feature.properties && feature.properties.popupContent) {
        layer.bindPopup(feature.properties.popupContent);
    }
}

});
