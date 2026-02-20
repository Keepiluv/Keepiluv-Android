package com.twix.ui.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream

class ImageGenerator(
    private val contentResolver: ContentResolver,
    private val rotator: Rotator,
) {
    /**
     * 주어진 [Uri]로부터 이미지를 읽어 JPEG 형식의 [ByteArray]로 변환한다.
     *
     * 내부 동작 과정:
     * 1. [android.content.ContentResolver.openInputStream]으로 InputStream을 연다.
     * 2. [android.graphics.BitmapFactory.decodeStream]으로 Bitmap 디코딩
     * 3. JPEG(품질 90) 압축 후 ByteArray 반환
     *
     * 실패 케이스:
     * - InputStream 열기 실패
     * - 디코딩 실패 (손상 이미지 등)
     * - 압축 실패
     *
     * @param imageUri 변환할 이미지 Uri (content:// 또는 file://)
     * @return 변환 성공 시 JPEG 바이트 배열, 실패 시 null
     */
    fun uriToByteArray(imageUri: Uri): ByteArray? =
        try {
            val orientation: Int = rotator.orientation(imageUri)
            val bitmap: Bitmap = uriToBitmap(contentResolver, imageUri)
            val rotatedBitmap =
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotator.rotate(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotator.rotate(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotator.rotate(bitmap, 270f)
                    else -> bitmap
                }
            if (rotatedBitmap !== bitmap) bitmap.recycle()
            bitmapToByteArray(rotatedBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    /**
     * [Uri] 로부터 실제 [Bitmap] 을 디코딩한다.
     *
     * 새로운 InputStream을 열어 [BitmapFactory.decodeStream] 으로 변환한다.
     */
    private fun uriToBitmap(
        contentResolver: ContentResolver,
        imageUri: Uri,
    ): Bitmap =
        contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IllegalArgumentException(IMAGE_DECODE_ERROR_MESSAGE.format(imageUri))

    /**
     * [Bitmap] 을 JPEG 형식(품질 90)으로 압축하여 [ByteArray] 로 변환한다.
     *
     * 압축 완료 후 메모리 절약을 위해 내부에서 [Bitmap.recycle] 을 호출한다.
     * 따라서 호출 이후 전달한 Bitmap은 재사용하면 안 된다.
     *
     * @param bitmap 압축 대상 Bitmap
     * @return JPEG 바이트 배열
     */
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray =
        ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            bitmap.recycle()
            outputStream.toByteArray()
        }

    companion object {
        private const val IMAGE_DECODE_ERROR_MESSAGE = "Failed to open or decode image: %s"
    }
}
