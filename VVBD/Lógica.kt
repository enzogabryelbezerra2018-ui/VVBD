package com.example.vvbd

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.vvbd.databinding.ActivityMainBinding
import java.io.InputStream
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Launcher para escolher arquivo (IMPORT)
    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            onFilePicked(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botões / cards
        binding.btnInstallOS.setOnClickListener {
            confirmBackupThen { openInstallOSFlow() }
        }

        binding.btnInstallMod.setOnClickListener {
            confirmBackupThen { openInstallModifiedAndroidFlow() }
        }

        binding.btnImport.setOnClickListener {
            openFilePicker()
        }
    }

    private fun confirmBackupThen(action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage(getString(R.string.confirm_backup))
            .setPositiveButton("Continuar") { _, _ -> action() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun openInstallOSFlow() {
        // Aqui você abriria a Activity/Fragment que implementa verificações, dry-run e instruções
        // Por agora, só mostramos um toast e um exemplo de fluxo.
        Toast.makeText(this, "Abrindo fluxo: Instalar SO (modo seguro)", Toast.LENGTH_SHORT).show()
        // TODO: startActivity(Intent(this, InstallOSActivity::class.java))
    }

    private fun openInstallModifiedAndroidFlow() {
        Toast.makeText(this, "Abrindo fluxo: Instalar Android modificado", Toast.LENGTH_SHORT).show()
        // TODO: startActivity(Intent(this, InstallModifiedActivity::class.java))
    }

    private fun openFilePicker() {
        // Filtrar para permitir zips e imagens (MIME variados). OpenDocument permite escolher a partir do armazenamento.
        pickFileLauncher.launch(arrayOf(
            "application/zip",
            "application/octet-stream",
            "application/x-zip-compressed",
            "application/*",
            "image/*"
        ))
    }

    private fun onFilePicked(uri: Uri) {
        Toast.makeText(this, "Arquivo selecionado: $uri", Toast.LENGTH_SHORT).show()

        // Exemplo: calcular SHA-256 do arquivo selecionado e mostrar num diálogo
        try {
            val sha256 = calculateSha256(uri)
            AlertDialog.Builder(this)
                .setTitle("Arquivo selecionado")
                .setMessage("URI: $uri\nSHA256: $sha256")
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao ler arquivo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun calculateSha256(uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256")
        contentResolver.openInputStream(uri).use { input ->
            val buffer = ByteArray(1024 * 8)
            var read: Int
            while (input?.read(buffer).also { read = it ?: -1 } != -1) {
                if (read > 0) digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
