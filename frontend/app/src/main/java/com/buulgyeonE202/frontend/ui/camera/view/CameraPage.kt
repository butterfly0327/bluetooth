package com.buulgyeonE202.frontend.ui.camera.view

import LockScreenOrientation
import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.buulgyeonE202.frontend.ui.component.MainBottomBar
import com.buulgyeonE202.frontend.ui.camera.component.CameraControlsBar
import com.buulgyeonE202.frontend.ui.camera.component.CameraPreview
import com.buulgyeonE202.frontend.ui.camera.component.rememberCameraRecorderController
import com.buulgyeonE202.frontend.ui.camera.viewmodel.CameraIntent
import com.buulgyeonE202.frontend.ui.camera.viewmodel.CameraViewModel
import kotlinx.coroutines.delay
import android.content.pm.ActivityInfo
import rememberDeviceRotation

// AI 관련 import
import java.util.concurrent.Executors
import com.buulgyeonE202.frontend.ui.ai.AiViewModel
import com.buulgyeonE202.frontend.ui.ai.GestureRecognizerHelper
import com.buulgyeonE202.frontend.data.manager.HumanTrackingManager
import com.buulgyeonE202.frontend.ui.ai.OverlayView
import com.buulgyeonE202.frontend.ui.ai.PoseLandmarkerHelper
import com.buulgyeonE202.frontend.ui.ai.TrackingOverlayView
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.buulgyeonE202.frontend.ui.camera.model.LensFacing
import android.hardware.camera2.CameraCharacteristics // Camera2 기본 라이브러리
// CameraX와 Camera2를 연결해주는 인터럽 라이브러리
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.annotation.OptIn

// [백수연] 비트맵 디버깅용 테스트 끝나면 꼭 지울것
//import java.io.File
//import java.io.FileOutputStream

