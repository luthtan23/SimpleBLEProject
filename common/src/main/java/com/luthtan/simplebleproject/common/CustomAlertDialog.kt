package com.luthtan.simplebleproject.common

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class CustomAlertDialog(
    private val context: Context, private val title: String?,
    private val message: String, private val cancelable: Boolean, private val positiveListener: DialogInterface.OnClickListener
) {

    private var negativeListener: DialogInterface.OnClickListener? = null

    constructor(context: Context, title: String, message: String, cancelable: Boolean, positiveListener: DialogInterface.OnClickListener,
    negativeListener: DialogInterface.OnClickListener): this(context, title, message, cancelable, positiveListener)  {
        this.negativeListener = negativeListener
    }


    fun show() {
        val builder = AlertDialog.Builder(context)
        if (negativeListener != null) {
            builder.setNegativeButton(CANCEL_TEXT, negativeListener)
        }
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(OK_TEXT, positiveListener)
        builder.show()
    }

}