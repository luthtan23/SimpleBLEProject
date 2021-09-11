package com.luthtan.simplebleproject.common

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView


fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

private const val UUID_FORMAT = "00000000-0000-0000-0000-000000000000"

fun uuidFormatChecker(uuid: String): Boolean {
    var stateUuid = true

    try {
        if (uuid.length != UUID_FORMAT.length) {
            stateUuid = false
        } else {
            if (uuid[8].toString() != "-") {
                stateUuid = false
                Log.i("UUID_1", uuid[8].toString())
            }
            if (uuid[13].toString() != "-") {
                stateUuid = false
                Log.i("UUID_2", uuid[13].toString())
            }
            if (uuid[18].toString() != "-") {
                stateUuid = false
                Log.i("UUID_3", uuid[18].toString())
            }
            if (uuid[23].toString() != "-") {
                stateUuid = false
                Log.i("UUID_4", uuid[23].toString())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return stateUuid
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}