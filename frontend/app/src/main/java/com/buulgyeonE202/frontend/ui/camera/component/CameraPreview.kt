package com.buulgyeonE202.frontend.ui.camera.component

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis // 백수연 추가
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.buulgyeonE202.frontend.ui.camera.CameraConstants
import com.buulgyeonE202.frontend.ui.camera.model.LensFacing // ✅ Enum Import 필수

// Controller 코드는 그대로 유지
class CameraRecorderController {
    internal var start: (() -> Unit)? = null
    internal var stop: (() -> Unit)? = null
    fun startRecording() = start?.invoke()
    fun stopRecording() = stop?.invoke()
}

@Composable
fun rememberCameraRecorderController(): CameraRecorderController =
    remember { CameraRecorderController() }

@SuppressLint("MissingPermission")
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    // Int 대신 Enum을 직접 받음
    lensFacing: LensFacing,
    zoomRatio: Float,
    recorderController: CameraRecorderController,

    // [백수연]추가: AI 분석용 ImageAnalysis (nullable)
    imageAnalysis: ImageAnalysis? = null,

    // 콜백도 Enum을 돌려줌 (Int -> Enum 변환 과정 삭제)
    onZoomBoundsReady: (lens: LensFacing, min: Float, max: Float, current: Float) -> Unit,

    onAppliedZoomChanged: (current: Float) -> Unit,
    onVideoSaved: (Uri) -> Unit,
    onRecordingError: (Throwable) -> Unit,
    onRecordingFinalize: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        androidx.camera.view.PreviewView(context).apply {
            implementationMode = androidx.camera.view.PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    var camera: Camera? by remember { mutableStateOf(null) }
    var videoCapture: VideoCapture<Recorder>? by remember { mutableStateOf(null) }
    var recording: Recording? by remember { mutableStateOf(null) }

    // Enum 자체가 Key가 됨
    DisposableEffect(lensFacing) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        // 1. 옵저버: 내가 받은 lensFacing(Enum)을 그대로 꼬리표로 붙여서 보냄
        val zoomObserver = androidx.lifecycle.Observer<androidx.camera.core.ZoomState> { state ->
            onZoomBoundsReady(lensFacing, state.minZoomRatio, state.maxZoomRatio, state.zoomRatio)
        }

        providerFuture.addListener({
            runCatching {
                val provider = providerFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val recorder = Recorder.Builder().build()
                val vc = VideoCapture.withOutput(recorder)

                // 🚨 [수정 3] 여기서만 .value(Int)를 꺼내서 카메라 선택
                val selector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing.value)
                    .build()

                provider.unbindAll()

                // [백수연] 원래 코드 스타일 유지하면서 ImageAnalysis 추가
                val boundCamera = if (imageAnalysis != null) {
                    provider.bindToLifecycle(lifecycleOwner, selector, preview, vc, imageAnalysis)
                } else {
                    provider.bindToLifecycle(lifecycleOwner, selector, preview, vc)
                }

                camera = boundCamera
                videoCapture = vc

                boundCamera.cameraInfo.zoomState.observe(lifecycleOwner, zoomObserver)
                boundCamera.cameraControl.setZoomRatio(zoomRatio)

            }.onFailure { onRecordingError(it) }
        }, executor)

        onDispose {
            runCatching {
                val provider = providerFuture.get()
                provider.unbindAll()
                camera?.cameraInfo?.zoomState?.removeObserver(zoomObserver)
            }
            camera = null
            videoCapture = null
            recording = null
        }
    }

    LaunchedEffect(zoomRatio, camera) {
        val cam = camera ?: return@LaunchedEffect

        // 1. 현재 카메라의 줌 정보 가져오기 (없으면 기본값 1.0 ~ 10.0으로 가정)
        val zs = cam.cameraInfo.zoomState.value
        val min = zs?.minZoomRatio ?: 1f
        val max = zs?.maxZoomRatio ?: 10f

        // 2. 안전하게 범위 자르기
        val target = zoomRatio.coerceIn(min, max)

        // 3. 로그 찍기 (Logcat에서 "CameraZoom" 검색해보세요!)
        android.util.Log.d("CameraZoom", "명령 전송: $target (범위: $min ~ $max)")

        // 4. 카메라 하드웨어에 명령 전송 (Future 리스너 추가)
        cam.cameraControl.setZoomRatio(target).addListener({
            try {
                android.util.Log.d("CameraZoom", "성공: 줌 변경 완료")
            } catch (e: Exception) {
                android.util.Log.e("CameraZoom", "실패: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))

        // 5. UI 업데이트 알림
        onAppliedZoomChanged(target)
    }

    LaunchedEffect(videoCapture) {
        recorderController.start = start@{
            val vc = videoCapture
            if (vc == null) {
                onRecordingError(IllegalStateException("VideoCapture not ready"))
                onRecordingFinalize()
                return@start
            }
            val name = "VID_${System.currentTimeMillis()}.mp4"
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, name)
                put(MediaStore.Video.Media.MIME_TYPE, CameraConstants.VIDEO_MIME_TYPE)
                put(MediaStore.Video.Media.RELATIVE_PATH, CameraConstants.VIDEO_RELATIVE_PATH)
            }
            val outputOptions = MediaStoreOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            ).setContentValues(values).build()

            try {
                recording = vc.output
                    .prepareRecording(context, outputOptions)
                    .withAudioEnabled()
                    .start(ContextCompat.getMainExecutor(context)) { event ->
                        if (event is VideoRecordEvent.Finalize) {
                            try {
                                if (!event.hasError()) onVideoSaved(event.outputResults.outputUri)
                            } finally {
                                recording = null
                                onRecordingFinalize()
                            }
                        }
                    }
            } catch (t: Throwable) {
                recording = null
                onRecordingError(t)
                onRecordingFinalize()
            }
        }
        recorderController.stop = {
            try { recording?.stop() } catch (t: Throwable) { onRecordingError(t) }
        }
    }

    AndroidView(modifier = modifier, factory = { previewView })
}