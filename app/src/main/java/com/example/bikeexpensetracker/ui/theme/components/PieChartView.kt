package com.example.bikeexpensetracker.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

@Composable
fun ExpensePieChart(
    fuelCost: Double,
    maintenanceCost: Double,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setExtraOffsets(5f, 10f, 5f, 5f)
                setDragDecelerationFrictionCoef(0.95f)
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                setTransparentCircleColor(Color.WHITE)
                setTransparentCircleAlpha(110)
                holeRadius = 58f
                transparentCircleRadius = 61f
                setDrawCenterText(true)
                centerText = "Total\nExpenses"
                setCenterTextSize(14f)
                setCenterTextColor(Color.DKGRAY)

                legend.isEnabled = true
                legend.textSize = 12f
                legend.textColor = Color.DKGRAY
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
            }
        },
        update = { chart ->
            val entries = mutableListOf<PieEntry>()
            if (fuelCost > 0) {
                entries.add(PieEntry(fuelCost.toFloat(), "Fuel"))
            }
            if (maintenanceCost > 0) {
                entries.add(PieEntry(maintenanceCost.toFloat(), "Maintenance"))
            }

            if (entries.isNotEmpty()) {
                val colors = mutableListOf<Int>()
                if (fuelCost > 0) colors.add(Color.rgb(76, 175, 80))  // Green for Fuel
                if (maintenanceCost > 0) colors.add(Color.rgb(255, 152, 0))  // Orange for Maintenance

                val dataSet = PieDataSet(entries, "Expense Distribution").apply {
                    this.colors = colors
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                    setValueFormatter(PercentFormatter(chart))
                }

                chart.data = PieData(dataSet)
                chart.invalidate()
            }
        },
        modifier = modifier
    )
}