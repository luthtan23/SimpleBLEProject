package com.luthtan.simplebleproject.common

import android.content.Context

class CustomAlertDialog(val context: Context) {

    var message: String? = null
    var title: String? = null

    constructor(context: Context, message: String) : this(context) {
        this.message = message
    }
    constructor(context: Context, title: String, message: String) : this(context) {
        this.title = title
        this.message = message
    }


    init {
//        this.context = context
    }

    fun showAlertDialog() {

    }
}