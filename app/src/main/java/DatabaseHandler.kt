import android.os.Environment
import android.service.autofill.FieldClassification
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Constructor

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

    fun fetchAllResultsFromSeason(yearRange: String) {
        val workbook: XSSFWorkbook = XSSFWorkbook()
        val sheet: XSSFSheet = workbook.createSheet("Sheet1")

        val headerRow: XSSFRow = sheet.createRow(0)
        for (i in 0 until yearRangesToExcelHeader[yearRange]!!.size) {
            headerRow.createCell(i).setCellValue(yearRangesToExcelHeader[yearRange]!![i])
        }

        var rowNum = 1

        database.getReference(yearRange).get().addOnSuccessListener {

            for (team in it.children) {
                Log.i(TAG, "teamName: ${team.key}")
                for (timestamp in team.children) {
                    var currRow: XSSFRow = sheet.createRow(rowNum)

                    var cellNum = 0

                    currRow.createCell(cellNum).setCellValue(team.key)
                    cellNum++
                    currRow.createCell(cellNum).setCellValue(timestamp.key)
                    cellNum++

                    Log.i(TAG, "teamName: ${team.key}, timestamp: ${timestamp.key}")

                    try {
                        var matchResults: MatchResults = getMatchResultsClassFromYearRangeAndDatabaseEntry(yearRange, timestamp.getValue() as Map<String, Map<String, Long>>)
                        Log.i(TAG, matchResults.toString())

                        for (number in matchResults.scoringMethodsCounts) {
                            Log.i(TAG, "Row: $rowNum, Cell: $cellNum, value: ${number.toDouble()}")
                            currRow.createCell(cellNum).setCellValue(number.toDouble())
                            cellNum++
                        }
                        rowNum++
                    }
                    catch (e: Exception) {
                        Log.i(TAG, "exception: ${e.toString()}")
                        Log.i(TAG, "stack trace: ${e.stackTraceToString()}")
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
}