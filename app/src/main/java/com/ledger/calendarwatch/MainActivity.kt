package com.ledger.calendarwatch

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.ledger.calendarwatch.ui.AppCard
import com.ledger.calendarwatch.ui.CardColors
import com.ledger.calendarwatch.ui.CardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AlarmScheduler.scheduleDailyUpdate(this)

        // Alarm set when missed (fresh install, watch was off at 23:59, etc.) by re-checking
        // today's and tomorrow's events right now
        CoroutineScope(Dispatchers.IO).launch {
            AlarmSyncCoordinator.syncTodayAndTomorrow(this@MainActivity)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager?.canUseFullScreenIntent() == false) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                    data = "package:$packageName".toUri()
                }
                startActivity(intent)
            }
        }

        setContent {
            CardTheme {
                TodayScreen()
            }
        }
    }
}

@Composable
fun TodayScreen() {
    var events by remember { mutableStateOf<List<EventEntry>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()

    LaunchedEffect(Unit) {
        scope.launch {
            events = runCatching { CalendarRepository.getEventsForDate(LocalDate.now()) }
                .getOrDefault(emptyList())
            loading = false
        }
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColors.Background)
            .padding(horizontal = 8.dp),
        state = listState
    ) {
        item {
            Text(
                text = "TODAY",
                style = MaterialTheme.typography.title3.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                color = CardColors.Gold,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            )
        }
        if (loading) {
            item {
                Text(
                    text = "Loading…",
                    color = CardColors.TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else if (events.isEmpty()) {
            item {
                Text(
                    text = "No events today",
                    color = CardColors.TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(events, key = { it.id }) { event ->
                AppCard(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.body1,
                        color = CardColors.TextPrimary
                    )
                    val timeLabel = listOfNotNull(event.start, event.end).joinToString(" – ")
                    if (timeLabel.isNotBlank()) {
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.caption2,
                            color = CardColors.Gold
                        )
                    }
                }
            }
        }
    }
}