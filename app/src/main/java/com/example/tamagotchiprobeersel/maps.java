package com.example.tamagotchiprobeersel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nirvana van Hees
 *  *
 * @version 1.0.0
 *
 * the gps and the map within the app
 */

public class maps extends Activity implements LocationListener {
    MapView map = null;

    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private Context context;
    private static final String TAG = "maps";
    private static final int LOCATION_REQUEST = 101;

    private ArrayList<GeoPoint> parks;
    private GeoPoint lastPark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration
        context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //Check Permissions ( this is already done in the first activity but is also here to make sure that code runs smoothly
        //else trouble will come with adding my location)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }

        //inflate and create the map
        setContentView(R.layout.activity_maps);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // multi-touch zoom
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        //default view point: eindhoven
        IMapController mapController = map.getController();
        mapController.setZoom(17.5);
        GeoPoint Eindhoven = new GeoPoint(51.4415, 5.4721);
        mapController.setCenter(Eindhoven);

        //add my location
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        final LocationManager locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final String provider = locMgr.getBestProvider(criteria, true);
        locMgr.requestLocationUpdates(provider, 1000, 100, this);

        Location loc = locMgr.getLastKnownLocation(provider);
        Log.d(TAG, "onCreate: " + loc.getLatitude() + ", " + loc.getLongitude());

        //my location overlay
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context),map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.setDrawAccuracyEnabled(true);

        map.getOverlays().add(this.mLocationOverlay);


        //locations of the parks in eindhoven
        parks = new ArrayList<>();
        parks.add(new GeoPoint(51.4324d,5.4783d)); //0.Anne-Frankplantsoen
        parks.add(new GeoPoint(51.4264d,5.4749d)); //1.Dommelplantsoen
        parks.add(new GeoPoint(51.4268d,5.4821d)); //2.Stadswandelpark
        parks.add(new GeoPoint(51.4300d,5.4999d)); //3.Glorieuxpark
        parks.add(new GeoPoint(51.4231d,5.4799d)); //4.Ton-Smitspark
        parks.add(new GeoPoint(51.4305d,5.4786d)); //5.Lex-en-Edo-Hornemannplantsoen

        for (GeoPoint point : parks) {
            Marker pMarker = new Marker(map);
            pMarker.setPosition(point);
            pMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            pMarker.setIcon(getDrawable(R.drawable.cave1)); //needs boundaries!!
            pMarker.setTitle("find food or energy!");
            map.getOverlays().add(pMarker);
        }

        // compass overlay
        this.mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        this.mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);
    } // end onCreate

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
   }

    @Override
    public void onLocationChanged(Location location) {
        for (GeoPoint point : parks) {
            // Distance in meters
            if (point != lastPark && point.distanceToAsDouble(new GeoPoint(location)) < 3) {
                lastPark = point;

                MainActivity parent = (MainActivity)getParent();
                Random random = new Random();

                if (random.nextBoolean()) {
                    parent.GainFood();
                } else {
                    parent.GainCrystal();
                }

                break;
            }
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}