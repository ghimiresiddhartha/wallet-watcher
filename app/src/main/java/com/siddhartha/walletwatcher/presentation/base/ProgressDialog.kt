package com.siddhartha.walletwatcher.presentation.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import com.siddhartha.walletwatcher.R

class ProgressDialog {
    private var mDialog: Dialog? = null

    companion object {
        private var showProgressDialog: ProgressDialog? = null

        fun getInstance(): ProgressDialog? {
            if (showProgressDialog == null) {
                showProgressDialog = ProgressDialog()
            }
            return showProgressDialog
        }
    }

    fun showProgress(context: Context?, message: String, cancelable: Boolean) {
        mDialog = Dialog(context!!)
        mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog!!.setContentView(R.layout.progress_bar_dialog)
        mDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val progressText = mDialog!!.findViewById<View>(R.id.progress_text) as TextView
        progressText.text = message
        progressText.visibility = View.VISIBLE
        mDialog!!.setCancelable(cancelable)
        mDialog!!.setCanceledOnTouchOutside(cancelable)
        mDialog!!.show()
    }

    fun isVisible(): Boolean{
        return mDialog?.isShowing ?: false
    }

    fun hideProgress() {
        if (mDialog != null) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }
}