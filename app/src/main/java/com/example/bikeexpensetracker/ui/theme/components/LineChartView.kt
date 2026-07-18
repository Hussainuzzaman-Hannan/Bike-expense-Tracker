package com.example.bikeexpensetracker.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MileageTrendChart(
    data: List<MileageDataPoint>,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // Chart configuration
                description.isEnabled = false
                setTouchEnabled(true)
                setDragEnabled(true)
                setScaleEnabled(true)
                setPinchZoom(true)

                // X-Axis configuration
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                    textColor = Color.DKGRAY
                    textSize = 10f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index >= 0 && index < data.size) {
                                SimpleDateFormat("dd/MM", Locale.getDefault()).format(data[index].date)
                            } else ""
                        }
                    }
                }

                // Y-Axis configuration
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                    textColor = Color.DKGRAY
                    textSize = 10f
                    axisMinimum = 0f
                }
                axisRight.isEnabled = false

                // Legend
                legend.isEnabled = true
                legend.textSize = 12f
                legend.textColor = Color.DKGRAY
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, point ->
                Entry(index.toFloat(), point.mileage.toFloat())
            }

            val dataSet = LineDataSet(entries, "Mileage (km/l)").apply {
                color = Color.rgb(76, 175, 80)  // Line color
                setCircleColor(Color.rgb(76, 175, 80))  // Circle color
                circleRadius = 4f
                setCircleHoleRadius(2f)
                lineWidth = 2f
                valueTextSize = 10f
                valueTextColor = Color.DKGRAY
                setDrawFilled(true)
                fillColor = Color.rgb(76, 175, 80)
                fillAlpha = 50
                setDrawValues(true)
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
    )
}

data class MileageDataPoint(
    val date: Date,
    val mileage: Double
)