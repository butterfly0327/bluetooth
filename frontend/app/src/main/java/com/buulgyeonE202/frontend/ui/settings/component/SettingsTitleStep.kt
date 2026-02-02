package com.buulgyeonE202.frontend.ui.settings.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme

@Composable
fun SettingsTitleStep(
    title: String = "설정",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 시안처럼 위쪽 공간 넉넉하게
        Spacer(modifier = Modifier.height(72.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SettingsTitleStepPreview() {
    FrontendTheme {
        SettingsTitleStep()
    }
}