@Composable
fun CameraPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel = viewModel(),
    aiViewModel: AiViewModel = hiltViewModel(),
    humanTrackingManager: HumanTrackingManager = hiltViewModel()
) {
    val uiRotation = rememberDeviceRotation()
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val context = LocalContext.current
    val state by cameraViewModel.state.collectAsState()
    val recorderController = rememberCameraRecorderController()

    // 결과 시각화 상태
    var latestGestureResult by remember { mutableStateOf<GestureRecognizerResult?>(null) }
    var gestureImgW by remember { mutableIntStateOf(0) }
    var gestureImgH by remember { mutableIntStateOf(0) }
    var gestureRotatedImgW by remember { mutableIntStateOf(0) }
    var gestureRotatedImgH by remember { mutableIntStateOf(0) }
    var Imgrotation by remember { mutableIntStateOf(0) } // [백수연] 각도
    var gestureAppliedRotation by remember { mutableIntStateOf(0) } // 결과에 적용된 회전값
    val trackingState by humanTrackingManager.trackingState.collectAsState()

    // 룰러 UI 상태
    var showRuler by remember { mutableStateOf(false) }
    var lastInteractionTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(lastInteractionTime) {
        if (!showRuler) return@LaunchedEffect
        delay(2000L)
        if (System.currentTimeMillis() - lastInteractionTime >= 2000L) {
            showRuler = false
        }
    }
    fun triggerRuler() { showRuler = true; lastInteractionTime = System.currentTimeMillis() }

    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasAudioPermission by remember { mutableStateOf(false) }
    val isRecordingState = rememberUpdatedState(state.isRecording)

    // AI 헬퍼들 초기화
    // 제스처 인식에 사용된 회전값을 캡처 (비동기 결과와 매칭용)
    var lastSentRotation by remember { mutableIntStateOf(0) }

    val gestureRecognizerHelper = remember {
        GestureRecognizerHelper(context) { result, _, _ ->
            // 🚨 안전장치: 여기서 에러가 나면 AI 파이프라인이 멈추므로 try-catch 필수
            try {
                aiViewModel.onGestureDetected(result)
                latestGestureResult = result
                gestureAppliedRotation = lastSentRotation
            } catch (e: Exception) {
                Log.e("CameraPage", "ViewModel 전달 중 에러: ${e.message}")
            }
        }
    }

    val poseLandmarkerHelper = remember {
        PoseLandmarkerHelper(context) { result, _, _ ->
            try {
                val isFront = state.lensFacing == LensFacing.FRONT
                humanTrackingManager.onPoseDetected(result, gestureImgW, gestureImgH, lastSentRotation, isFront)
            } catch (e: Exception) {
                Log.e("CameraPage", "Tracking 전달 중 에러: ${e.message}")
            }
        }
    }

    // 센서 기반 리스너 설정
    val orientationEventListener = remember {
        object : android.view.OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                // 0, 90, 180, 270도 근처로 값을 보정 (약 45도 기준으로 끊기)
                val newRotation = when (orientation) {
                    in 45..134 -> 270 // 폰을 왼쪽으로 눕힘 (반시계)
                    in 135..224 -> 180 // 거꾸로
                    in 225..314 -> 90  // 폰을 오른쪽으로 눕힘 (시계)
                    else -> 0          // 정방향 세로
                }

                if (Imgrotation != newRotation) {
                    Imgrotation = newRotation
                }
            }
        }
    }

    // 260201. 추가-백수연 렌즈 방향에 따른 CameraInfo를 안전하게 가져오는 로직
    // ProcessCameraProvider를 통해 현재 활성화된 카메라의 정보를 찾음
    val cameraInfo = remember(state.lensFacing) {
        try {
            val provider = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context).get()
            val selector = if (state.lensFacing == LensFacing.FRONT) androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
            else androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
            selector.filter(provider.availableCameraInfos).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    // 260201. 백수연 AI 분석 쓰레드 및 분석기 수정
    // Executors.newFixedThreadPool(2) -> Executors.newSingleThreadExecutor()
    // 분석 스레드 1개만 사용, 타임스탬프 역전으로 인한 MediaPipe 분석 중단 방지
    // mediapipe는 타임이 순차적으로 찍혀야만 분석을 진행
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    // 이미지 분석기
    @OptIn(ExperimentalCamera2Interop::class) // Camera2 정보 접근을 위해 필요
    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                // SENSOR_ORIENTATION은 하드웨어 고유값이므로 분석 시작 전에 한 번만 가져옴
                val sensorOrientation = cameraInfo?.let {
                    Camera2CameraInfo.from(it).getCameraCharacteristic(CameraCharacteristics.SENSOR_ORIENTATION)
                } ?: 90

                // setAnalyzer: 들어오는 프레임마다 분석 로직을 실행시킬 객체
                setAnalyzer(analysisExecutor) { imageProxy ->
                    // 녹화 중이 아니면 기능 OFF -> 연산 자원 보호
                    if (!isRecordingState.value) {
                        // imageProxy: 카메라 렌즈를 통해 들어온 이미지 데이터의 정보(픽셀, 회전 각도, 시간 정보 등)을 담은 객체
                        // 분석 후나 쓰이지 않을 때 close() 안해주면 카메라는 다음 프레임 안찍고 기다리게됨 -> 카메라 프리징 원인
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    // 비트맵 참조 변수 선언
                    // 아직 Bitmap 생성 X -> toBitmap() 호출해야 생성
                    // 어떤 error 상황에서 finally 블록에서 recycle() 호출, 메모리 누수 방지
                    var originalBitmap: Bitmap? = null
                    var bitmapToProcess: Bitmap? = null

                    try {
                        originalBitmap = imageProxy.toBitmap()
//                        saveBitmapForDebug(context, originalBitmap!!, "original")

                        // AI 모델이 학습한 이미지이자, 실제 사용자가 보고 있는 화면에 맞춰서 회전
                        // 센서가 돌아간 상태로 내장되어 있어서 originalBitmap 자체가 돌아가서 출력되기 때문
                        val targetRotation = (sensorOrientation - Imgrotation + 360) % 360

                        // 각도에 맞는 새로운 비트맵 생성
                        val matrix = Matrix().apply { postRotate(targetRotation.toFloat())}
                        bitmapToProcess = Bitmap.createBitmap(
                            originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
                        )
//                        saveBitmapForDebug(context, bitmapToProcess!!, "processed")

                        // 사용이 끝난 원본 비트맵 즉시 해제 (LOS 메모리 폭발 방지)
                        // 회전한 bitmapToProcess만 쓸 것이므로 바로 해제
                        originalBitmap.recycle()
                        originalBitmap = null

                        // 시각화용 사이즈 업데이트
                        // Mediapipe는 좌표를 0.0 ~ 1.0 사이의 비율 값으로 보내주므로 실제 화면에 맞추기 위한 너비, 높이 업데이트
                        gestureImgW = bitmapToProcess.width
                        gestureImgH = bitmapToProcess.height

                        // 비트맵 자체 회전 제거, 현재 각도만 보냄
                        lastSentRotation = Imgrotation // 똑바로 세웠을 때 0도, 반시계 방향 회전 90, 180, 270

                        // 동일한 비트맵 공유
                        gestureRecognizerHelper.recognizeBitmap(bitmapToProcess)
                        poseLandmarkerHelper.recognizeBitmap(bitmapToProcess)
                    }
                    catch (e: Exception) {
                        Log.e("CameraPage", "이미지 처리 실패: ${e.message}")
                    }
                    finally {
                        // 사용 완료된 비트맵 명시적 해제 및 카메라 해방
                        bitmapToProcess?.recycle()
                        originalBitmap?.recycle()
                        imageProxy.close()
                    }
                }
            }
    }

    LaunchedEffect(Unit) { humanTrackingManager.connectBluetooth() }
    DisposableEffect(Unit) {
        orientationEventListener.enable() // 리스너 시작
        onDispose {
            gestureRecognizerHelper.clear()
            poseLandmarkerHelper.clear()
            aiViewModel.reset()
            humanTrackingManager.reset()
            analysisExecutor.shutdown()
            orientationEventListener.disable() // 리스너 종료
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        hasCameraPermission = it[Manifest.permission.CAMERA] == true
        hasAudioPermission = it[Manifest.permission.RECORD_AUDIO] == true
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            MainBottomBar(
                currentRoute = "camera",
                onNavigate = { route ->
                    if (route != "camera") navController.navigate(route) {
                        popUpTo("gesture_home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                uiRotation = uiRotation // 여기서 각도를 넘겨줌
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // 핀치 줌 발생 시 -> triggerRuler() 호출하여 룰러 켜기
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        cameraViewModel.onIntent(CameraIntent.OnZoomPinch(zoom))
                        triggerRuler()
                    }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    lensFacing = state.lensFacing,
                    zoomRatio = state.targetZoom,
                    recorderController = recorderController,
                    imageAnalysis = imageAnalyzer,
                    onZoomBoundsReady = { l, min, max, c -> cameraViewModel.onIntent(CameraIntent.OnZoomBoundsReady(l, min, max, c)) },
                    onAppliedZoomChanged = { cameraViewModel.onIntent(CameraIntent.OnAppliedZoomChanged(it)) },
                    onRecordingFinalize = { cameraViewModel.onIntent(CameraIntent.OnRecordingFinalize) },
                    onVideoSaved = { cameraViewModel.onIntent(CameraIntent.OnVideoSaved(it)) },
                    onRecordingError = { cameraViewModel.onIntent(CameraIntent.OnRecordingError(it)) }
                )

                AndroidView(
                    factory = { ctx -> OverlayView(ctx, null) },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        latestGestureResult?.let {
                            val isFront = state.lensFacing == LensFacing.FRONT
                            view.setResults(it, isFront, gestureImgW, gestureImgH, gestureAppliedRotation)
//                            view.setResults(it, isFront, gestureImgW, gestureImgH, gestureAppliedRotation, gestureRotatedImgW, gestureRotatedImgH)
                        }
                    }
                )
                AndroidView(
                    factory = { ctx -> TrackingOverlayView(ctx, null) },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        val isFront = state.lensFacing == LensFacing.FRONT
                        view.setResults(trackingState, isFront, Imgrotation)
                    }
                )
            }

            CameraControlsBar(
                isRecording = state.isRecording,
                currentLens = state.lensFacing,
                selectedZoom = state.targetZoom,
                minZoom = state.minZoom,
                maxZoom = state.maxZoom,
                // UI 회전 감지
                uiRotation = uiRotation,

                // 상태 및 콜백 전달
                showRuler = showRuler,
                onRulerInteraction = { triggerRuler() },
                onRequestShowRuler = { triggerRuler() },
                onSwitchLens = { cameraViewModel.onIntent(CameraIntent.OnSwitchLens) },
                onSelectZoom = { cameraViewModel.onIntent(CameraIntent.OnSelectZoom(it)) },
                onToggleRecord = {
                    if (!hasAudioPermission) permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                    else if (state.isRecording) {
                        recorderController.stopRecording()
                        cameraViewModel.onIntent(CameraIntent.OnStopRecording)
                    } else {
                        recorderController.startRecording()
                        cameraViewModel.onIntent(CameraIntent.OnStartRecording)
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}

//// [백수연] 비트맵 디버깅용 나중에 꼭 지울 것
//fun saveBitmapForDebug(context: android.content.Context, bitmap: Bitmap, fileName: String) {
//    val file = File(context.cacheDir, "$fileName.jpg")
//    try {
//        val out = FileOutputStream(file)
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//        out.flush()
//        out.close()
//        Log.d("DebugImage", "이미지 저장 완료: ${file.absolutePath}")
//    } catch (e: Exception) {
//        Log.e("DebugImage", "저장 실패: ${e.message}")
//    }
//}