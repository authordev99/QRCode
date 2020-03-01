package com.teddybrothers.qrcodereader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.otaliastudios.cameraview.controls.Flash
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_main.*

class HistoryActivity : AppCompatActivity(), ItemClickPresenter {

    private val historyItemAdapter = HistoryItemAdapter(this)
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        sessionManager = SessionManager(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.findViewById<TextView>(R.id.title).text = "History"
        toolbar.findViewById<TextView>(R.id.title)
            .setTextColor(ContextCompat.getColor(this, R.color.dark))

        historyRecyclerView.adapter = historyItemAdapter
        historyItemAdapter.updateDataSet(sessionManager.resultScanList.reversed())
    }

    override fun onItemClicked(item: Any) {
        Toast.makeText(this, (item as ResultScan).result, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)

        val closeItem = menu.findItem(R.id.action_close)
        val closeItemLayout = closeItem.actionView.findViewById<IconView>(R.id.icon)
        closeItemLayout.text = getString(R.string.icon_close)
        closeItemLayout.setTextColor(ContextCompat.getColor(this, R.color.dark))
        closeItemLayout.setOnClickListener {
            finish()
        }

        return true
    }
}
