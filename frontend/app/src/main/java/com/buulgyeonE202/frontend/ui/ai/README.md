> 나중에 삭제하겠습니다

# 🤖 S24 실시간 제스처 인식 모듈 (AI-Module)

본 모듈은 **Samsung Galaxy S24**의 하드웨어 성능을 극대화하여, 실시간 카메라 스트림으로부터 사용자 제스처 및 기하학적 동작을 분석하고 발표 제어 신호(Action ID)를 생성합니다. 

기존의 독립적인 Fragment 구조 대신, 프론트엔드 팀의 **`CameraPreview`**에 직접 통합되어 자원을 효율적으로 관리하는 '플러그인' 방식으로 설계되었습니다.

---

### 📂 폴더 및 파일 역할

#### 1. `ui/ai/GestureRecognizerHelper.kt` (Engine)
- **역할**: MediaPipe 런타임 관리 및 실시간 추론 수행
- **S24 최적화**: GPU Delegate를 활용하여 추론 지연 시간을 최소화하며, `LIVE_STREAM` 모드로 끊김 없는 분석을 지원합니다.
- **데이터 공급**: `CameraPreview.kt`로부터 `ImageProxy`를 전달받아 처리합니다.

#### 2. `ui/ai/AiViewModel.kt` (State & Business Logic)
- **데이터 정제**: MediaPipe로부터 전달받은 Raw 데이터를 **5프레임 최빈값(Smoothing)** 로직을 통해 정제합니다.
- **통신**: 확정된 `Action ID`를 `BluetoothManager`를 통해 수신 기기(PC)로 즉시 전송합니다.
- **상태 관리**: 중복 신호 전송 방지 및 인식 로그(`gestureLog`) 관리를 담당합니다.

#### 3. `ui/ai/OverlayView.kt` (Visualization)
- **역할**: 카메라 화면 위에 손의 21개 랜드마크와 연결선을 실시간으로 렌더링합니다.
- **디버깅**: 현재 계산된 거리값, 각도 변화 등을 텍스트로 표시하여 개발 단계의 정밀한 튜닝을 돕습니다.

#### 4. `utils/GestureClassifier.kt` (Geometric Math)
- **역할**: 파이썬으로 선검증된 기하학적 판정 알고리즘의 Kotlin 이식판입니다.
- **젓가락 제스처**: 검지(8)와 중지(12) 사이의 유클리드 거리를 측정합니다.
- **다이얼 회전**: 손목(0)-검지(8) 벡터의 프레임 간 각도 변화량($\Delta\theta$)을 계산합니다.

---

### 🔄 데이터 파이프라인 (Data Flow)

1. **획득**: S24 카메라 뷰(`CameraPreview`)가 켜지면 `ImageAnalysis` 유즈케이스 활성화
2. **추론**: `GestureRecognizerHelper`가 프레임을 분석하여 랜드마크 좌표 추출
3. **연산**: `GestureClassifier`가 추출된 좌표로 거리 및 회전량 계산
4. **정제**: `AiViewModel`이 5프레임 스무딩을 거쳐 최종 제스처 확정
5. **명령**: Bluetooth를 통해 확정된 `Action ID` 문자열 전송

---

### 💾 관련 리소스 (Assets)
- **위치**: `app/src/main/assets/`
- **모델**: `gesture_recognizer.task` (Hand Tracking & Gesture Labeling)

---

### ⚠️ 주의사항
- 본 모듈은 단독 실행되지 않으며, `CameraPage.kt` 내에서 `CameraPreview`와 함께 초기화되어야 합니다.
- 성능 및 배터리 효율을 위해 카메라 프리뷰가 백그라운드로 전환될 경우 AI 분석 또한 즉시 중단됩니다.