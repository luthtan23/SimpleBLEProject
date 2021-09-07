package com.luthtan.simplebleproject.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.luthtan.simplebleproject.data.di.DATABASE_NAME
import com.luthtan.simplebleproject.data.local.room.DashboardDB
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val LOGGER = "DATABASE_LOGGER"
const val MAXIMUM_DATABASE_FILE = 3

@SuppressLint("SimpleDateFormat")
fun getDateFromMillis(millis: Long): String {

    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return formatter.format(calendar.time)

}

fun backupDatabaseUser(dashboardDB: DashboardDB, context: Context) {
    dashboardDB.close()

    val dbFile = context.getDatabasePath(DATABASE_NAME)
    val sDir = File(dbFile.absolutePath, "backup")
    val fileName = DATABASE_NAME.plus(getDateFromMillis(System.currentTimeMillis()))

    val sfPath = sDir.path.plus(File.separator).plus(fileName)

    if (!sDir.exists()) {
        sDir.mkdir()
    } else {
        checkAndDeleteBackupFile(sDir, sfPath)
    }

    val saveFile = File(sfPath)
    if (saveFile.exists()) {
        Log.d(LOGGER, "File exists. Deleting it and then creating new file.");
        saveFile.delete()
    }
    try {
        if (saveFile.createNewFile()) {
            val bufferSize = 8 * 1024
            val buffer = ByteArray(bufferSize)
            var byteRead: Int
            val saveDb = FileOutputStream(sfPath)
            val inDb = FileInputStream(dbFile)
            while ((inDb.read(buffer, 0, bufferSize)) > 0) {
                byteRead = inDb.read(buffer, 0, bufferSize)
                saveDb.write(buffer, 0, byteRead)
            }
            saveDb.flush()
            inDb.close()
            saveDb.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d(LOGGER, "ex: $e")
    }
}

fun checkAndDeleteBackupFile(directory: File, path: String) {
    val currentDateFile = File(path)
    var fileIndex = -1
    var lastModifiedTime = System.currentTimeMillis()

    if (!currentDateFile.exists()) {
        val files = directory.listFiles()
        if (files != null && files.size >= MAXIMUM_DATABASE_FILE) {
            for (i in 0..files.size) {
               val file = files[i]
               val fileLastModifiedTime = file.lastModified()
                if (fileLastModifiedTime < lastModifiedTime) {
                    lastModifiedTime = fileLastModifiedTime
                    fileIndex = i
                }
            }

            if (fileIndex != -1) {
                val deletingFile = files[fileIndex]
                if (deletingFile.exists()) {
                    deletingFile.delete()
                }
            }
        }
    }
}