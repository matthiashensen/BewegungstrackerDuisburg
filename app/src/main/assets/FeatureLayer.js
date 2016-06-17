var map = L.map('map').setView([51.4825,7.216944], 14);

// Add base layer
var baseLayer = L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
    attribution: osmAttr
}).addTo(map);

// Add feature Layer
var service = L.esri.featureLayerService({
  url: 'http://services6.arcgis.com/4xcXhPaJrA6efO9w/arcgis/rest/services/TrackedPoints/FeatureServer/0'
}).addTo(map);

/*
var trackedPoints = L.esri.featureLayer({
url: 'https://services6.arcgis.com/4xcXhPaJrA6efO9w/arcgis/rest/services/TrackedPoints/FeatureServer'
}).addTo(map);
*/

var latitude = Android.getLatitude();
var longitude = Android.getLongitude();

// Save new Features
var addFeature = {
    type: 'Feature',
    geometry: {
        type: 'Point',
        coordinates: [latitude, longitude]
    },
    properties: {
        id: '',
        UserID: '',
        Date: '',
        Zeit: ''

    }
};

service.addFeature(addFeature, function(error, response){
    if(error){
      console.log('error creating feature' + error.message);
    } else {
      console.log('Successfully created feature with id ' + response.objectId);
    }
});

//update features
var updateFeature = {
    type: 'Feature',
    id: 2,
    geometry: {
        type: 'Point',
        coordinates: [-122, 45]
    },
    properties: {
        name: 'Hi I\'m Feature 2'
    }
};

service.updateFeature(updateFeature, function(error, response){
    if(error){
      console.log('error updating feature' + error.message);
    } else {
      console.log('Successfully updated feature ' + feature.id);
    }
});

//delete features
service.deleteFeature(2, function(error, response){
    if(error){
      console.log('error deleting feature' + error.message);
    } else {
      console.log('Successfully deleted feature ' + response.objectId);
    }
});

//querying features
service.query().where("name='Hello World'").run(function(error, featureCollection, response){
    console.log(featureCollection.features[0].properties.name);
});