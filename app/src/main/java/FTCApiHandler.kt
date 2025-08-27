import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Base64
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.typeOf
import kotlinx.coroutines.*

class FTCApiHandler(apiData: FTCApiData) {
    class EventCodeAndNamePair(val eventCode: String, val eventName: String) {
        override fun toString(): String {
            return eventName
        }
    }
    val TAG = "FTCApiHandler"
    private val authorizationHeader: String = apiData.getAuthorizationHeader()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // maps from each country has an array that contains pairs of the event codes and their readable names
    var countriesToCodesAndNames: MutableMap<String, MutableList<EventCodeAndNamePair>> = mutableMapOf()

    @kotlinx.serialization.Serializable
    data class FTCApiData(val username: String, val authorizationToken: String) {
        fun getAuthorizationHeader(): String {
            return Base64.getEncoder().encodeToString((username + ":" + authorizationToken).toByteArray(Charsets.UTF_8))
        }
    }

    suspend fun initializeMap(): MutableMap<String, MutableList<EventCodeAndNamePair>> {
        return suspendCoroutine { continuation ->
            getEvents(Utils.getCurrentYear()) { result ->
                result.fold(
                    onSuccess = {jsonObjects ->
                        val seenCountries = mutableSetOf<String>()

                        Log.i(TAG, "length of json objects: ${jsonObjects.size}")

                        for (event in jsonObjects) {
                            if (event.containsKey("country") && event.containsKey("code") && event.containsKey("name")) {
                                // Fix: Extract actual string values from JSON
                                val country = event["country"]?.jsonPrimitive?.content
                                val eventCode = event["code"]?.jsonPrimitive?.content // or "code"
                                val name = event["name"]?.jsonPrimitive?.content

                                if (country != null && eventCode != null && name != null) {
                                    if (!seenCountries.contains(country)) {
                                        seenCountries.add(country) // Fix: Use add() not plus()
                                        countriesToCodesAndNames[country] = mutableListOf<EventCodeAndNamePair>()
                                    }

                                    // Fix: Actually add to the list
                                    countriesToCodesAndNames[country]!!.add(EventCodeAndNamePair(eventCode, makeEventNameReadable(name, country)))
                                }
                            }
                        }

                        continuation.resume(countriesToCodesAndNames)
                    },
                    onFailure = {error ->
                        continuation.resumeWithException(error)
                    }
                )
            }
        }
    }

    private fun getEvents(year: String, callback: (Result<Array<JsonObject>>) -> Unit) {
        val url = "https://ftc-api.firstinspires.org/v2.0/$year/events"

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Basic $authorizationHeader")
            .get()
            .build()

        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { resp ->
                        if (!resp.isSuccessful) {
                            callback(Result.failure(IOException("HTTP ${resp.code}: ${resp.message}")))
                            return
                        }

                        try {
                            val responseBody = resp.body?.string() ?: ""

                            // Parse as JsonElement first
                            val jsonElement = Json.parseToJsonElement(responseBody)

                            // Extract the "Events" array from the response
                            val eventsArray = jsonElement.jsonObject["events"]?.jsonArray

                            if (eventsArray != null) {
                                // Convert JsonArray to Array<JsonObject>
                                val jsonObjects = eventsArray.map { it.jsonObject }.toTypedArray()
                                callback(Result.success(jsonObjects))
                            } else {
                                callback(Result.failure(Exception("No 'events' array found in response")))
                            }
                        } catch (e: Exception) {
                            callback(Result.failure(e))
                        }
                    }
                }
            }
        )
    }

    fun getTeamsInEventInPage(year: String, eventCode: String, page: Int, callback: (Result<List<String>>) -> Unit) {
        val url = "https://ftc-api.firstinspires.org/v2.0/$year/teams?eventCode=$eventCode&page=$page"

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Basic $authorizationHeader")
            .get()
            .build()

        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { resp ->
                        if (!resp.isSuccessful) {
                            callback(Result.failure(IOException("HTTP ${resp.code}: ${resp.message}")))
                            return
                        }

                        try {
                            val responseBody = resp.body?.string() ?: ""

                            // Parse as JsonElement first
                            val jsonElement = Json.parseToJsonElement(responseBody)


                            // Extract the "Events" array from the response
                            val teamsArray = jsonElement.jsonObject["teams"]?.jsonArray

                            if (teamsArray != null) {
                                // Convert JsonArray to Array<JsonObject>
                                val teamNames = teamsArray.map { readableTeamName(it.jsonObject["nameShort"]?.jsonPrimitive!!.content, it.jsonObject["displayTeamNumber"]?.jsonPrimitive!!.content) }.toTypedArray()
                                callback(Result.success(teamNames.toMutableList()))
                            } else {
                                callback(Result.failure(Exception("No 'teams' array found in response")))
                            }
                        } catch (e: Exception) {
                            callback(Result.failure(e))
                        }
                    }
                }
            }
        )
    }

    fun getTotalPagesOfTeamsInEvent(year: String, eventCode: String, callback: (Result<Int>) -> Unit) {
        val url = "https://ftc-api.firstinspires.org/v2.0/$year/teams?eventCode=$eventCode&page=1"

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Basic $authorizationHeader")
            .get()
            .build()

        client.newCall(request).enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { resp ->
                        if (!resp.isSuccessful) {
                            callback(Result.failure(IOException("HTTP ${resp.code}: ${resp.message}")))
                            return
                        }

                        try {
                            val responseBody = resp.body?.string() ?: ""

                            // Parse as JsonElement first
                            val jsonElement = Json.parseToJsonElement(responseBody)


                            // Extract the "Events" array from the response
                            val totalPages = jsonElement.jsonObject["pageTotal"]?.jsonPrimitive?.intOrNull

                            if (totalPages != null) {
                                callback(Result.success(totalPages))
                            } else {
                                callback(Result.failure(Exception("No 'pageTotal' value found in response")))
                            }
                        } catch (e: Exception) {
                            callback(Result.failure(e))
                        }
                    }
                }
            }
        )
    }

    // You'll need to create suspend versions of your callback functions:
    suspend fun getTotalPagesOfTeamsInEventSuspend(year: String, eventCode: String): Int {
        return suspendCoroutine { continuation ->
            getTotalPagesOfTeamsInEvent(year, eventCode) { result ->
                result.fold(
                    onSuccess = { continuation.resume(it) },
                    onFailure = { continuation.resumeWithException(it) }
                )
            }
        }
    }

    suspend fun getTeamsInEventInPageSuspend(year: String, eventCode: String, page: Int): List<String> {
        return suspendCoroutine { continuation ->
            getTeamsInEventInPage(year, eventCode, page) { result ->
                result.fold(
                    onSuccess = { continuation.resume(it) },
                    onFailure = { continuation.resumeWithException(it) }
                )
            }
        }
    }

    suspend fun getTeamsInEvent(year: String, eventCode: String): MutableList<String> {
        // First get total pages
        val totalPages = getTotalPagesOfTeamsInEventSuspend(year, eventCode)

        val allTeamNames = mutableListOf<String>()

        // Process each page sequentially or in parallel
        for (i in 1..totalPages) {
            val teamNames = getTeamsInEventInPageSuspend(year, eventCode, i)
            allTeamNames.addAll(teamNames)
        }

        return allTeamNames
    }

    fun readableTeamName(shortName: String, number: String): String {
        return "$shortName $number"
    }

    fun makeEventNameReadable(name: String, countryName: String): String {
        if (name.contains(countryName)) {
            return name;
        }
        return "$countryName $name"
    }
}