package com.siddhartha.walletwatcher.presentation.base

import androidx.core.content.ContextCompat
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.presentation.common.ProgressDialog
import com.siddhartha.walletwatcher.util.AppUtil

abstract class BaseActivity<T : ViewDataBinding, V : ViewModel> : AppCompatActivity() {

    private lateinit var mViewDataBinding: T
    private var mViewModel: V? = null

    private val customProgress = ProgressDialog.getInstance()

    /**
     *@return view binding
     */
    open fun getViewDataBinding(): T {
        return mViewDataBinding
    }

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
        super.onCreate(savedInstanceState)
        performDataBinding()
        AppUtil.changeDeviceBarColor(this)
    }


    /**
     * Launch(start) the activity from here
     */
    open fun launchActivity(classType: Intent, view: View) {
        AppUtil.preventTwoClick(view)
        startActivity(classType)
    }

    /**
     * Binding the View and ViewModel
     */
    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        this.mViewModel = if (mViewModel == null) getViewModel() else mViewModel
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding.executePendingBindings()
    }

    /**
     * Method for hide keyboard
     */
    open fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    open fun showKeyboard(edit: View) {
        val view = this.currentFocus
        val methodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assert(view != null)
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun showProgressDialog(message: String = "Loading") {
        customProgress?.let {
            if(!it.isVisible())
                it.showProgress(this, message, false)
        }
    }

    open fun finishProgressDialog() {
        customProgress?.let {
            if(it.isVisible())
                it.hideProgress()
        }
    }
}