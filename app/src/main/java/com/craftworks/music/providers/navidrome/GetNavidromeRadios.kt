package com.craftworks.music.providers.navidrome

import android.net.Uri
import com.craftworks.music.R
import com.craftworks.music.data.Radio
import com.craftworks.music.data.radioList
import com.gitlab.mvysny.konsumexml.konsumeXml

suspend fun getNavidromeRadios(){
    sendNavidromeGETRequest("getInternetRadioStations.view?")
}

suspend fun deleteNavidromeRadio(id:String){
//    if (navidromeServersList[selectedNavidromeServerIndex.intValue].username == "" ||
//        navidromeServersList[selectedNavidromeServerIndex.intValue].url == "" ||
//        navidromeStatus.value != "ok") return
//
//    val thread = Thread {
//        try {
//            val navidromeUrl =
//                URL("${navidromeServersList[selectedNavidromeServerIndex.intValue].url}/rest/deleteInternetRadioStation.view?id=$id&u=${navidromeServersList[selectedNavidromeServerIndex.intValue].username}&p=${navidromeServersList[selectedNavidromeServerIndex.intValue].password}&v=1.12.0&c=Chora")
//
//            with(navidromeUrl.openConnection() as HttpURLConnection) {
//                requestMethod = "GET"  // optional default is GET
//                Log.d("GET", "\nSent 'GET' request to URL : $url; Response Code : $responseCode")
//            }
//        } catch (e: Exception) {
//            Log.d("Exception", e.toString())
//        }
//    }
//    thread.start()

    sendNavidromeGETRequest("deleteInternetRadioStation.view?id=$id")
}

suspend fun modifyNavidromeRadio(id:String, name:String, url:String, homePage:String){
//    if (navidromeServersList[selectedNavidromeServerIndex.intValue].username == "" ||
//        navidromeServersList[selectedNavidromeServerIndex.intValue].url == "" ||
//        navidromeStatus.value != "ok") return
//
//    val thread = Thread {
//        try {
//            val navidromeUrl =
//                URL("${navidromeServersList[selectedNavidromeServerIndex.intValue].url}/rest/updateInternetRadioStation.view?name=$name&streamUrl=$url&homepageUrl=$homePage&id=$id&u=${navidromeServersList[selectedNavidromeServerIndex.intValue].username}&p=${navidromeServersList[selectedNavidromeServerIndex.intValue].password}&v=1.12.0&c=Chora")
//
//            with(navidromeUrl.openConnection() as HttpURLConnection) {
//                requestMethod = "GET"  // optional default is GET
//                Log.d(
//                    "GET",
//                    "\nSent 'GET' request to URL : $navidromeUrl; Response Code : $responseCode"
//                )
//            }
//
//            getNavidromeRadios()
//        } catch (e: Exception) {
//            Log.d("Exception", e.toString())
//        }
//    }
//    thread.start()

    sendNavidromeGETRequest("updateInternetRadioStation.view?name=$name&streamUrl=$url&homepageUrl=$homePage&id=$id")
}

suspend fun createNavidromeRadio(name:String, url:String, homePage:String){
//    if (navidromeServersList[selectedNavidromeServerIndex.intValue].username == "" ||
//        navidromeServersList[selectedNavidromeServerIndex.intValue].url == "" ||
//        navidromeStatus.value != "ok") return
//
//    val thread = Thread {
//        try {
//            Log.d("useNavidromeServer", "URL: $url")
//
//            val navidromeUrl =
//                URL("${navidromeServersList[selectedNavidromeServerIndex.intValue].url}/rest/createInternetRadioStation.view?name=$name&streamUrl=$url&homepageUrl=$homePage&u=${navidromeServersList[selectedNavidromeServerIndex.intValue].username}&p=${navidromeServersList[selectedNavidromeServerIndex.intValue].password}&v=1.12.0&c=Chora")
//
//            with(navidromeUrl.openConnection() as HttpURLConnection) {
//                requestMethod = "GET"  // optional default is GET
//                Log.d("GET", "\nSent 'GET' request to URL : $url; Response Code : $responseCode")
//            }
//        } catch (e: Exception) {
//            Log.d("Exception", e.toString())
//        }
//    }
//    thread.start()

    sendNavidromeGETRequest("createInternetRadioStation.view?name=$name&streamUrl=$url&homepageUrl=$homePage")
}

fun parseNavidromeRadioXML(response: String){

    // Avoid crashing by removing some useless tags.
    val newResponse = response
        .replace("xmlns=\"http://subsonic.org/restapi\" ", "")

    newResponse.konsumeXml().apply {
        child("subsonic-response"){
            child("internetRadioStations"){
                children("internetRadioStation"){
                    val radioName = attributes.getValue("name")
                    val radioUrl = attributes.getValue("streamUrl")
                    val radioHomepage = attributes.getValueOrNull("homePageUrl") ?: ""
                    val radioID = attributes.getValue("id")

                    val radio = Radio(
                        name = radioName,
                        imageUrl = Uri.parse("android.resource://com.craftworks.music/" + R.drawable.radioplaceholder),
                        homepageUrl = radioHomepage,
                        media = Uri.parse(radioUrl),
                        navidromeID = radioID
                    )

                    synchronized(radioList){
                        if (radioList.none { it.media == radio.media }) {
                            radioList.add(radio)
                        }
                    }

                    skipContents()
                    finish()
                }
            }
        }
    }
}