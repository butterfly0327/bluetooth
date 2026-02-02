package com.buulgyeonE202.frontend.ui.local.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme
import com.buulgyeonE202.frontend.ui.theme.White

private val CardHorizontalPadding = 24.dp

@Composable
fun LocalNetworkSettingComponent(
    onDiseaseConnectClick: () -> Unit,
    onPcConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Text(
                text = "설정",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(56.dp))
        }


        Text(
            text = "네트워크 통신 연결",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )


        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = White
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // 짐벌 연결
                LocalSettingsMenuItem(
                    text = "짐벌 연결",
                    onClick = onDiseaseConnectClick,
                    modifier = Modifier.padding(
                        start = CardHorizontalPadding,
                        end = CardHorizontalPadding
                    )
                )

                // PC 연결
                LocalSettingsMenuItem(
                    text = "PC 연결",
                    onClick = onPcConnectClick,
                    modifier = Modifier.padding(
                        start = CardHorizontalPadding,
                        end = CardHorizontalPadding
                    )
                )

                // 하단 여백 (둥근 모서리 유지)
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun LocalSettingsMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(
    name = "Local Network Setting - Light",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun LocalNetworkSettingComponentPreview() {
    FrontendTheme {
        LocalNetworkSettingComponent(
            onDiseaseConnectClick = {},
            onPcConnectClick = {}
        )
    }
}
