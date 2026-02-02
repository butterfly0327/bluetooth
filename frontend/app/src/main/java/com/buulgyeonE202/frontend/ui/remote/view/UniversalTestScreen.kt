package com.buulgyeonE202.frontend.ui.remote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buulgyeonE202.frontend.ui.remote.viewmodel.UniversalControlViewModel

@Composable
fun UniversalTestScreen(
    viewModel: UniversalControlViewModel = hiltViewModel()
) {
    val isPcConnected by viewModel.pcConnected.collectAsState(initial = false)
    val isPiConnected by viewModel.piConnected.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.connectAll()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🚀 통합 제어 센터", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        // 상태 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusIndicator("💻 PC (HID)", isPcConnected)
            StatusIndicator("🍓 Gimbal (Pi)", isPiConnected)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // PC 제어 섹션
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💻 PC Controls", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.pcNextSlide() }, modifier = Modifier.weight(1f)) {
                        Text("다음 장")
                    }
                    Button(onClick = { viewModel.startPptFromBeginning() }, modifier = Modifier.weight(1f)) {
                        Text("처음부터")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pi 제어 섹션 (각도 버튼 3개)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🍓 Pi Controls (Pan Angle)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // 👇 [수정] 버튼 3개를 가로로 배치
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // 버튼 사이 간격
                ) {
                    Button(
                        onClick = { viewModel.gimbalAngleZero() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
                    ) {
                        Text("0°")
                    }

                    Button(
                        onClick = { viewModel.gimbalAngleNinety() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("90°")
                    }

                    Button(
                        onClick = { viewModel.gimbalAngleOneEighty() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF018786))
                    ) {
                        Text("180°")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 동시 제어
        Button(
            onClick = { viewModel.startPresentation() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("🔥 동시 실행 (발표 모드)")
        }
    }
}

@Composable
fun StatusIndicator(name: String, isConnected: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(if (isConnected) Color.Green else Color.Red, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, style = MaterialTheme.typography.bodyMedium)
    }
}