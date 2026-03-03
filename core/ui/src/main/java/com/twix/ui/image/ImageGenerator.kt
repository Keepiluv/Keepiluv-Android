package com.twix.ui.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.twix.ui.image.exception.ImageProcessException
import java.io.ByteArrayOutputStream

class ImageGenerator(
    private val contentResolver: ContentResolver,
    private val rotator: Rotator,
) {
    /**
     * 주어진 [Uri]로부터 이미지를 읽어 JPEG 형식의 [ByteArray]로 변환한다.
     *
     * 내부 동작 과정:
     * 1. EXIF 메타데이터로 회전 방향 확인
     * 2. [Uri]로부터 Bitmap 디코딩 (메모리 최적화를 위해 샘플링 적용)
     * 3. 필요 시 회전 처리 후 JPEG(품질 90) 압축하여 [ByteArray] 반환
     *
     * 실패 케이스:
     * - InputStream 열기 실패
     * - 디코딩 실패 (손상된 이미지 등)
     * - 압축 실패
     *
     * @param imageUri 변환할 이미지 Uri (content:// 또는 file://)
     * @return 변환 성공 시 JPEG 바이트 배열, 실패 시 null
     */
    fun uriToByteArray(imageUri: Uri): ByteArray? =
        try {
            val orientation: Int = rotator.orientation(imageUri)
            val bitmap: Bitmap = uriToBitmap(imageUri)
            val rotatedBitmap =
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotator.rotate(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotator.rotate(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotator.rotate(bitmap, 270f)
                    else -> bitmap
                }

            // 회전된 새 비트맵이 생성된 경우 원본 즉시 해제
            if (rotatedBitmap !== bitmap) bitmap.recycle()
            bitmapToByteArray(rotatedBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    /**
     * [Uri]로부터 [Bitmap]을 디코딩한다.
     *
     * 메모리 사용량을 줄이기 위해 두 단계로 디코딩한다.
     * 1. [BitmapFactory.Options.inJustDecodeBounds]로 이미지 크기만 먼저 읽기
     * 2. [calculateInSampleSize]로 샘플 크기를 계산한 뒤 실제 디코딩
     *
     * @throws ImageProcessException.DecodeFailedException 디코딩 실패 시
     */
    private fun uriToBitmap(imageUri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, bounds)
        }

        val options =
            BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(bounds, 1920, 1080)
            }

        return contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: throw ImageProcessException.DecodeFailedException(imageUri)
    }

    /**
     * 목표 해상도([reqWidth] x [reqHeight])에 맞는 최적의 [BitmapFactory.Options.inSampleSize]를 계산한다.
     *
     * 반환값은 2의 거듭제곱이며, 디코딩된 이미지가 목표 해상도보다 작아지지 않는 최대값을 반환한다.
     *
     * @param options outWidth, outHeight가 채워진 [BitmapFactory.Options]
     * @param reqWidth 목표 너비 (px)
     * @param reqHeight 목표 높이 (px)
     * @return 계산된 inSampleSize (최솟값 1)
     */
    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * [Bitmap]을 JPEG 형식(품질 90)으로 압축하여 [ByteArray]로 변환한다.
     *
     * 압축 완료 후 [Bitmap.recycle]을 호출하므로, 이후 해당 [Bitmap]을 재사용해선 안 된다.
     *
     * @param bitmap 압축 대상 Bitmap
     * @return JPEG 바이트 배열
     * @throws ImageProcessException.CompressionFailedException 압축 실패 시
     */
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        try {
            val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            if (!success) {
                // TODO("Firebase Crashlytics 로깅")
                throw ImageProcessException.CompressionFailedException(
                    IMAGE_COMPRESSION_ERROR_MESSAGE.format(
                        bitmap.config,
                        bitmap.width,
                        bitmap.height,
                    ),
                )
            }
            return outputStream.toByteArray()
        } finally {
            bitmap.recycle()
            outputStream.close()
        }
    }

    companion object {
        private const val IMAGE_COMPRESSION_ERROR_MESSAGE = "Config: %s, Size: %dx%d"
    }
}
