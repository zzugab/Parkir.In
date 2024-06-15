package com.sugab.parkirin.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.sugab.parkirin.R
import com.sugab.parkirin.data.parking.Parking

class ParkingView : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    var floorId: Int = 0
    var selectedParkingId: Int? = null

    private val viewModel by lazy {
        ViewModelProvider(context as ViewModelStoreOwner).get(ParkingViewModel::class.java)
    }

    init {
        // Observing the floors data
        viewModel.floors.observe(context as LifecycleOwner) {
            invalidate() // Redraw the view when data changes
            requestLayout() // Ensure that onMeasure and onDraw are called
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val floors = viewModel.floors.value
        val floor = floors?.getOrNull(floorId)
        if (floor != null) {
            floor.parkingList.forEach { parking ->
                drawParking(canvas, parking)
            }
        } else {
            Toast.makeText(context, "Lantai tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private val backgroundPaint = Paint()
    private val wheelPaint = Paint()
    private val spoilerPaint = Paint()
    private val numberParkingPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
    private var editedParking: Parking? = null
    private lateinit var auth: FirebaseAuth

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
        val startX = (width - totalWidth) / 2
        val startY = 50f

        var rowIndex = 0
        var columnIndex = 0

        viewModel.floors.value?.getOrNull(floorId)?.let { selectedFloor ->
            for (parking in selectedFloor.parkingList) {
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

    }

    private fun drawParking(canvas: Canvas?, parking: Parking) {
        if (parking.isPlaced) {
            backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.black_999, null)
            wheelPaint.color = ResourcesCompat.getColor(resources, R.color.black_999, null)
            spoilerPaint.color = ResourcesCompat.getColor(resources, R.color.black_999, null)
            numberParkingPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        } else {
            backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
            wheelPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
            spoilerPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
            numberParkingPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        }

        canvas?.save()

        canvas?.translate(parking.x ?: 0F, parking.y ?: 0F)

        val bgPath = Path()
        bgPath.addRect(5f, 150f, 150f, 180f, Path.Direction.CCW)
        bgPath.addRect(60f, 133f, 110f, 180f, Path.Direction.CCW)
        bgPath.addCircle(60f, 138f + 15f, 20f, Path.Direction.CCW)
        canvas?.drawPath(bgPath, backgroundPaint)

        val wheelPath = Path()
        val wheelRadius = 15f
        val wheelY = 150f
        val wheelOffset = 30f

        wheelPath.addCircle(120f, wheelY + wheelOffset, wheelRadius, Path.Direction.CCW)
        wheelPath.addCircle(30f, wheelY + wheelOffset, wheelRadius, Path.Direction.CCW)
        canvas?.drawPath(wheelPath, wheelPaint)

        val text: String = when (floorId) {
            0 -> "A${parking.id}"
            1 -> "B${parking.id}"
            2 -> "C${parking.id}"
            else -> "Lantai tidak ditemukan"
        }

        if (text == "Lantai tidak ditemukan") {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        numberParkingPaint.textSize = 18f
        canvas?.drawText(text, 70f, 120f, numberParkingPaint)

        canvas?.restore()
    }

    private fun showEditPlateNumberDialog(context: Context, parking: Parking) {
        editedParking = parking.copy()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val displayName = currentUser?.displayName

        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(context)
            .setTitle("Masukkan Plat Nomor")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val plateNumber = editText.text.toString()
                parking.plat = plateNumber
                parking.namePlaced = displayName ?: ""
                parking.isPlaced = true
                parking.startTime = Timestamp.now()

                viewModel.updateParking(floorId, parking)
                invalidate()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                cancelParkingEdit()
                dialog.dismiss()
            }
            .show()
    }

    private fun cancelParkingEdit() {
        editedParking?.let {
            val originalParking = viewModel.getParkingById(floorId, it.id)
            originalParking?.let { original ->
                original.plat = it.plat
                original.namePlaced = it.namePlaced
                original.isPlaced = it.isPlaced
                original.total = it.total
                invalidate()
            }
        }
        editedParking = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedParkingId = findParkingIdAtPoint(event.x, event.y)
                selectedParkingId?.let { parkingId ->
                    val parking = viewModel.getParkingById(floorId, parkingId)
                    if (parking != null && !parking.isPlaced) {
                        showEditPlateNumberDialog(context, parking)
                    } else {
                        Toast.makeText(
                            context,
                            "Parkiran sudah diisi cog, yakali!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    invalidate()
                }
            }
        }
        return true
    }

    private fun findParkingIdAtPoint(x: Float, y: Float): Int? {
        val floors = viewModel.floors.value
        val floor = floors?.getOrNull(floorId)
        floor?.let { selectedFloor ->
            selectedFloor.parkingList.forEach { parking ->
                val rect = RectF(
                    parking.x ?: 0f,
                    parking.y ?: 0f,
                    (parking.x ?: 0f) + 200f,
                    (parking.y ?: 0f) + 200f
                )
                if (rect.contains(x, y)) {
                    return parking.id
                }
            }
        }
        return null
    }
}