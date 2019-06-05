package com.shjeong.logintest

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.n.api.internal.NativeMapLocationManager.setShowCurrentLocationMarker


class MainActivity : AppCompatActivity() {

    val MYCOLOR_DEEP_LINK =  "/test/success"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionCheck : Int = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this@MainActivity,"권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            locationCheck()
            return
        }

        val i = intent
        val id = i.getStringExtra("id")

        if(i.data == null) {
            textView2.text = "ID : $id"
            Log.d("main!@#", "ID : $id")
        } else {
            textView2.text = "ID : ${i.data.path}"
            Log.d("main!@#", "ID : ${i.data.path}")
        }

        if (i.data != null && MYCOLOR_DEEP_LINK.equals(i.data.path)) {
            textView2.text = "ID : ${i.data.path.split("/")[2]}"
            Log.d("main!@#", "ID : ${i.data.path.split("/")[2]}")
        }


        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            textView.text = "딥 링크로 여기에 오신걸 환영합니다!"
        } else {
            textView.text = ""
        }

        firebaseDynamicLinks_check()

        // 다음 맵 지도
        daumMapInit()


        val activityManager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = activityManager.getRunningTasks(1)
        val currentTopActivity = taskInfo[0].topActivity.className

        Log.d("main!@#", "className : $currentTopActivity")
        Log.d("main!@#", "package Name : ${this.packageName}")

        val intent = Intent(this@MainActivity, MyAccessibilityService::class.java)
        startService(intent)
        Log.d("main!@#", "package Name : ${this.packageName}")
    }

    private fun firebaseDynamicLinks_check() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("main!@#", "Dynamic link Success!!")
                    textView.text = "파이어베이스 DynamicLink로 들어오셨습니다!"
                }
                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e -> Log.d("main!@#", "getDynamicLink:onFailure", e) }
    }

    private fun daumMapInit() {
        val mapView = MapView(this)

        val mapViewContainer = findViewById<ViewGroup>(R.id.map_view)
        mapViewContainer.addView(mapView)

        val mapPoint = MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633)

        // 중심점 변경 + 줌 레벨 변경
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 2, true)

        val marker = MapPOIItem()
        marker.itemName = "Default Marker"
        marker.tag = 0
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker)



        mapView.setMapViewEventListener(object : MapView.MapViewEventListener {
            override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

            override fun onMapViewInitialized(p0: MapView?) {
                Log.d("main!@#", "onMapViewInitialized p0:$p0")
            }

            override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDragStarted p0:$p0, p1:$p1")
            }

            override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

            override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

            override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

            override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

            override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

            override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
                Log.d("main!@#", "onMapViewDoubleTapped p0:$p0, p1:$p1")
            }

        })

        Log.d("main!@#", mapView.mapType.toString())

        mapView.setCalloutBalloonAdapter(CustomCalloutBalloonAdapter())

        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        Log.d("main!@#", MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading.toString())
        Log.d("main!@#", mapView.currentLocationTrackingMode.toString())
        setShowCurrentLocationMarker(true)

//        val reverseGeoCoder =
//            MapReverseGeoCoder("LOCAL_API_KEY", mapPoint, reverseGeoCodingResultListener, contextActivity)
//        reverseGeoCoder.startFindingAddress()

    }


    internal inner class CustomCalloutBalloonAdapter : CalloutBalloonAdapter {
        private val mCalloutBalloon: View? = null

        override fun getCalloutBalloon(poiItem: MapPOIItem): View? {
            Log.d("main!@#","getCalloutBalloon $poiItem")
            return null
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem): View? {
            Log.d("main!@#", "getPressedCalloutBalloon $poiItem")
            return null
        }
    }
 private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private fun locationCheck() {

        if(ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){

            // Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)){

                Log.d("main!@#", "1")
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            }else{
                Log.d("main!@#", "2")
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    Toast.makeText(this@MainActivity, "권한을 수락하셨습니다.", Toast.LENGTH_SHORT).show()

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@MainActivity, "권한을 수락하세요.", Toast.LENGTH_SHORT).show()
                    locationCheck()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        }
    }


}
