package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity.Companion.TAG
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment() ,OnMapReadyCallback{

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var selectedLocation = LatLng(30.12674,31.29315)
    private var selectedLocationDescription:String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.saveReminderLocationBtn.setOnClickListener {
        _viewModel.onLocationSelected(selectedLocation,selectedLocationDescription) }


        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        setMapStyle(map)
        setPoiClick(map)
        setOnLongClick(map)
        enableMyLocation()
    }

    private fun setOnLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { LatLng->
            val snippet = String.format(Locale.getDefault(),"Lat:%1$.5f , Long:%2$.5f",LatLng.latitude,LatLng.longitude)
            map.addMarker(MarkerOptions().position(LatLng).title(getString(R.string.dropped_pin)).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            selectedLocation = LatLng
            selectedLocationDescription = "Custom Location"
        }
    }

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        when{
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false)->{
                enableMyLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false)->{
                enableMyLocation()
            }
            else->{
                Log.i("Permission : ","Denied")
                Toast.makeText(context,"Location permission was not granted.",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun enableMyLocation() {
        if (ifPermissionGranted()){
            map.isMyLocationEnabled = true
            Toast.makeText(context,"Location permission is Granted.",Toast.LENGTH_LONG).show()
        }else{
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun ifPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success  = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.maps_style))
            if (!success){
                Log.e(TAG,"Style parsing failed.")
            }
        }catch (e: Resources.NotFoundException){
            Log.e(TAG,"Style not found. Error: ",e)
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi->
            val poiMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            poiMarker?.showInfoWindow()
            selectedLocation = poi.latLng
            selectedLocationDescription = poiMarker?.title
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}
