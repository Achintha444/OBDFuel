package com.mtdaps.obdfuel.activities.home.util;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Used to handle the database that will save the OBD data
 */
public class DatabaseHandler {

    private static final String TAG = "databaseHandler";
    private static Database database;
    private static DatabaseHandler defaultDatabaseHandler;
    private final String mSyncGatewayUrl = "ws://mtdap.projects.uom.lk:4984/obdfuel/";
    private URI mSyncGatewayUri;
    private ReplicatorConfiguration replicatorConfiguration;


    private DatabaseHandler(Context context) {
        try {
            initCouchbaseLite(context);
            final DatabaseConfiguration config = new DatabaseConfiguration();
            config.setDirectory(context.getFilesDir().getAbsolutePath());
            database = new Database("obdfuel", config);

            setURI();
            setReplicatorConfiguration();

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseHandler getDefaultDatabaseHandler(Context context) {
        if (defaultDatabaseHandler == null) {
            synchronized (DatabaseHandler.class) {
                if (defaultDatabaseHandler == null) {
                    defaultDatabaseHandler = new DatabaseHandler(context);
                }
            }
        }

        return defaultDatabaseHandler;
    }

    /**
     * Save Obddata in the database
     *
     * @param obdData
     */
    public static void saveObdData(HashMap<String, Object> obdData) {
        MutableDocument document = new MutableDocument(obdData);
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            Log.println(Log.ERROR, TAG, e.toString());
        }
    }

    private void initCouchbaseLite(Context context) {
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

    /**
     * This is the method that used to send the data through the sync gateway to the database
     */
    public void startReplication() {
        final Replicator thisReplicator = new Replicator(this.replicatorConfiguration);
        thisReplicator.start(false);
        Log.println(Log.INFO, TAG, "Data sent to the database");
    }
}
