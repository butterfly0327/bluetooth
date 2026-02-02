// ui/MainActivity.kt

package com.buulgyeonE202.frontend.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.navArgument

import com.buulgyeonE202.frontend.data.manager.BluetoothManager
import com.buulgyeonE202.frontend.data.manager.HidControlManager
import com.buulgyeonE202.frontend.data.manager.TokenManager

import com.buulgyeonE202.frontend.ui.auth.view.LoginScreen
import com.buulgyeonE202.frontend.ui.auth.view.SignupScreen
import com.buulgyeonE202.frontend.ui.auth.model.AuthFlowType
import com.buulgyeonE202.frontend.ui.auth.model.AuthStep
import com.buulgyeonE202.frontend.ui.auth.view.AuthCodePage
import com.buulgyeonE202.frontend.ui.auth.view.AuthEmailPage
import com.buulgyeonE202.frontend.ui.auth.view.PasswordResetPage
import com.buulgyeonE202.frontend.ui.auth.viewmodel.AuthFlowViewModel
import com.buulgyeonE202.frontend.ui.auth.viewmodel.AuthViewModel

import com.buulgyeonE202.frontend.ui.camera.view.CameraPage

import com.buulgyeonE202.frontend.ui.gesture.view.GestureActionSelectionScreen
import com.buulgyeonE202.frontend.ui.gesture.view.GestureAssignmentScreen
import com.buulgyeonE202.frontend.ui.gesture.view.GestureDetailScreen
import com.buulgyeonE202.frontend.ui.gesture.view.GestureHomeScreen
import com.buulgyeonE202.frontend.ui.gesture.view.GestureNameEditScreen

import com.buulgyeonE202.frontend.ui.settings.view.SettingsPage
import com.buulgyeonE202.frontend.ui.settings.view.PasswordChangePage
import com.buulgyeonE202.frontend.ui.settings.view.WithdrawPage
import com.buulgyeonE202.frontend.ui.theme.FrontendTheme

import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

