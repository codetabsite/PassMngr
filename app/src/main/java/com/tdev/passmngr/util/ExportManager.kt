package com.tdev.passmngr.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.tdev.passmngr.data.model.Password
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExportManager {

    fun exportToCsv(context: Context, passwords: List<Password>): Intent {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val filename = "passmngr_backup_${sdf.format(Date())}.csv"
        val file = File(context.cacheDir, filename)

        file.bufferedWriter().use { writer ->
            writer.write("Account,Username,Password,Category,Created\n")
            passwords.forEach { p ->
                val decrypted = CryptoManager.decrypt(p.encryptedPassword)
                val created = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(p.createdAt))
                writer.write("\"${p.accountName}\",\"${p.username}\",\"$decrypted\",\"${p.category.label}\",\"$created\"\n")
            }
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
