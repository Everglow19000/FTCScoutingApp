import android.os.Environment
import android.service.autofill.FieldClassification
import android.util.Log
import androidx.core.util.Pools
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Constructor
import java.text.SimpleDateFormat
import java.util.Date

const val TAG = "DatabaseHandlerTag"
object DatabaseHandler {
    val database = Firebase.database("https://everglowftcscoutingdatabase-default-rtdb.europe-west1.firebasedatabase.app/")

    val yearRangesToExcelHeader: MutableMap<String, Array<String>> = mutableMapOf(
        "2024-2025" to IntoTheDeepResults.excelTitle
    )
    val matchResultsClassToYearRanges: MutableMap<Class<out MatchResults>, String> = mutableMapOf(
        IntoTheDeepResults::class.java to "2024-2025"
    )

    fun getMatchResultsClassFromYearRangeAndDatabaseEntry(yearRange: String, databaseEntry: Map<String, Map<String, Long>>): MatchResults {
        if (yearRange.equals("2024-2025")) {
            return IntoTheDeepResults(databaseEntry as Map<String?, Map<String?, Long?>?>?)
        }
        throw IllegalArgumentException("Must give a valid yearRange!")
    }

    fun writeMatchResultToDataBase(match: MatchResults, teamName: String) {
        var databaseNewEntryRef = database.getReference(matchResultsClassToYearRanges[match::class.java] + "/" + teamName).child(Utils.getCurrentUtcTime())
        databaseNewEntryRef.setValue(match.databaseEntry)
    }

    fun fetchAllResultsFromSeason(yearRange: String, startDate: Date = Date(0), endDate: Date = Date()) {
        val workbook: XSSFWorkbook = XSSFWorkbook()
        val sheet: XSSFSheet = workbook.createSheet("Sheet1")

        val headerRow: XSSFRow = sheet.createRow(0)
        for (i in 0 until yearRangesToExcelHeader[yearRange]!!.size) {
            headerRow.createCell(i).setCellValue(yearRangesToExcelHeader[yearRange]!![i])
        }

        var rowNum = 1

        database.getReference(yearRange).get().addOnSuccessListener {

            for (team in it.children) {
                for (timestamp in team.children) {
                    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    var date: Date = simpleDateFormat.parse(timestamp.key)

                    if (date.after(startDate) && date.before(endDate)) {
                        var currRow: XSSFRow = sheet.createRow(rowNum)

                        var cellNum = 0

                        currRow.createCell(cellNum).setCellValue(team.key)
                        cellNum++
                        currRow.createCell(cellNum).setCellValue(timestamp.key)
                        cellNum++


                        try {
                            var matchResults: MatchResults = getMatchResultsClassFromYearRangeAndDatabaseEntry(yearRange, timestamp.getValue() as Map<String, Map<String, Long>>)

                            for (number in matchResults.scoringMethodsCounts) {
                                currRow.createCell(cellNum).setCellValue(number.toDouble())
                                cellNum++
                            }
                            rowNum++
                        }
                        catch (e: Exception) {
                            Log.i(TAG, "team that caused exception: ${team.key}")
                            Log.i(TAG, "timestamp that caused exception: ${timestamp.key}")
                            Log.i(TAG, "value of timestamp: ${timestamp.value as Map<String, Map<String, Long>>}")
                            Log.i(TAG, "exception: ${e.toString()}")
                            Log.i(TAG, "stack trace: ${e.stackTraceToString()}")
                        }
                    }

                }
            }

            var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "scouting_data_$yearRange.xlsx")
            if (file.exists()) {
                var numToWrite = 1
                while (file.exists()) {
                    file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "scouting_data_$yearRange($numToWrite).xlsx")
                    numToWrite++
                }
            }

            val fileOutputStream: FileOutputStream = FileOutputStream(file)
            workbook.write(fileOutputStream)
            fileOutputStream.close()
            workbook.close()
        }
    }

    fun getAvailableYearRanges(callback: (Array<String>) -> Unit) {
        database.getReference("").get().addOnSuccessListener { snapshot ->
            val yearRanges = mutableListOf<String>()
            Log.i(TAG, snapshot.children.toString())
            for (child in snapshot.children) {
                child.key?.let { yearRanges.add(it) }
            }
            callback(yearRanges.toTypedArray())
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to get year ranges", exception)
            callback(emptyArray())
        }
    }
}