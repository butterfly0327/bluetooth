package com.buulgyeonE202.frontend.di

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.buulgyeonE202.frontend.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object S3Module {

    @Provides
    @Singleton
    fun provideAmazonS3Client(): AmazonS3Client {
        // 1. 자격 증명 (키 설정)
        val credentials = BasicAWSCredentials(
            BuildConfig.AWS_ACCESS_KEY_ID,
            BuildConfig.AWS_SECRET_ACCESS_KEY
        )

        // 2. 클라이언트 생성
        val s3Client = AmazonS3Client(credentials)

        // 3. 리전 설정 (시드니: ap-southeast-2)
        // BuildConfig에 저장된 문자열에 따라 Region 객체를 설정.
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2))

        return s3Client
    }

    @Provides
    @Singleton
    fun provideTransferUtility(
        @ApplicationContext context: Context,
        s3Client: AmazonS3Client
    ): TransferUtility {
        // 파일 업로드를 쉽게 해주는 유틸리티
        return TransferUtility.builder()
            .context(context)
            .s3Client(s3Client)
            .defaultBucket(BuildConfig.S3_BUCKET_NAME)
            .build()
    }
}