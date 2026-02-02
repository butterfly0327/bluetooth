# bluetooth

## 목표
앱(휴대폰) ↔ 라즈베리파이(Pi)는 **일반 Bluetooth Classic(RFCOMM/SPP)** 로 자동 연결하고,  
PC ↔ 앱(휴대폰)은 **HID** 로 연결되도록 분리합니다.

## 문제 해결 방식 (기술 명세)
1. **Android 앱 → Pi 자동 연결**
   - `BluetoothAdapter.getRemoteDevice(MAC)` + **RFCOMM 채널 연결**(`createInsecureRfcommSocket`) 사용.
   - 앱 실행 시 Bluetooth 권한이 있으면 **자동으로 Pi에 연결**을 시도합니다.
2. **Pi → RFCOMM 서버 유지**
   - Pi는 RFCOMM 서버로 대기하며, 앱에서 연결하면 바로 수신 루프가 돌도록 구성했습니다.
3. **HID는 PC 전용**
   - HID 매니저는 그대로 유지하지만, Pi 연결은 **RFCOMM 전용**으로 분리했습니다.

## 환경에 맞게 수정해야 하는 부분
아래 값들은 **본인 환경에 맞게 반드시 수정**해야 합니다.

### 1) Pi 블루투스 MAC 주소 (앱)
파일: `frontend/app/src/main/java/com/buulgyeonE202/frontend/data/manager/BluetoothManager.kt`
- `TARGET_DEVICE_ADDRESS`: **Pi의 Bluetooth MAC 주소**
  - Pi에서 확인: `bluetoothctl show` 또는 `hciconfig`로 MAC 확인
- `TARGET_RFCOMM_CHANNEL`: **Pi가 열어둔 RFCOMM 채널 번호** (기본 1)
- `TARGET_DEVICE_NAME`: (선택) **MAC 대신 이름으로 찾고 싶을 때** 사용되는 백업 값

### 2) Pi RFCOMM 서버 바인딩 주소/포트
파일: `Pi_Gimbal_Tracker/config.py`
- `BT_BIND_ADDRESS`
  - `""`(빈 문자열) → Pi의 모든 Bluetooth 어댑터에서 수신
  - 특정 어댑터만 쓰려면 **Pi의 MAC 주소**를 넣으세요.
- `BT_PORT`
  - RFCOMM 채널 번호 (앱의 `TARGET_RFCOMM_CHANNEL`과 **같아야 함**)

## 변경한 파일 위치
아래 파일이 수정되었습니다.
- `frontend/app/src/main/java/com/buulgyeonE202/frontend/data/manager/BluetoothManager.kt`
- `frontend/app/src/main/java/com/buulgyeonE202/frontend/ui/MainActivity.kt`
- `Pi_Gimbal_Tracker/models/bt_receiver.py`
- `Pi_Gimbal_Tracker/config.py`

## 운영 팁
- **Pi 연결은 앱에서만 수행**하세요. (휴대폰 Bluetooth 설정에서 Pi를 직접 연결하면 HID로 잡히는 경우가 있음)
- 기존에 잘못 페어링된 기록이 있다면 **휴대폰/라즈베리파이에서 페어링 해제 후** 앱으로 다시 연결하세요.
