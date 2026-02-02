package com.buulgyeonE202.frontend.ui.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.settings.component.WithdrawBottomActions
import com.buulgyeonE202.frontend.ui.settings.component.WithdrawInfoStep
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.LightGray
import com.buulgyeonE202.frontend.ui.theme.White

@Composable
fun WithdrawPage(
    onStay: () -> Unit = {},
    onWithdraw: () -> Unit = {}
) {
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Text(
                text = "회원탈퇴",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = White
            ) {
                WithdrawInfoStep(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(90.dp))
        }

        WithdrawBottomActions(
            enabled = checked,
            onStay = onStay,
            onWithdraw = { if (checked) onWithdraw() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun WithdrawPagePreview() {
    FrontendTheme {
        WithdrawPage()
    }
}
