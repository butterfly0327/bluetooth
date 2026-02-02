package com.buulgyeonE202.frontend.ui.remote.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buulgyeonE202.frontend.ui.remote.viewmodel.PcControlViewModel

@Composable
fun PcRemoteTestScreen(
    viewModel: PcControlViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PC 리모컨 테스트", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = { viewModel.nextSlide() }) {
            Text(text = "PPT 다음 장 넘기기 ▶")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { viewModel.toggleMedia() }) {
            Text(text = "영상 재생 / 정지 ⏯")
        }
    }
}