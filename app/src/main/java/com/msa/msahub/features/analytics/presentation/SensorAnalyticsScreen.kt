package com.msa.msahub.features.analytics.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.features.analytics.data.local.entity.SensorAnalyticsEntity
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorAnalyticsScreen(
    deviceId: String,
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensor Analytics") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Temperature Trends (Last 30 Days)", style = MaterialTheme.typography.titleLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // نمایش نمودار خطی ساده با Canvas
            SimpleLineChart(
                data = state.trends,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Daily Statistics", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.trends) { trend ->
                    DailyStatRow(trend)
                }
            }
        }
    }
}

@Composable
fun SimpleLineChart(data: List<SensorAnalyticsEntity>, modifier: Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        
        val maxVal = data.maxOf { it.maxValue }.toFloat()
        val minVal = data.minOf { it.minValue }.toFloat()
        val range = maxVal - minVal
        
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        
        val path = Path()
        data.forEachIndexed { index, entity ->
            val x = index * stepX
            val y = height - ((entity.avgValue.toFloat() - minVal) / range * height)
            
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(path = path, color = primaryColor, style = Stroke(width = 3.dp.toPx()))
    }
}

@Composable
fun DailyStatRow(trend: SensorAnalyticsEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Date: ${trend.dateMillis}") // نیاز به فرمت‌کننده تاریخ
                Text("Avg: ${trend.avgValue}°C", style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}
