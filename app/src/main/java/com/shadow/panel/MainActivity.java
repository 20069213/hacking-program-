// 💀ABDO SAAD💀
package com.abdosaad.cyber

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val locales = listOf(
        "ar" to "العربية",
        "en" to "English",
        "fr" to "Français",
        "de" to "Deutsch",
        "ru" to "Русский",
        "tr" to "Türkçe"
    )

    private lateinit var langBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        langBtn = findViewById(R.id.tvLang)
        langBtn.setOnClickListener { showLanguagePicker() }
        refreshLangLabel()

        bind(R.id.cardTerminal) { go(TerminalActivity::class.java) }
        bind(R.id.cardScanner)  { go(NetworkScannerActivity::class.java) }
        bind(R.id.cardToolkit)  { go(ToolkitActivity::class.java) }
        bind(R.id.cardRecon)    { go(ReconActivity::class.java) }
        bind(R.id.cardWifi)     { go(WiFiAnalyzerActivity::class.java) }
        bind(R.id.cardSubnet)   { go(SubnetActivity::class.java) }
        bind(R.id.cardPassword) { go(PasswordForgeActivity::class.java) }
        bind(R.id.cardFileHash) { go(FileHasherActivity::class.java) }
        bind(R.id.cardStego)    { go(StegoActivity::class.java) }
        bind(R.id.cardCrypto)   { go(CryptoActivity::class.java) }
        bind(R.id.cardDns)      { go(DnsQueryActivity::class.java) }
        bind(R.id.cardTls)      { go(TlsCertActivity::class.java) }
        bind(R.id.cardLan)      { go(LanSweepActivity::class.java) }
        bind(R.id.cardHttp)     { go(HttpBuilderActivity::class.java) }
        bind(R.id.cardHashId)   { go(HashIdActivity::class.java) }
        bind(R.id.cardCoder)    { go(MultiCoderActivity::class.java) }
        bind(R.id.cardDefense)  { go(SelfDefenseActivity::class.java) }
        bind(R.id.cardDarkWeb)  { go(DarkWebActivity::class.java) }
        bind(R.id.cardLearning) { go(LearningActivity::class.java) }
        bind(R.id.cardExam)     { go(ExamActivity::class.java) }
        bind(R.id.cardScary)    { go(ScaryActivity::class.java) }
    }

    private fun refreshLangLabel() {
        val cur = AppCompatDelegate.getApplicationLocales()
        val code = if (cur.isEmpty) {
            Locale.getDefault().language
        } else cur.toLanguageTags().split(",").firstOrNull()?.substringBefore("-") ?: "en"
        val name = locales.firstOrNull { it.first == code }?.second ?: code
        langBtn.text = getString(R.string.language_label, name)
    }

    private fun showLanguagePicker() {
        val names = locales.map { it.second }.toTypedArray()
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setItems(names) { _, which ->
                val code = locales[which].first
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
                // restart to apply across all activities
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun bind(id: Int, action: () -> Unit) {
        findViewById<MaterialCardView>(id).setOnClickListener { action() }
    }

    private fun go(cls: Class<*>) { startActivity(Intent(this, cls)) }
            }
