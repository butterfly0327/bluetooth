package com.buulgyeonE202.frontend.ui.auth.model

data class AuthFlowTexts(
    val mainTitle: String,       // 큰 굵은 제목(예: "만나서 반가워요...")
    val buttonText: String       // 버튼 텍스트(다음/완료)
)

fun textsFor(flowType: AuthFlowType, step: AuthStep): AuthFlowTexts {
    return when (flowType) {
        AuthFlowType.SIGN_UP -> when (step) {
            AuthStep.EMAIL -> AuthFlowTexts(
                mainTitle = "만나서 반가워요.\n이메일을 입력해주세요!",
                buttonText = "다음"
            )
            AuthStep.CODE -> AuthFlowTexts(
                mainTitle = "안전한 사용을 위해\n이메일 인증을 해주세요.",
                buttonText = "다음"
            )
            AuthStep.PASSWORD -> AuthFlowTexts(
                mainTitle = "안전한 사용을 위해\n비밀번호를 입력해주세요.",
                buttonText = "완료"
            )
        }

        AuthFlowType.RESET_PASSWORD -> when (step) {
            AuthStep.EMAIL -> AuthFlowTexts(
                mainTitle = "비밀번호를 찾으실\n이메일을 입력해주세요.",
                buttonText = "다음"
            )
            AuthStep.CODE -> AuthFlowTexts(
                mainTitle = "입력하신 이메일로\n인증 문자를 전송했습니다.",
                buttonText = "다음"
            )
            AuthStep.PASSWORD -> AuthFlowTexts(
                mainTitle = " 새 비밀번호를 입력해주세요.",
                buttonText = "완료"
            )
        }

        // 비밀번호 변경 플로우 문구
        AuthFlowType.CHANGE_PASSWORD -> when (step) {
            AuthStep.EMAIL -> AuthFlowTexts(
                mainTitle = "비밀번호 변경을 위해\n이메일을 확인해주세요.",
                buttonText = "다음"
            )
            AuthStep.CODE -> AuthFlowTexts(
                mainTitle = "전송된 인증번호를\n입력해주세요.",
                buttonText = "다음"
            )
            AuthStep.PASSWORD -> AuthFlowTexts(
                mainTitle = "현재 비밀번호와\n새 비밀번호를 입력해주세요.",
                buttonText = "변경 완료"
            )
        }
    }
}
