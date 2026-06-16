package io.legado.probe

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class WebViewProbeActivity : Activity() {

    private var server: ProbeHttpServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "Legado Android Probe is running\nPort: 18888"
            textSize = 16f
            setPadding(32, 48, 32, 32)
        })
        if (server == null) {
            server = ProbeHttpServer(applicationContext, 18888).apply {
                start()
                Log.i("Probe", "Server started on port 18888")
            }
        }
    }

    override fun onDestroy() {
        server?.stop()
        server = null
        super.onDestroy()
    }
}
