package com.siddhartha.walletwatcher.presentation.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.siddhartha.walletwatcher.callback.RootCallback
import com.siddhartha.walletwatcher.databinding.FormScreenBinding
import com.siddhartha.walletwatcher.databinding.NameScreenBinding
import com.siddhartha.walletwatcher.databinding.OtpScreenBinding
import com.siddhartha.walletwatcher.databinding.PhoneScreenBinding
import com.siddhartha.walletwatcher.databinding.WelcomeScreenBinding
import com.siddhartha.walletwatcher.domain.model.onboarding.PhoneSmsResponse
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import java.lang.Exception

class OnBoardScreensAdapter :
    RecyclerView.Adapter<OnBoardScreensViewHolder>() {

    private var rootCallback: RootCallback<Any>? = null
    private var dataList = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardScreensViewHolder {
        when (viewType) {
            WELCOME_SCREEN -> {
                val binding = WelcomeScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OnBoardScreensViewHolder.WelcomeScreenViewHolder(binding, rootCallback)
            }

            PHONE_SCREEN -> {
                val binding = PhoneScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OnBoardScreensViewHolder.PhoneScreenViewHolder(binding, rootCallback)
            }

            OTP_SCREEN -> {
                val binding = OtpScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OnBoardScreensViewHolder.OtpScreenViewHolder(binding, rootCallback)
            }

            NAME_SCREEN -> {
                val binding = NameScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OnBoardScreensViewHolder.NameScreenViewHolder(binding, rootCallback)
            }

            FORM_SCREEN -> {
                val binding = FormScreenBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OnBoardScreensViewHolder.FormScreenViewHolder(binding, rootCallback)
            }

            else -> {
                throw Exception("Invalid view type found")
            }
        }
    }

    override fun onBindViewHolder(holder: OnBoardScreensViewHolder, position: Int) {
        when (holder) {
            is OnBoardScreensViewHolder.WelcomeScreenViewHolder -> {
                holder.bind()
            }

            is OnBoardScreensViewHolder.PhoneScreenViewHolder -> {
                holder.bind()
            }

            is OnBoardScreensViewHolder.OtpScreenViewHolder -> {
                holder.bind(dataList[position] as PhoneSmsResponse)
            }

            is OnBoardScreensViewHolder.NameScreenViewHolder -> {
                holder.bind(dataList[position] as UserData)
            }

            is OnBoardScreensViewHolder.FormScreenViewHolder -> {
                holder.bind()
            }

            else -> {}
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return when(dataList[position]){
            is Int -> dataList[position] as Int
            is PhoneSmsResponse -> OTP_SCREEN
            is UserData -> NAME_SCREEN
            else -> 0
        }
    }

    fun setData(items: MutableList<Any>) {
        dataList = items
        notifyDataSetChanged()
    }

    fun addItem(item: Any, position: Int) {
        if (!dataList.contains(item)){
            dataList.add(item)
            notifyItemInserted(position)
        }
    }

    fun removeItem(item: Any, position: Int) {
        dataList.remove(item)
        notifyItemRemoved(position)
    }

    fun setRootCallBack(callback: RootCallback<Any>) {
        this.rootCallback = callback
    }

    fun getItemList(): MutableList<Any> {
        return dataList
    }

    companion object {
        private var index = 0
        private fun getIndex(): Int {
            index += 1
            return index
        }

        val WELCOME_SCREEN = getIndex()
        val PHONE_SCREEN = getIndex()
        val OTP_SCREEN = getIndex()
        val NAME_SCREEN = getIndex()
        val FORM_SCREEN = getIndex()
    }
}