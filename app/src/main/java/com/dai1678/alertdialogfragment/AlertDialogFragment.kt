package com.dai1678.alertdialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


/**
 * メッセージダイアログ
 */
private const val DIALOG_TAG = "alertDialogFragment"
private const val DIALOG_REQUEST_CODE = 1234
private const val KEY_TITLE = "title"
private const val KEY_MESSAGE = "message"
private const val KEY_MESSAGE_FORMAT_ARGS = "messageFormatArgs"
private const val KEY_POSITIVE_BUTTON_TITLE = "positive"
private const val KEY_NEGATIVE_BUTTON_TITLE = "negative"
private const val KEY_CANCELABLE = "cancelable"

@DslMarker
annotation class AlertDialogFragmentBuilderDsl

fun alertDialogFragment(
    setup: AlertDialogFragment.Builder.() -> Unit
): AlertDialogFragment {
    return AlertDialogFragment.Builder().apply(setup).build()
}

/**
 * メッセージとボタンを含むダイアログ
 */
class AlertDialogFragment : DialogFragment() {
    private var alertDialogFragmentListener: AlertDialogFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        alertDialogFragmentListener = when {
            context is AlertDialogFragmentListener -> context
            targetFragment is AlertDialogFragmentListener -> targetFragment as AlertDialogFragmentListener
            else -> null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = getString(arguments?.getInt(KEY_TITLE) ?: R.string.blank)
        val messageResId = arguments?.getInt(KEY_MESSAGE) ?: R.string.blank
        val messageFormatArgs = arguments?.getStringArray(KEY_MESSAGE_FORMAT_ARGS)
        val message = if (messageFormatArgs.isNullOrEmpty()) {
            getString(messageResId)
        } else {
            getString(messageResId, *messageFormatArgs)
        }
        val positiveButtonTitle =
            getString(arguments?.getInt(KEY_POSITIVE_BUTTON_TITLE) ?: R.string.ok)
        val negativeButtonTitle =
            getString(arguments?.getInt(KEY_NEGATIVE_BUTTON_TITLE) ?: R.string.blank)
        val cancelable = arguments?.getBoolean(KEY_CANCELABLE) ?: true
        val activity = activity ?: return super.onCreateDialog(savedInstanceState)

        isCancelable = cancelable
        return AlertDialog.Builder(activity).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButtonTitle) { dialog, which ->
                alertDialogFragmentListener?.onPositiveClick(dialog, which)
            }
            if (negativeButtonTitle.isBlank().not()) {
                setNegativeButton(negativeButtonTitle) { dialog, which ->
                    alertDialogFragmentListener?.onNegativeClick(dialog, which)
                }
            }
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (targetFragment as? DialogInterface.OnDismissListener
            ?: activity as? DialogInterface.OnDismissListener)?.onDismiss(dialog)
    }

    @Suppress("DEPRECATION")
    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, DIALOG_TAG)
    }

    @Deprecated(
        "Please use the show method implemented in AlertDialogFragment",
        ReplaceWith("fragment.show(manager, this)")
    )
    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

    @Suppress("DEPRECATION")
    @JvmOverloads
    fun <T> show(
        fragmentManager: FragmentManager,
        fragment: T,
        tag: String? = DIALOG_TAG,
        requestCode: Int = DIALOG_REQUEST_CODE
    ) where T : Fragment, T : AlertDialogFragmentListener {
        setTargetFragment(fragment, requestCode)
        show(fragmentManager, tag)
    }

    /**
     * JavaではKotlinDslが使えないので、Builderを用意しておく
     */
    @AlertDialogFragmentBuilderDsl
    class Builder {
        private val args = Bundle()
        var titleResId: Int = R.string.blank
        var messageResId: Int = R.string.blank
        var messageFormatArgs: Array<String>? = null // 書式文字列の設定
        var positiveTitleResId: Int = R.string.ok
        var negativeTitleResId: Int = R.string.blank
        var cancelable = true

        fun setTitle(titleResId: Int): Builder {
            this.titleResId = titleResId
            return this
        }

        fun setMessage(messageResId: Int, vararg formatArgs: String): Builder {
            this.messageResId = messageResId
            this.messageFormatArgs = arrayOf(*formatArgs)
            return this
        }

        fun setPositiveTitle(positiveTitleResId: Int): Builder {
            this.positiveTitleResId = positiveTitleResId
            return this
        }

        fun setNegativeTitle(negativeTitleResId: Int): Builder {
            this.negativeTitleResId = negativeTitleResId
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun build(): AlertDialogFragment = AlertDialogFragment().apply {
            arguments = args.apply {
                putInt(KEY_TITLE, titleResId)
                putInt(KEY_MESSAGE, messageResId)
                putStringArray(KEY_MESSAGE_FORMAT_ARGS, messageFormatArgs)
                putInt(KEY_POSITIVE_BUTTON_TITLE, positiveTitleResId)
                putInt(KEY_NEGATIVE_BUTTON_TITLE, negativeTitleResId)
                putBoolean(KEY_CANCELABLE, cancelable)
            }
        }
    }

    /**
     * コールバックを実行する側で複数の処理パターンがある場合は、tagで判別する
     */
    interface AlertDialogFragmentListener {
        fun onPositiveClick(dialog: DialogInterface, which: Int) = Unit
        fun onNegativeClick(dialog: DialogInterface, which: Int) = Unit
    }
}