// 26.01.26 백수연(AI) 테스트용 CameraPage 임포트 추가
import com.buulgyeonE202.frontend.ui.camera.view.CameraPage

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var hidControlManager: HidControlManager
    @Inject lateinit var bluetoothManager: BluetoothManager
    @Inject lateinit var tokenManager: TokenManager

    private val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            try { hidControlManager.initialize() } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(bluetoothPermissions)
        try { hidControlManager.initialize() } catch (e: Exception) { e.printStackTrace() }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FrontendTheme {
                // =================================================================
                // 앱 실행 시 카메라/오디오 권한 즉시 요청 로직
                // =================================================================
                val context = LocalContext.current
                val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    // 권한 결과 처리
                    val allGranted = result.values.all { it }
                    if (!allGranted) {
                        // 권한 거부 시 처리 로직 (필요시 추가)
                    }
                }

                LaunchedEffect(Unit) {
                    val hasPermissions = permissions.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }
                    if (!hasPermissions) {
                        launcher.launch(permissions)
                    }
                }
                // =================================================================
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val navController = rememberNavController()
                    val authFlowViewModel: AuthFlowViewModel = hiltViewModel()

                    NavHost(navController = navController, startDestination = "login") {

                        // ==========================================
                        // 1. 기본 화면
                        // ==========================================
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onFindPasswordClick = {
                                    // 비밀번호 찾기 클릭 -> 이메일 입력 화면으로
                                    authFlowViewModel.initFlow(AuthFlowType.RESET_PASSWORD)
                                    navController.navigate("find_password_email")
                                }
                            )
                        }

                        composable("signup") { SignupScreen(navController = navController) }
                        composable("gesture_home") { GestureHomeScreen(navController = navController) }
                        composable("camera") { CameraPage(navController = navController) }

                        // ==========================================
                        // 2. 비밀번호 찾기 (비로그인 상태)
                        // ==========================================

                        // (1) 이메일 입력
                        composable("find_password_email") {
                            val uiState by authFlowViewModel.uiState.collectAsState()

                            AuthEmailPage(
                                flowType = AuthFlowType.RESET_PASSWORD,
                                email = uiState.email,
                                onEmailChange = { authFlowViewModel.onEmailChange(it) },
                                emailError = uiState.emailError,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    authFlowViewModel.onPrimaryClick() // 이메일 전송 API
                                    navController.navigate("find_password_code")
                                }
                            )
                        }

                        // (2) 인증코드 입력
                        composable("find_password_code") {
                            val uiState by authFlowViewModel.uiState.collectAsState()

                            AuthCodePage(
                                flowType = AuthFlowType.RESET_PASSWORD,
                                code = uiState.code,
                                onCodeChange = { authFlowViewModel.onCodeChange(it) },
                                secondsLeft = uiState.secondsLeft,
                                codeError = uiState.codeError,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    authFlowViewModel.onPrimaryClick() // 코드 검증 API
                                }
                            )

                            // 검증 완료되면 재설정 페이지로 이동
                            LaunchedEffect(uiState.step) {
                                if (uiState.step == AuthStep.PASSWORD) {
                                    navController.navigate("find_password_reset") {
                                        popUpTo("find_password_code") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // (3) 비밀번호 재설정 (현재 비번 입력 X)
                        composable("find_password_reset") {
                            val uiState by authFlowViewModel.uiState.collectAsState()

                            PasswordResetPage(
                                onBack = { navController.popBackStack() },
                                onSubmit = { newPw, confirmPw ->
                                    if (newPw != confirmPw) {
                                        Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // 현재 비밀번호 없이 변경 요청
                                        authFlowViewModel.onPasswordChange(newPw)
                                        authFlowViewModel.onPasswordConfirmChange(confirmPw)
                                        authFlowViewModel.onPrimaryClick() // 변경 API
                                    }
                                }
                            )

                            // 완료 시 로그인 화면으로
                            LaunchedEffect(uiState.isComplete) {
                                if (uiState.isComplete) {
                                    Toast.makeText(context, "비밀번호가 재설정되었습니다.", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // 3. 설정 (로그인 후)
                        // ==========================================
                        composable("setting") {
                            val authViewModel: AuthViewModel = hiltViewModel()
                            val userEmail = remember { tokenManager.getUserEmail() }

                            SettingsPage(
                                navController = navController,
                                email = userEmail,
                                onPasswordChangeClick = {
                                    authFlowViewModel.initFlow(AuthFlowType.CHANGE_PASSWORD, email = userEmail)
                                    navController.navigate("password_verify")
                                },
                                onLogoutClick = {
                                    authViewModel.logout {
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                },
                                onWithdrawClick = {
                                    navController.navigate("withdraw")
                                },
                                gimbalStatusText = "연결됨",
                                pcStatusText = "미연결",
                                gimbalConnected = true,
                                pcConnected = false,
                                onGimbalClick = { },
                                onPcClick = { }
                            )
                        }

                        // 설정 > 비밀번호 변경 > 인증코드 입력
                        composable("password_verify") {
                            val uiState by authFlowViewModel.uiState.collectAsState()

                            AuthCodePage(
                                flowType = uiState.flowType,
                                code = uiState.code,
                                onCodeChange = { authFlowViewModel.onCodeChange(it) },
                                secondsLeft = uiState.secondsLeft,
                                codeError = uiState.codeError,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    authFlowViewModel.onPrimaryClick()
                                }
                            )

                            LaunchedEffect(uiState.step) {
                                if (uiState.step == AuthStep.PASSWORD) {
                                    navController.navigate("password_change_input") {
                                        popUpTo("password_verify") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // 설정 > 비밀번호 변경 > 입력 화면 (현재 비밀번호 포함)
                        composable("password_change_input") {
                            val uiState by authFlowViewModel.uiState.collectAsState()

                            PasswordChangePage(
                                onBack = { navController.popBackStack() },
                                onSubmit = { current, next, confirm ->
                                    authFlowViewModel.onCurrentPasswordChange(current)
                                    authFlowViewModel.onPasswordChange(next)
                                    authFlowViewModel.onPasswordConfirmChange(confirm)
                                    authFlowViewModel.onPrimaryClick()
                                }
                            )

                            LaunchedEffect(uiState.isComplete) {
                                if (uiState.isComplete) {
                                    Toast.makeText(context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack("setting", inclusive = false)
                                }
                            }
                        }

                        // 회원탈퇴
                        composable("withdraw") {
                            val authViewModel: AuthViewModel = hiltViewModel()
                            val isWithdrawn by authViewModel.isWithdrawn.collectAsState()

                            WithdrawPage(
                                onStay = { navController.popBackStack() },
                                onWithdraw = { authViewModel.withdrawAccount() }
                            )

                            LaunchedEffect(isWithdrawn) {
                                if (isWithdrawn) {
                                    Toast.makeText(context, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // 4. 기타 경로 (제스처 등 기존 유지)
                        // ==========================================

                        // + 버튼 클릭 시 생성
                        composable("gesture_name_input") {
                            GestureNameEditScreen(
                                navController = navController,
                                mappingId = 0,
                                currentName = "",
                                onBackClick = { navController.popBackStack() },
                                onSuccess = { navController.popBackStack() }
                            )
                        }

                        // 기존 수정 경로
                        composable(
                            route = "gesture_name_edit/{mappingId}/{currentName}",
                            arguments = listOf(
                                navArgument("mappingId") { type = NavType.IntType },
                                navArgument("currentName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val mappingId = backStackEntry.arguments?.getInt("mappingId") ?: 0
                            val currentName = java.net.URLDecoder.decode(
                                backStackEntry.arguments?.getString("currentName") ?: "",
                                "UTF-8"
                            )

                            GestureNameEditScreen(
                                navController = navController,
                                mappingId = mappingId,
                                currentName = currentName,
                                onBackClick = { navController.popBackStack() },
                                onSuccess = { navController.popBackStack() }
                            )
                        }

                        // 상세 화면
                        composable(
                            route = "gesture_detail/{mappingId}/{presetTitle}",
                            arguments = listOf(
                                navArgument("mappingId") { type = NavType.IntType },
                                navArgument("presetTitle") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val mappingId = backStackEntry.arguments?.getInt("mappingId") ?: 0
                            val presetTitle = backStackEntry.arguments?.getString("presetTitle") ?: ""

                            GestureDetailScreen(
                                navController = navController,
                                mappingId = mappingId,
                                initialTitle = presetTitle,
                                onAddClick = {
                                    navController.navigate("gesture_action_selection/$mappingId")
                                }
                            )
                        }

                        // 기능 선택 화면
                        composable(
                            route = "gesture_action_selection/{mappingId}",
                            arguments = listOf(navArgument("mappingId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val mappingId = backStackEntry.arguments?.getInt("mappingId") ?: 0

                            GestureActionSelectionScreen(
                                navController = navController,
                                mappingId = mappingId,
                                onBackClick = { navController.popBackStack() },
                                onActionSelected = { actionItem ->
                                    val encodedTitle = URLEncoder.encode(actionItem.title, StandardCharsets.UTF_8.toString())
                                    val encodedDesc = URLEncoder.encode(actionItem.description ?: "", StandardCharsets.UTF_8.toString())
                                    navController.navigate("gesture_assignment/$mappingId/${actionItem.id}/$encodedTitle/$encodedDesc/-1")
                                }
                            )
                        }

                        // 할당 화면
                        composable(
                            route = "gesture_assignment/{mappingId}/{actionId}/{actionTitle}/{actionDescription}/{currentGestureId}",
                            arguments = listOf(
                                navArgument("mappingId") { type = NavType.IntType },
                                navArgument("actionId") { type = NavType.IntType },
                                navArgument("actionTitle") { type = NavType.StringType },
                                navArgument("actionDescription") { type = NavType.StringType },
                                navArgument("currentGestureId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val mappingId = backStackEntry.arguments?.getInt("mappingId") ?: 0
                            val actionId = backStackEntry.arguments?.getInt("actionId") ?: 0
                            val title = backStackEntry.arguments?.getString("actionTitle") ?: ""
                            val desc = backStackEntry.arguments?.getString("actionDescription") ?: ""
                            val currentGestureId = backStackEntry.arguments?.getInt("currentGestureId") ?: -1

                            GestureAssignmentScreen(
                                navController = navController,
                                mappingId = mappingId,
                                actionId = actionId,
                                actionTitle = title,
                                actionDescription = desc,
                                currentGestureId = currentGestureId,
                                onBackClick = { navController.popBackStack() },
                                onSaveComplete = {
                                    navController.popBackStack("gesture_detail/{mappingId}/{presetTitle}", inclusive = false)
                                }
                            )
                        }
                    }

//                    NavHost(
//                        navController = navController,
//                        startDestination = "debug_menu"
//                    ) {
//                        composable("debug_menu") {
//                            DebugMenu(onGo = { route -> navController.navigate(route) })
//                        }
//
//                        composable("universal_test") {
//                            UniversalTestScreen()
//                        }
//
//                        composable("debug_login_error") {
//                            DebugLoginErrorScreen()
//                        }
//
//                        composable("debug_email_format_error") {
//                            DebugEmailFormatErrorScreen()
//                        }
//
//                        composable("debug_code_error") {
//                            DebugCodeErrorScreen()
//                        }
//
//                        composable("debug_password_error") {
//                            DebugPasswordErrorScreen()
//                        }
//                    }
                    // 26.01.26 백수연(AI) 테스트용
                    // 테스트 안할 때는 주석처리 하고 위의 Nev 주석 다 풀 것
//                    CameraPage()

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hidControlManager.restoreName()
    }
}