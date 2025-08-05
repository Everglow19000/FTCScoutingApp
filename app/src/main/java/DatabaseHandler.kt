import com.google.firebase.Firebase
import com.google.firebase.database.database

object DatabaseHandler {
    val database = Firebase.database("https://everglowftcscoutingdatabase-default-rtdb.europe-west1.firebasedatabase.app/")
    val yearRangesToMatchResultsClass: MutableMap<String, Class<out MatchResults>> = mutableMapOf(
        "2024-2025" to IntoTheDeepResults::class.java
    )
    val matchResultsClassToYearRanges: MutableMap<Class<out MatchResults>, String> = mutableMapOf(
        IntoTheDeepResults::class.java to "2024-2025"
    )

    fun writeMatchResultToDataBase(match: MatchResults, teamName: String) {
        var databaseNewEntryRef = database.getReference(matchResultsClassToYearRanges[match::class.java] + "/" + teamName).child(Utils.getCurrentUtcTime())
        databaseNewEntryRef.setValue(match.databaseEntry)
    }
}