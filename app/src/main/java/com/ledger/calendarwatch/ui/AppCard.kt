package com.ledger.calendarwatch.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * AppCard (not "Card") to collision with Compose "Card"
 * if it is ever used in this project
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CardColors.Gold,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardColors.Surface, RoundedCornerShape(10.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        content()
    }
}
