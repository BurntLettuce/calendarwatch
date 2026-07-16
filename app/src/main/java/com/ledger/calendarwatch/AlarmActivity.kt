package com.ledger.calendarwatch

import android.media.RingtoneManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.ledger.calendarwatch.ui.CardColors
import com.ledger.calendarwatch.ui.CardTheme
import android.app.KeyguardManager

class AlarmActivity : ComponentActivity() {

    private var ringtone: android.media.Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wake + show over lock screen, same as a stock alarm.
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getSystemService(KeyguardManager::class.java)?.requestDismissKeyguard(this, null)

        val title = intent.getStringExtra("title") ?: "Event"
        val note = intent.getStringExtra("note")

        playAlarmSound()
        vibrate()

        setContent {
            CardTheme {
                val glow = Brush.radialGradient(
                    colors = listOf(
                        CardColors.Purple.copy(alpha = 0.35f),
                        CardColors.Background
                    ),
                    center = Offset.Unspecified,
                    radius = 480f
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(glow)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(1.dp)
                                .background(CardColors.PurpleBright)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.title2.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            color = CardColors.PurpleBright
                        )

                        if (!note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = note,
                                style = MaterialTheme.typography.body2,
                                color = CardColors.TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(1.dp)
                                .background(CardColors.PurpleBright)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { finishAndStop() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }

    private fun playAlarmSound() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, uri)
        ringtone?.play()
    }

    private fun vibrate() {
        val vibrator = getSystemService(Vibrator::class.java) ?: return
        val pattern = longArrayOf(0, 500, 500)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun finishAndStop() {
        ringtone?.stop()
        getSystemService(Vibrator::class.java)?.cancel()
        finish()
    }

    override fun onStop() {
        // Stops the noise immediately instead of letting it constantly
        // ringing in the background + home button substitute
        ringtone?.stop()
        getSystemService(Vibrator::class.java)?.cancel()
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        ringtone?.stop()
        getSystemService(Vibrator::class.java)?.cancel()
        super.onDestroy()
    }
}