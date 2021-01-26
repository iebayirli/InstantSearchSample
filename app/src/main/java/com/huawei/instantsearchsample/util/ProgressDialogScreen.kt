package com.huawei.instantsearchsample.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.huawei.instantsearchsample.R
import com.huawei.instantsearchsample.databinding.ItemProgressdialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProgressDialogScreen(
    private val context: Context,
) {

    private val dialog = Dialog(context, android.R.style.Theme_Black)
    private val view = ItemProgressdialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setContentView(view.root)
            setCancelable(false)
        }
    }

    fun showProgress(message: String = "") {
        if (!dialog.isShowing) {
            view.tvInfo.text = message
            dialog.show()
        }
    }

    fun dismissProgress() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }


}