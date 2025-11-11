package com.luigivampa92.ndefemulator.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luigivampa92.ndefemulation.NdefEmulation
import com.luigivampa92.ndefemulation.ndef.ContactNdefData
import com.luigivampa92.ndefemulation.ndef.LocationNdefData
import com.luigivampa92.ndefemulation.ndef.TextNdefData
import com.luigivampa92.ndefemulation.ndef.UriNdefData
import com.luigivampa92.ndefemulation.ndef.WifiNetworkNdefData
import com.luigivampa92.ndefemulation.ndef.WifiNetworkNdefDataProtectionType
import com.luigivampa92.ndefemulator.R
import java.util.Date

class DemoActivity : BaseActivity() {

	private lateinit var ndefEmulation: NdefEmulation
	private lateinit var buttonMainAction: Button
	private lateinit var buttonPrev: Button
	private lateinit var buttonNext: Button
	private lateinit var buttonClear: Button

	private data class DemoAction(
		val label: String,
		val perform: () -> Unit,
		val toastMessage: String
	)

	private lateinit var actions: List<DemoAction>
	private var selectedIndex: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		ndefEmulation = NdefEmulation(this)

		buttonClear = findViewById(R.id.button_clear)
		buttonMainAction = findViewById(R.id.button_main_action)
		buttonPrev = findViewById(R.id.button_nav_prev)
		buttonNext = findViewById(R.id.button_nav_next)

					actions = listOf(
				DemoAction("YT_URL", {
					ndefEmulation.currentEmulatedNdefData = UriNdefData("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
				}, "Youtube URL"),
				DemoAction("YT URI", {
					ndefEmulation.currentEmulatedNdefData = UriNdefData("vnd.youtube://www.youtube.com/watch?v=dQw4w9WgXcQ")
				}, "Youtube URI"),
				DemoAction("WA URL", {
					ndefEmulation.currentEmulatedNdefData = UriNdefData("https://wa.me/79123456789")
				}, "WhatsApp"),
				DemoAction("TG URI", {
					ndefEmulation.currentEmulatedNdefData = UriNdefData("tg://msg?to=+79123456789")
				}, "Telegram"),
				DemoAction("TEXT", {
					ndefEmulation.currentEmulatedNdefData = TextNdefData("Whiskey Tango Foxtrot")
				}, "Text"),
				DemoAction("GEO", {
					ndefEmulation.currentEmulatedNdefData = LocationNdefData(59.940825, 30.410988)
				}, "Location"),
				DemoAction("WIFI", {
					ndefEmulation.currentEmulatedNdefData = WifiNetworkNdefData("TestWifi", WifiNetworkNdefDataProtectionType.PASSWORD, "TestWifiPassword")
				}, "WiFi"),
				DemoAction("CONTACT", {
					ndefEmulation.currentEmulatedNdefData = ContactNdefData(
						"Thomas",
						"Anderson",
						"+13125550690",
						"tanderson@metacortex.com",
						Date(53617109910L),
						"MetaCortex",
						"Software Engineer",
						"https://hackertyper.net",
						"Wake up",
					)
				}, "Contact"),
				DemoAction("Website", {
					ndefEmulation.currentEmulatedNdefData = UriNdefData("https://sgomezsal.com")
				}, "Website")
			)

		selectedIndex = savedInstanceState?.getInt("selectedIndex")
			?: (actions.indexOfFirst { it.label == "Website" }.takeIf { it >= 0 } ?: 0)
		updateMainButton()

		buttonMainAction.setOnClickListener {
			actions[selectedIndex].perform.invoke()
			toast(actions[selectedIndex].toastMessage)
		}

		buttonPrev.setOnClickListener {
			selectedIndex = if (selectedIndex - 1 < 0) actions.size - 1 else selectedIndex - 1
			updateMainButton()
		}

		buttonNext.setOnClickListener {
			selectedIndex = (selectedIndex + 1) % actions.size
			updateMainButton()
		}

		buttonClear.setOnClickListener {
			ndefEmulation.currentEmulatedNdefData = null
			toast("Clear data")
		}
	}

	private fun updateMainButton() {
		buttonMainAction.text = actions[selectedIndex].label
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt("selectedIndex", selectedIndex)
	}

	private fun toast(message: String?) {
		if (!message.isNullOrBlank()) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show()
		}
	}
}