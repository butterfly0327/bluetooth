package com.buulgyeonE202.frontend.data.model.request.auth

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String,
    val verificationToken: String
)