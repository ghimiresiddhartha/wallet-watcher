package com.siddhartha.walletwatcher.presentation.home

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.siddhartha.walletwatcher.BR
import com.siddhartha.walletwatcher.R
import com.siddhartha.walletwatcher.databinding.ActivityHomeBinding
import com.siddhartha.walletwatcher.domain.model.onboarding.FormData
import com.siddhartha.walletwatcher.domain.model.onboarding.UserData
import com.siddhartha.walletwatcher.presentation.base.BaseActivity
import com.siddhartha.walletwatcher.util.AppConstant.CATEGORY_ENTERTAINMENT
import com.siddhartha.walletwatcher.util.AppConstant.CATEGORY_FOOD
import com.siddhartha.walletwatcher.util.AppConstant.CATEGORY_TRANSPORTATION
import com.siddhartha.walletwatcher.util.AppConstant.FORM_DATA
import com.siddhartha.walletwatcher.util.AppConstant.USER_DATA
import com.siddhartha.walletwatcher.util.ext.gone
import com.siddhartha.walletwatcher.util.ext.visible

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private var userScreenName: String = ""
    private var monthlySalary: Int = 0
    private var foodExpenseAmount: Int = 0
    private var transportationExpenseAmount = 0
    private var entertainmentExpenseAmount = 0
    private val pieChartColorList = mutableListOf(Color.GREEN, Color.RED)
    private val pieChartEntryList = mutableListOf<PieEntry>()

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getViewModel(): HomeViewModel = homeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        initBundleData()
        initViewAndResources()
    }

    private fun initBundleData() {
        val bundle = intent.extras
        bundle?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(USER_DATA, UserData::class.java)
            } else {
                it.getParcelable(USER_DATA)
            }?.apply {
                userScreenName = screenName ?: ""
            }

            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(FORM_DATA, FormData::class.java)
            } else {
                it.getParcelable(FORM_DATA)
            })?.apply {
                monthlySalary = monthlyIncome ?: 0
                foodExpenseAmount = foodAmount ?: 0
                transportationExpenseAmount = transportationAmount ?: 0
                entertainmentExpenseAmount = entertainmentAmount ?: 0
            }
        }
    }

    private fun initViewAndResources() {
        with(binding) {
            ivExpandArrowFood.setOnClickListener {
                pcEntertainment.gone()
                pcTransportation.gone()
                configurePieChart(CATEGORY_FOOD)
                expandCategory(ivExpandArrowFood, pcFood, cvFoodCardHome)
            }

            ivExpandArrowEntertainment.setOnClickListener {
                pcFood.gone()
                pcTransportation.gone()
                configurePieChart(CATEGORY_ENTERTAINMENT)
                expandCategory(
                    ivExpandArrowEntertainment, pcEntertainment, cvEntertainmentCardHome
                )
            }

            ivExpandArrowTransportation.setOnClickListener {
                pcEntertainment.gone()
                pcFood.gone()
                configurePieChart(CATEGORY_TRANSPORTATION)
                expandCategory(
                    ivExpandArrowTransportation, pcTransportation, cvTransportationCardHome
                )
            }

            tvUserName.text = userScreenName
            tvMonthlyIncome.text = monthlySalary.toString()
            tvYearlyIncome.text = (monthlySalary * 12).toString()
        }
    }

    private fun expandCategory(arrow: ImageView, categoryCardView: PieChart, parentView: CardView) {
        if (!categoryCardView.isVisible) {
            arrow.rotation = 270f
            TransitionManager.beginDelayedTransition(parentView, AutoTransition())
            categoryCardView.visible()
        } else {
            arrow.rotation = 0f
            categoryCardView.gone()
        }
    }

    private fun configurePieChart(categoryType: String) {
        pieChartEntryList.clear()
        pieChartEntryList.add(PieEntry(monthlySalary.toFloat(), "Income"))
        when (categoryType) {
            CATEGORY_FOOD -> {
                displayFoodPieChart()
            }

            CATEGORY_ENTERTAINMENT -> {
                displayEntertainmentPieChart()
            }

            CATEGORY_TRANSPORTATION -> {
                displayTransportationPieChart()
            }
        }
    }

    private fun displayFoodPieChart() {
        pieChartEntryList.add(PieEntry(foodExpenseAmount.toFloat(), "Expense"))
        val pieFoodDataSet = PieDataSet(
            pieChartEntryList, "You saved Rs${monthlySalary - foodExpenseAmount} on food."
        )
        pieFoodDataSet.colors = pieChartColorList
        pieFoodDataSet.valueTextColor = R.color.color_accent

        val pieFoodData = PieData(pieFoodDataSet)
        binding.pcFood.apply {
            data = pieFoodData
            invalidate()
        }
    }

    private fun displayTransportationPieChart() {
        pieChartEntryList.add(PieEntry(transportationExpenseAmount.toFloat(), "Expense"))
        val pieTransportationDataSet = PieDataSet(
            pieChartEntryList,
            "You saved Rs${monthlySalary - transportationExpenseAmount} on transportation."
        )
        pieTransportationDataSet.colors = pieChartColorList
        pieTransportationDataSet.valueTextColor = R.color.color_accent

        val pieTransportationData = PieData(pieTransportationDataSet)

        binding.pcTransportation.apply {
            data = pieTransportationData
            invalidate()
        }
    }

    private fun displayEntertainmentPieChart() {
        pieChartEntryList.add(PieEntry(entertainmentExpenseAmount.toFloat(), "Expense"))
        val pieEntertainmentDataSet = PieDataSet(
            pieChartEntryList,
            "You saved Rs${monthlySalary - entertainmentExpenseAmount} on entertainment."
        )
        pieEntertainmentDataSet.colors = pieChartColorList
        pieEntertainmentDataSet.valueTextColor = R.color.color_accent

        val pieEntertainmentData = PieData(pieEntertainmentDataSet)
        binding.pcEntertainment.apply {
            data = pieEntertainmentData
            invalidate()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            with(binding) {
                tvTitleMonthlyIncome.gone()
                tvMonthlyIncome.gone()
                tvTitleYearlyIncome.gone()
                tvYearlyIncome.gone()
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            with(binding) {
                tvTitleMonthlyIncome.visible()
                tvMonthlyIncome.visible()
                tvTitleYearlyIncome.visible()
                tvYearlyIncome.visible()
            }
        }
    }
}