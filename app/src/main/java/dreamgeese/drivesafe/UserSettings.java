package dreamgeese.drivesafe;

import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserSettings {
    public static String telephoneNumber=null;
    public static String playMusic="CLOCKWISE_ROTATION";
    public static String pauseMusic="COUNTERCLOCKWISE_ROTATION";
    public static String callNumber="TACTILE1_UP";
    public static String acceptCall="TOUCH2_UP";
    public static String rejectCall="TACTILE0_UP";
    public static String raiseVolume="SWIPE_UP";
    public static String lowerVolume="SWIPE_DOWN";
    public static String nextSong="SWIPE_RIGHT";
    public static String previousSong="SWIPE_LEFT";

    public UserSettings(Database database){
        // Let's find the documents that have conflicts so we can resolve them:
        Query query = database.createAllDocumentsQuery();

        try{
            QueryEnumerator result = query.run();

            if(result.getCount()==0){ //if the database is empty (first launch of app)
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("playMusic", "CLOCKWISE_ROTATION");
                properties.put("pauseMusic", "COUNTERCLOCKWISE_ROTATION");
                properties.put("callNumber", "TACTILE1_UP");
                properties.put("acceptCall", "TOUCH2_UP");
                properties.put("rejectCall", "TACTILE0_UP");
                properties.put("raiseVolume", "SWIPE_UP");
                properties.put("lowerVolume", "SWIPE_DOWN");
                properties.put("nextSong", "SWIPE_RIGHT");
                properties.put("previousSong", "SWIPE_LEFT");

                Document document = database.createDocument();
                document.putProperties(properties);
                Log.d("dvdsvds",document.getProperties().toString());
            }
            else{
                for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Map<String, Object> document=row.getDocumentProperties();
                    Log.e("QUERY",document.toString());
                }
            }
        }catch(Exception e){
            Log.e("QUERY","Could not query the database. Using the default values");
        }

    }
}
