package com.buulgyeonE202.frontend.ui.remote.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buulgyeonE202.frontend.ui.remote.viewmodel.RemoteControlViewModel

@Composable
fun BluetoothTestScreen(
    viewModel: RemoteControlViewModel = hiltViewModel(),
    onBackClick: () -> Unit // 뒤로가기 버튼용
) {
    val status by viewModel.statusText.collectAsState()

    // 슬라이더 상태 값 (기본값 중앙 0.5)
    var xValue by remember { mutableFloatStateOf(0.5f) }
    var yValue by remember { mutableFloatStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. 상태 표시 및 버튼
        Text(text = "상태: $status", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Button(onClick = { viewModel.connect() }) {
                Text("짐벌 연결")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = { viewModel.disconnect() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("연결 해제")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 2. X축 조절 (좌우)
        Text(text = "Pan (좌우 제어 전용): ${String.format("%.2f", xValue)}")
        Slider(
            value = xValue,
            onValueChange = { newValue ->
                xValue = newValue
                viewModel.sendPanOnly(xValue) // 좌우 전용 함수 호출
            },
            valueRange = 0f..1f
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onBackClick) {
            Text("메인으로 돌아가기")
        }
    }
}