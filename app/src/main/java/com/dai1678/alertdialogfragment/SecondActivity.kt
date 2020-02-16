package com.dai1678.alertdialogfragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity(), AlertDialogFragment.AlertDialogFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        show_dialog_button.setOnClickListener {
            alertDialogFragment {
                titleResId = R.string.sample_dialog_title
                messageResId = R.string.sample_dialog_message
            }.show(supportFragmentManager)
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, SecondActivity::class.java)
    }

    override fun onPositiveClick(dialog: DialogInterface, which: Int) {
        Toast.makeText(this, "YES!!!", Toast.LENGTH_SHORT).show()
    }
}
