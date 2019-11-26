package com.dai1678.alertdialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment


/**
 * メッセージダイアログ
 */
class AlertDialogFragment : DialogFragment() {
    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mPositiveButtonTitle: String? = null
    private var mNegativeButtonTitle: String? = null
    private var alertDialogFragmentListener: AlertDialogFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        alertDialogFragmentListener = when {
            context is AlertDialogFragmentListener -> context
            targetFragment is AlertDialogFragmentListener -> targetFragment as AlertDialogFragmentListener
            else -> throw IllegalAccessException("$context not implemented listener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTitle = savedInstanceState?.getString(KEY_TITLE) ?: arguments?.getString(KEY_TITLE)
        mMessage = savedInstanceState?.getString(KEY_MESSAGE) ?: arguments?.getString(KEY_MESSAGE)
        mPositiveButtonTitle =
            savedInstanceState?.getString(KEY_POSITIVE_BUTTON_TITLE) ?: arguments?.getString(
                KEY_POSITIVE_BUTTON_TITLE
            )
        mNegativeButtonTitle =
            savedInstanceState?.getString(KEY_NEGATIVE_BUTTON_TITLE) ?: arguments?.getString(
                KEY_NEGATIVE_BUTTON_TITLE
            )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(mTitle)
                setMessage(mMessage)

                val positiveTitle = if (mPositiveButtonTitle.isNullOrEmpty()) {
                    getString(android.R.string.ok)
                } else {
                    mPositiveButtonTitle.orEmpty()
                }
                setPositiveButton(positiveTitle) { dialog, which ->
                    alertDialogFragmentListener?.onPositiveClick(dialog, which)
                }

                setNegativeButton(mNegativeButtonTitle) { dialog, which ->
                    alertDialogFragmentListener?.onNegativeClick(dialog, which)
                }
            }.create()
        } ?: throw IllegalAccessError("Activity cannot be null")
    }

    /**
     * Fragmentの中で発生したデータやViewへの入力情報を保持するときは
     * ↓のようにonSaveInstanceStateを使う
     */
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.run {
//            putString(KEY_TITLE, mTitle)
//            putString(KEY_MESSAGE, mMessage)
//            putString(KEY_POSITIVE_BUTTON_TITLE, mPositiveButtonTitle)
//            putString(KEY_NEGATIVE_BUTTON_TITLE, mNegativeButtonTitle)
//        }
//    }

    companion object {
        const val DIALOG_REQUEST_CODE = 0
        const val DIALOG_TAG = "alertDialogFragment"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_POSITIVE_BUTTON_TITLE = "positive"
        private const val KEY_NEGATIVE_BUTTON_TITLE = "negative"

        fun newInstance(
            title: String?,
            message: String?,
            positiveTitle: String?,
            negativeTitle: String?
        ): DialogFragment {
            val dialog = AlertDialogFragment()
            dialog.arguments = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_MESSAGE, message)
                putString(KEY_POSITIVE_BUTTON_TITLE, positiveTitle)
                putString(KEY_NEGATIVE_BUTTON_TITLE, negativeTitle)
            }
            return dialog
        }
    }

    interface AlertDialogFragmentListener {
        fun onPositiveClick(dialog: DialogInterface, which: Int)
        fun onNegativeClick(dialog: DialogInterface, which: Int)
    }
}
