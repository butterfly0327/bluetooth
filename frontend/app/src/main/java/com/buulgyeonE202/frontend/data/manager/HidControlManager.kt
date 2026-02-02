package com.buulgyeonE202.frontend.data.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class HidControlManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "HidControlManager"
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var hidDevice: BluetoothHidDevice? = null
    private var connectedDevice: BluetoothDevice? = null

    // ★ 원래 폰 이름 저장용 변수
    private var originalDeviceName: String? = null

    // ★ 우리가 원하는 장비 이름
    private val CUSTOM_DEVICE_NAME = "E202 Controller"

    private val _connectionState = MutableStateFlow(false)
    val connectionState = _connectionState.asStateFlow()

    private val hidDescriptor = byteArrayOf(
        0x05, 0x01, 0x09, 0x06, 0xA1.toByte(), 0x01, 0x05, 0x07, 0x19, 0xE0.toByte(),
        0x29, 0xE7.toByte(), 0x15, 0x00, 0x25, 0x01, 0x75, 0x01, 0x95.toByte(), 0x08,
        0x81.toByte(), 0x02, 0x95.toByte(), 0x01, 0x75, 0x08, 0x81.toByte(), 0x01,
        0x95.toByte(), 0x05, 0x75, 0x01, 0x05, 0x08, 0x19, 0x01, 0x29, 0x05,
        0x91.toByte(), 0x02, 0x95.toByte(), 0x01, 0x75, 0x03, 0x91.toByte(), 0x01,
        0x95.toByte(), 0x06, 0x75, 0x08, 0x15, 0x00, 0x25, 0x65, 0x05, 0x07,
        0x19, 0x00, 0x29, 0x65, 0x81.toByte(), 0x00, 0xC0.toByte()
    )

    private val sdpSettings = BluetoothHidDeviceAppSdpSettings(
        "E202 Controller", "Gesture Control Keyboard", "BuulgyeonE202",
        BluetoothHidDevice.SUBCLASS1_KEYBOARD, hidDescriptor
    )

    @SuppressLint("MissingPermission")
    fun initialize() {
        if (bluetoothAdapter == null) return

        // 이름 변경
        changeBluetoothName()

        // ★ 검색 가능 모드로 설정 (300초)
        makeDiscoverable()

        bluetoothAdapter?.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.HID_DEVICE) {
                    hidDevice = proxy as BluetoothHidDevice
                    Log.d(TAG, "HID 프로필 연결됨")

                    hidDevice?.registerApp(sdpSettings, null, null, Executors.newCachedThreadPool(), object : BluetoothHidDevice.Callback() {
                        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                            Log.d(TAG, "HID 앱 등록 상태: $registered, pluggedDevice: ${pluggedDevice?.name}")
                            if (registered) {
                                Log.d(TAG, "★ HID 장치가 등록되어 검색 가능합니다!")
                                // ★ 이미 연결된 장치가 있으면 바로 연결 상태 반영
                                if (pluggedDevice != null) {
                                    connectedDevice = pluggedDevice
                                    _connectionState.value = true
                                    Log.d(TAG, "★ 이미 연결된 장치 감지: ${pluggedDevice.name}")
                                }
                            }
                        }

                        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
                            when (state) {
                                BluetoothProfile.STATE_CONNECTED -> {
                                    connectedDevice = device
                                    _connectionState.value = true
                                    Log.d(TAG, "PC 연결됨: ${device?.name}")
                                }
                                BluetoothProfile.STATE_CONNECTING -> {
                                    Log.d(TAG, "PC 연결 중...")
                                }
                                BluetoothProfile.STATE_DISCONNECTED -> {
                                    Log.d(TAG, "PC 연결 해제됨 - 재연결 시도")
                                    connectedDevice = null
                                    _connectionState.value = false
                                    // ★ 자동 재연결: 3초 후 다시 연결 시도
                                    if (device != null) {
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            try {
                                                hidDevice?.connect(device)
                                                Log.d(TAG, "재연결 시도: ${device.name}")
                                            } catch (e: Exception) {
                                                Log.e(TAG, "재연결 실패: ${e.message}")
                                            }
                                        }, 3000)
                                    }
                                }
                                BluetoothProfile.STATE_DISCONNECTING -> {
                                    Log.d(TAG, "PC 연결 해제 중...")
                                }
                            }
                        }
                    })
                }
            }
            override fun onServiceDisconnected(profile: Int) {
                hidDevice = null
                _connectionState.value = false
                Log.d(TAG, "HID 프로필 연결 해제됨")
            }
        }, BluetoothProfile.HID_DEVICE)
    }

    @SuppressLint("MissingPermission")
    private fun changeBluetoothName() {
        if (bluetoothAdapter?.name != CUSTOM_DEVICE_NAME) {
            originalDeviceName = bluetoothAdapter?.name
            bluetoothAdapter?.name = CUSTOM_DEVICE_NAME
            Log.d(TAG, "블루투스 이름을 변경했습니다: $CUSTOM_DEVICE_NAME")
        }
    }

    @SuppressLint("MissingPermission")
    private fun makeDiscoverable() {
        if (bluetoothAdapter?.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoverableIntent = android.content.Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(discoverableIntent)
            Log.d(TAG, "검색 가능 모드 요청")
        }
    }

    @SuppressLint("MissingPermission")
    fun sendKey(keyCode: Byte, modifier: Byte = 0) {
        val device = connectedDevice ?: return
        hidDevice?.sendReport(device, 0, byteArrayOf(modifier, 0, keyCode, 0, 0, 0, 0, 0))
        hidDevice?.sendReport(device, 0, byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0))
    }

    @SuppressLint("MissingPermission")
    fun sendKeys(keyCodes: List<Byte>, modifier: Byte = 0) {
        val device = connectedDevice ?: return

        // 최대 6개 키까지 지원
        val report = ByteArray(8)
        report[0] = modifier  // Shift, Ctrl 등
        report[1] = 0         // 예약

        keyCodes.take(6).forEachIndexed { index, keyCode ->
            report[2 + index] = keyCode
        }

        hidDevice?.sendReport(device, 0, report)
        hidDevice?.sendReport(device, 0, ByteArray(8))  // 키 릴리즈
    }


    // ★ [추가] 앱 꺼질 때 원래 이름 복구하는 함수
    @SuppressLint("MissingPermission")
    fun restoreName() {
        if (originalDeviceName != null && bluetoothAdapter != null) {
            bluetoothAdapter?.name = originalDeviceName
            Log.d(TAG, "블루투스 이름을 복구했습니다: $originalDeviceName")
        }
    }
}