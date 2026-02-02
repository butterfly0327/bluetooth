package com.buulgyeonE202.frontend.data.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothManager @Inject constructor() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // ★ 실시간 연결 상태 방송국
    private val _connectionState = MutableStateFlow(false)
    val connectionState = _connectionState.asStateFlow()

    private val TARGET_DEVICE_NAME = "e202-desktop" // 라즈베리파이 호스트네임 (백업용)
    private val TARGET_DEVICE_ADDRESS = "2C:CF:67:6A:DD:87" // 라즈베리파이 블루투스 MAC
    private val TARGET_RFCOMM_CHANNEL = 1

    val isConnected: Boolean
        get() = socket?.isConnected == true

    @SuppressLint("MissingPermission")
    suspend fun connectToPi(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return@withContext false

                val device = if (TARGET_DEVICE_ADDRESS.isNotBlank()) {
                    bluetoothAdapter.getRemoteDevice(TARGET_DEVICE_ADDRESS)
                } else {
                    bluetoothAdapter.bondedDevices.find {
                        it.name?.equals(TARGET_DEVICE_NAME, ignoreCase = true) == true
                    }
                }
                if (device == null) return@withContext false

                bluetoothAdapter.cancelDiscovery()
                Log.d("BluetoothManager", "RFCOMM 채널 ${TARGET_RFCOMM_CHANNEL} 연결 시도")
                // ★ 리플렉션으로 채널 강제 연결 (RFCOMM)
                val method = device.javaClass.getMethod("createInsecureRfcommSocket", Int::class.javaPrimitiveType)
                socket = method.invoke(device, TARGET_RFCOMM_CHANNEL) as BluetoothSocket

                socket?.connect()
                outputStream = socket?.outputStream

                _connectionState.value = true // 🟢 연결 성공
                Log.d("BluetoothManager", "연결 성공!")
                true
            } catch (e: Exception) {
                Log.e("BluetoothManager", "연결 실패: ${e.message}")
                disconnect()
                false
            }
        }
    }

    suspend fun sendCoordinates(jsonString: String) {
        withContext(Dispatchers.IO) {
            try {
                if (outputStream == null || socket?.isConnected == false) return@withContext
//                val jsonString = "{\"x\":$x,\"y\":$y}\n"
                val messageToSend = "$jsonString\n"
                outputStream?.write(messageToSend.toByteArray())
                outputStream?.flush()
            } catch (e: IOException) {
                Log.e("BluetoothManager", "전송 중 오류 발생: ${e.message}")
                disconnect()
            }
        }
    }

    // [백수연] RemoteControlViewModel, UniversalControlViewModel에서 사용
    suspend fun sendCoordinates_fix(x: Float, y: Float) {
        withContext(Dispatchers.IO) {
            try {
                if (outputStream == null || socket?.isConnected == false) return@withContext
                val jsonString = "{\"x\":$x,\"y\":$y}\n"
                outputStream?.write(jsonString.toByteArray())
                outputStream?.flush()
            } catch (e: IOException) {
                disconnect()
            }
        }
    }

    // 26.01.26 백수연(AI) 제스처 액션 전송 기능 추가
    suspend fun sendAction(action: String) {
        withContext(Dispatchers.IO) {
            try {
                // 연결 상태 확인: 스트림이 없거나 소켓이 끊겼으면 중단
                if (outputStream == null || socket?.isConnected == false) return@withContext

                // 메시지 끝에 줄바꿈(\n)을 추가하여 수신측(라즈베리파이 등)에서 데이터의 끝을 알 수 있게 함
                val message = "$action\n"
                outputStream?.write(message.toByteArray())
                outputStream?.flush() // 버퍼 비우기

                Log.d("BluetoothManager", "액션 전송 성공: $action")
            } catch (e: IOException) {
                Log.e("BluetoothManager", "액션 전송 실패: ${e.message}")
                disconnect() // 통신 에러 발생 시 안전하게 연결 해제
            }
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        socket = null
        outputStream = null
        _connectionState.value = false // 🔴 연결 끊김
    }

}
