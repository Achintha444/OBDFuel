package com.mtdaps.obdfuel.activities.home.util;

import android.content.Context;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by uwin5 on 04/01/18.
 */

public class DatabaseHandler {

    private static Database database;
    private final String mSyncGatewayUrl = "http://mtdap.projects.uom.lk:4984/obdfuel/";
    private URI mSyncGatewayUri;
    private ReplicatorConfiguration replicatorConfiguration;
    private DatabaseHandler defaultDatabaseHandler;


    private DatabaseHandler(Context context) {
        try {
//            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
//            database = getManager().getDatabase("mtdaps");

            final DatabaseConfiguration config = new DatabaseConfiguration();
            config.setDirectory(String.format("%s/%s", context.getFilesDir()));
            database = new Database("obdfuel", config);

            setURI();
            setReplicatorConfiguration();

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public DatabaseHandler getDefaultDatabaseHandler(Context context){
        if(defaultDatabaseHandler==null){
            synchronized (DatabaseHandler.class){
                if(defaultDatabaseHandler==null){
                    defaultDatabaseHandler = new DatabaseHandler(context);
                }
            }
        }

        return this.defaultDatabaseHandler;
    }

/*
    public static void saveToDatabase() {
        // The properties that will be saved on the document
        Map<String, Object> properties = new HashMap<String, Object>();

        Log.d("DATA====", SensorData.getMacceX());
        properties.put("journeyID", SensorData.getJourneyId());
        properties.put("imei", SensorData.getDeviceId());
        properties.put("model", SensorData.getModel());
        properties.put("lat", SensorData.getMlat());
        properties.put("lon", SensorData.getMlon());
        properties.put("obdSpeed", SensorDataProcessor.vehicleSpeed());
        properties.put("gpsSpeed", MobileSensors.getGpsSpeed());
        properties.put("obdRpm", SensorData.getMobdRpm());
        properties.put("acceX", SensorDataProcessor.getReorientedAx());
        properties.put("acceY", SensorDataProcessor.getReorientedAy());
        properties.put("acceZ", SensorDataProcessor.getReorientedAz());
        properties.put("acceX_raw", SensorData.getMacceX());
        properties.put("acceY_raw", SensorData.getMacceY());
        properties.put("acceZ_raw", SensorData.getMacceZ());
        properties.put("magnetX", SensorData.getMagnetX());
        properties.put("magnetY", SensorData.getMagnetY());
        properties.put("magnetZ", SensorData.getMagnetZ());
        properties.put("gyroX", SensorData.getGyroX());
        properties.put("gyroY", SensorData.getGyroY());
        properties.put("gyroZ", SensorData.getGyroZ());
        properties.put("time", System.currentTimeMillis());
        properties.put("dataType", "data_item");

        // Create a new document
        Document document = database.createDocument();
        // Save the document to the database
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static void saveJourneyName() {
        // The properties that will be saved on the document
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put("journeyID", SensorData.getJourneyId());
        properties.put("journeyName", "latest");
        properties.put("startLat", SensorData.getMlat());
        properties.put("startLon", SensorData.getMlon());
        properties.put("dataType", "trip_names");
        // Create a new document
        Document document = database.createDocument();
        // Save the document to the database
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
*/

    public void initCouchbaseLite(Context context) {
        CouchbaseLite.init(context);
    }

    private void setURI() {
        try {
            this.mSyncGatewayUri = new URI(this.mSyncGatewayUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setReplicatorConfiguration() {
        if (this.replicatorConfiguration == null) {
            // initialize the replicator configuration
            this.replicatorConfiguration = new ReplicatorConfiguration(
                    database,
                    new URLEndpoint(this.mSyncGatewayUri));

            // Set replicator type
            replicatorConfiguration.setReplicatorType(
                    ReplicatorConfiguration.ReplicatorType.PUSH);

            // Configure Sync Mode
            replicatorConfiguration.setContinuous(false);

        }
    }

    // Replication
    public void startReplication() {
        /*// initialize the replicator configuration
        final ReplicatorConfiguration thisConfig
                = new ReplicatorConfiguration(
                thisDB,
                URLEndpoint(URI("wss://listener.com:8954")));

        // Set replicator type
        thisConfig.setReplicatorType(
                ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);

        // Configure Sync Mode
        thisConfig.setContinuous(false); // default value

        // Configure the credentials the
        // client will provide if prompted
        final BasicAuthenticator thisAuth
                = new BasicAuthenticator(
                "Our Username",
                "Our PasswordValue"))

        thisConfig.setAuthenticator(thisAuth)

        *//* Optionally set custom conflict resolver call back *//*
        thisConfig.setConflictResolver( *//* define resolver function *//*);

// Create replicator
// Consider holding a reference somewhere
// to prevent the Replicator from being GCed*/
        final Replicator thisReplicator = new Replicator(this.replicatorConfiguration);

// Start replicator
        thisReplicator.start(false);
    }
}
