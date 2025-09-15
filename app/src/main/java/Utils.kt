import android.content.Context
import java.util.TimeZone
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar

object Utils {
    private val TAG = "UtilsTag"

    public lateinit var ftcApiHandlerInstance: FTCApiHandler

    fun getCurrentUtcTime(): String {
        var calendar: Calendar = Calendar.getInstance()
        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.setTimeZone(TimeZone.getTimeZone("UTC"))
        return format.format(calendar)
    }

    fun getCurrentYearRange(): String {
        return "2025-2026"
    }

    fun getCurrentYear(): String {
        return "2025"
    }

    fun isNumeric(strNum: String?): Boolean {
        if (strNum == null) {
            return false;
        }
        try {
            var d: Double = java.lang.Double.parseDouble(strNum)
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    suspend fun getFTCApiHandler(context: Context, fileName: String = "ftc-events-api.json"): FTCApiHandler? {
        try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            var json = String(buffer, Charsets.UTF_8)
            val apiData: FTCApiHandler.FTCApiData = Json.decodeFromString<FTCApiHandler.FTCApiData>(json)
            val apiHandler: FTCApiHandler = FTCApiHandler(apiData)
            apiHandler.initializeMap()
            return apiHandler
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
            ex.printStackTrace()
            return null
        }
    }
}