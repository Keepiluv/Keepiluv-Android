package com.twix.ui.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

class Rotator(
    private val contentResolver: ContentResolver,
) {
    /**
     * 이미지의 EXIF 메타데이터에서 Orientation 값을 읽는다.
     *
     * - NORMAL → 회전 없음
     * - ROTATE_90 / 180 / 270 → 해당 각도만큼 시계 방향 회전 필요
     *
     * 내부적으로 새로운 InputStream을 열어 [ExifInterface] 로 분석한다.
     *
     * @param imageUri 대상 이미지 Uri
     * @return EXIF orientation 값 (기본값: ORIENTATION_NORMAL)
     */
    fun orientation(imageUri: Uri): Int =
        contentResolver.openInputStream(imageUri)?.use { exifStream ->
            ExifInterface(exifStream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
        } ?: ExifInterface.ORIENTATION_NORMAL

    /**
     * 주어진 [Bitmap] 을 지정한 각도만큼 회전한 새로운 Bitmap을 생성한다.
     *
     * 원본 Bitmap은 수정되지 않고,
     * 새로운 Bitmap 인스턴스가 반환된다.
     *
     * @param image 회전 대상 Bitmap
     * @param degree 시계 방향 회전 각도
     * @return 회전된 새 Bitmap
     */
    fun rotate(
        image: Bitmap,
        degree: Float,
    ): Bitmap {
        val matrix = Matrix().apply { postRotate(degree) }
        return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
    }
}
