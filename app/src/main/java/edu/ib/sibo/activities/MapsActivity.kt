package edu.ib.sibo.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import edu.ib.sibo.R
import edu.ib.sibo.models.Specialist


class MapsActivity : BaseActivity(), GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var map: GoogleMap? = null
    private var mList: ArrayList<Specialist> = ArrayList()
    private var cameraPosition: CameraPosition? = null
    private lateinit var placesClient: PlacesClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null

    companion object {
        private const val DEFAULT_ZOOM = 14
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setOnInfoWindowClickListener(this)
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
        setResult(RESULT_OK, Intent())
        mList = intent.getParcelableArrayListExtra<Specialist>("specialistList")!!
        if (mList != null) {
            populateSpecialistToMap(mList)
        } else {
            Toast.makeText(this, "There is no specialist", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onInfoWindowClick(marker: Marker) {
        var name = marker.title?.split(" ")!![0]
        var surname: String = ""
        var address: String = ""
        val index: Int = marker.title?.split(" ")!![1].indexOf(",")
        if (index >= 0) {
            surname = marker.title?.split(" ")!![1].substring(0, index)
        }

        val indexA: Int = marker.snippet!!.lastIndexOf(",")
        if (indexA >= 0) {
            address = marker.snippet!!.substring(0, indexA)
        }
        var specialist = findSpecialistInList(name, surname, address)
        var intent = Intent(this, SpecialistDetailsActivity::class.java)
        intent.putExtra("model", specialist)
        startActivity(intent)
        finish()
    }

    private fun findSpecialistInList(name: String, surname: String, address: String): Specialist {
        var spec: Specialist = Specialist()
        for (i in mList) {
            if (name == i.name && surname == i.surname && address == i.address) {
                spec = i
            }
        }
        return spec
    }


    @SuppressLint("MissingPermission")
    private fun populateSpecialistToMap(list: ArrayList<Specialist>) {
        var latlng: LatLng

        for (i in list) {
            latlng = getLatLng(i.address)!!
            var value = 0
            for (i in i.rating) {
                value += i.rate
            }

            var valueD = (value.toDouble() / i.rating.size)

            if (i.type == "Dietetyk") {
                map?.addMarker(
                    MarkerOptions()
                        .position(latlng)
                        .title("${i.name} ${i.surname}, ${i.type}")
                        .snippet("${i.address}, Ocena: ${valueD}")
                        .icon(BitmapDescriptorFactory.defaultMarker(310F))
                )
            } else if (i.type == "Lekarz") {
                map?.addMarker(
                    MarkerOptions()
                        .position(latlng)
                        .title("${i.name} ${i.surname}, ${i.type}")
                        .snippet("${i.address}, Ocena: ${valueD}")
                        .icon(BitmapDescriptorFactory.defaultMarker(285F))
                )
            }
        }
    }

    private fun getLatLng(name: String): LatLng {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocationName(name, 1)

        return LatLng(list[0].latitude, list[0].longitude)
    }


    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {

        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        lastKnownLocation = task.result

                        if (lastKnownLocation != null) {

                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

}

