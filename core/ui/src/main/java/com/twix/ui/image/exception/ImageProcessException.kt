package com.twix.ui.image.exception

import android.net.Uri

sealed class ImageProcessException(
    message: String,
) : Exception(message) {
    // Uri로부터 비트맵을 디코딩하지 못했을 때 (파일 없음, 권한 부족, 손상된 파일)
    class DecodeFailedException(
        uri: Uri,
    ) : ImageProcessException("이미지 디코딩 실패. Uri: $uri")

    // 비트맵 압축(compress) 결과가 false일 때 (메모리 부족, 하드웨어 가속 오류 등)
    class CompressionFailedException(
        details: String,
    ) : ImageProcessException("이미지 압축 실패: $details")
}
