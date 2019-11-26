package com.dai1678.alertdialogfragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dai1678.alertdialogfragment.AlertDialogFragment.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), AlertDialogFragmentListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        show_dialog_button.setOnClickListener {
            val dialog = AlertDialogFragment.newInstance(
                title = "タイトル",
                message = "メッセージ",
                positiveTitle = "ポジティブ",
                negativeTitle = "ネガティブ"
            )
            dialog.setTargetFragment(this, AlertDialogFragment.DIALOG_REQUEST_CODE)
            dialog.show(requireFragmentManager(), AlertDialogFragment.DIALOG_TAG)
        }

        start_second_activity_button.setOnClickListener {
            startActivity(SecondActivity.createIntent(requireContext()))
        }
    }

    override fun onPositiveClick(dialog: DialogInterface, which: Int) {
        Toast.makeText(context, "YES!!!", Toast.LENGTH_SHORT).show()
    }

    override fun onNegativeClick(dialog: DialogInterface, which: Int) {
        dialog.dismiss()
    }

    companion object {
        const val FRAGMENT_TAG = "MAIN_FRAGMENT"

        fun newInstance(): Fragment {
            return MainFragment()
        }
    }
}
