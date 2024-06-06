package com.sugab.parkirin.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.sugab.parkirin.R
import com.sugab.parkirin.data.parking.Floor
import com.sugab.parkirin.data.parking.Parking

class ParkingViewF1 : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun createParking(floorId: Int, label: String): List<Parking> {
        return (1..20).map { index ->
            Parking(
                id = (floorId - 1) * 20 + index,
                name = "$label$index",
                type = "Type$floorId",
                isPlaced = false
            )
        }
    }

    private val floors = listOf(
        Floor(id = 0, parkingList = createParking(1, "A"))
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        val columnCount = 4
        val rowCount = 5
        val parkingWidth = 200f
        val parkingHeight = 200f
        val horizontalSpacing = 50f
        val verticalSpacing = 50f
        val totalWidth = columnCount * parkingWidth + (columnCount - 1) * horizontalSpacing
        val totalHeight = rowCount * parkingHeight + (rowCount - 1) * verticalSpacing
        val startX = (width - totalWidth) / 2
        val startY = (height - totalHeight) / 2

        var rowIndex = 0
        var columnIndex = 0

        // Hanya mengatur posisi untuk satu floor saja
        val floor = floors.first() // Mengambil floor pertama (floor 1)
        for (k in floor.parkingList.indices) {
            val parking = floor.parkingList[k]
            parking.apply {
                x = startX + columnIndex * (parkingWidth + horizontalSpacing)
                y = startY + rowIndex * (parkingHeight + verticalSpacing)
            }

            columnIndex++
            if (columnIndex == columnCount) {
                columnIndex = 0
                rowIndex++
            }
        }
    }

    private val backgroundPaint = Paint()
    private val wheelPaint = Paint()
    private val spoilerPaint = Paint()
    private val numberSeatPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
    private val titlePaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Hanya menggambar satu floor (floor 1)
        val floor = floors.first() // Mengambil floor pertama (floor 1)
        // Menampilkan notifikasi floor
        drawFloorNotification(canvas, floor)

        floor.parkingList.forEach { parking ->
            drawParking(canvas, parking)
        }
    }

    private fun drawFloorNotification(canvas: Canvas, floor: Floor) {
        val text = "Floor ${floor.id + 1}"
        titlePaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        titlePaint.textSize = 36f
        canvas.drawText(text, 50f, 50f, titlePaint)
    }

    private fun drawParking(canvas: Canvas?, parking: Parking) {
        if (parking.isPlaced) {
            backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.black_999, null)
            wheelPaint.color = ResourcesCompat.getColor(resources, R.color.black_999, null)
            spoilerPaint.color = ResourcesCompat.getColor(resources, R.color.black_999, null)
            numberSeatPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        } else {
            backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.black_111, null)
            wheelPaint.color = ResourcesCompat.getColor(resources, R.color.black_111, null)
            spoilerPaint.color = ResourcesCompat.getColor(resources, R.color.black_111, null)
            numberSeatPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        }

        // Menyimpan State
        canvas?.save()

        // Background
        canvas?.translate(parking.x ?: 0F, parking.y ?: 0F)

        // Path untuk badan mobil (diperkecil)
        val bgPath = Path()
        bgPath.addRect(5f, 150f, 150f, 180f, Path.Direction.CCW)
        bgPath.addRect(60f, 133f, 110f, 180f, Path.Direction.CCW)
        bgPath.addCircle(60f, 138f + 15f, 20f, Path.Direction.CCW)
        canvas?.drawPath(bgPath, backgroundPaint)

        // Menggambar 2 roda (diperkecil)
        val wheelPath = Path()
        val wheelRadius = 15f
        val wheelY = 150f
        val wheelOffset = 30f

        // Roda belakang
        wheelPath.addCircle(120f, wheelY + wheelOffset, wheelRadius, Path.Direction.CCW)
        wheelPath.addCircle(30f, wheelY + wheelOffset, wheelRadius, Path.Direction.CCW)
        canvas?.drawPath(wheelPath, wheelPaint)
        // Menambahkan teks ID parkinggit
        val text: String = when (floors.first().id) {
            0 -> {
                "A${parking.id}"
            }
            1 -> {
                "B${parking.id}"
            }
            2 -> {
                "C${parking.id}"
            } else -> {
                "Lantai tidak ditemukan"
            }
        }
        if (text == "Lantai tidak ditemukan") {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        numberSeatPaint.textSize = 18f
        canvas?.drawText(text, 70f, 120f, numberSeatPaint)


        // Restore state
        canvas?.restore()
    }

}