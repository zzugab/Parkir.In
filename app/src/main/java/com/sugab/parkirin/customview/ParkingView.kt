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
import com.google.firebase.auth.FirebaseAuth
import com.sugab.parkirin.R
import com.sugab.parkirin.data.parking.Floor
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
        set(value) {
            field = value
            invalidate() // Memanggil ulang onDraw() untuk menggambar ulang
        }
    var touchStartTime: Long = 0
    var selectedParkingId: Int? = null

    private fun createParking(floorId: Int, label: String): List<Parking> {
        return (1..20).map { index ->
            Parking(
                id = (floorId - 1) * 20 + index,
                name = "$label$index",
                isPlaced = false
            )
        }
    }

    private val floors = listOf(
        Floor(id = 0, parkingList = createParking(1, "A")),
        Floor(id = 1, parkingList = createParking(2, "B")),
        Floor(id = 2, parkingList = createParking(3, "C"))
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
        val startX = (width - totalWidth) / 2
        val startY = 50f // Ubah nilai ini untuk menyesuaikan jarak dari atas

        var rowIndex = 0
        var columnIndex = 0

        // Mengambil floor berdasarkan floorId yang saat ini aktif
        val floor = floors.getOrNull(floorId)
        floor?.let { selectedFloor ->
            for (k in selectedFloor.parkingList.indices) {
                val parking = selectedFloor.parkingList[k]
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

    private val backgroundPaint = Paint()
    private val wheelPaint = Paint()
    private val spoilerPaint = Paint()
    private val numberParkingPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
    private var editedParking: Parking? = null
    private lateinit var auth: FirebaseAuth


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Menggambar parking spots pada floor yang sesuai
        val floor = floors.getOrNull(floorId)
        if (floor != null) {

            floor.parkingList.forEach { parking ->
                drawParking(canvas, parking)
            }
        } else {
            // Menampilkan pesan jika floorId tidak valid
            Toast.makeText(context, "Lantai tidak ditemukan", Toast.LENGTH_SHORT).show()
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

        // Menggambar 2 roda
        val wheelPath = Path()
        val wheelRadius = 15f
        val wheelY = 150f
        val wheelOffset = 30f

        // Roda belakang
        wheelPath.addCircle(120f, wheelY + wheelOffset, wheelRadius, Path.Direction.CCW)
        wheelPath.addCircle(30f, wheelY + wheelOffset, wheelRadius, Path.Direction.CCW)
        canvas?.drawPath(wheelPath, wheelPaint)
        // Menambahkan teks ID parking
        val text: String = when (floors.getOrNull(floorId)!!.id) {
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

        numberParkingPaint.textSize = 18f
        canvas?.drawText(text, 70f, 120f, numberParkingPaint)


        // Restore state
        canvas?.restore()
    }


    private fun showEditPlateNumberDialog(context: Context, parking: Parking) {
        // Simpan parkir yang sedang diedit sebelum menampilkan dialog
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
                parking.name = displayName!!
                invalidate() // Menggambar ulang ParkingView setelah mengubah nilai plat dan name
            }
            .setNegativeButton("Batal") { dialog, _ ->
                // Batalkan perubahan yang dilakukan
                cancelParkingEdit()
                dialog.dismiss()
            }
            .show()
    }

    private fun cancelParkingEdit() {
        editedParking?.let {
            // Kembalikan parkir ke kondisi sebelum dialog ditampilkan
            val originalParking = findParkingById(it.id)
            originalParking?.let { original ->
                original.plat = it.plat
                original.name = it.name
                original.isPlaced = it.isPlaced
                original.total = it.total
                invalidate() // Gambar ulang untuk memperbarui tampilan
            }
        }
        editedParking = null // Hapus referensi parkir yang sedang diedit
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Cek apakah titik sentuhan berada di dalam area parkir
                selectedParkingId = findParkingIdAtPoint(event.x, event.y)
                selectedParkingId?.let { parkingId ->
                    val parking = findParkingById(parkingId)
                    if (parking != null && !parking.isPlaced) {
                        showEditPlateNumberDialog(context, parking)
                        parking.isPlaced = true
                        touchStartTime = System.currentTimeMillis()
                        Toast.makeText(context, "Parking ID: ${parking?.id}", Toast.LENGTH_SHORT).show()
                        // Memanggil invalidate untuk menggambar ulang tampilan
                    }else {
                        Toast.makeText(context, "Parkiran sudah diisi cog, yakali !", Toast.LENGTH_SHORT).show()
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                // Saat user melepaskan sentuhan, selesai menghitung waktu parkir
                selectedParkingId?.let { parkingId ->
                    val parking = findParkingById(parkingId)
                    if (parking != null && parking.isPlaced) {
                        // Menghitung total parkir berdasarkan waktu parkir
                        val endTime = System.currentTimeMillis()
                        val elapsedTime = endTime - touchStartTime
                        parking.total = (elapsedTime / 1000 * 5).toInt() // dikalikan 5000
                        // Memanggil invalidate untuk menggambar ulang tampilan
                        invalidate()
                    }
                }
                selectedParkingId = null
            }
        }
        return true
    }

    private fun findParkingIdAtPoint(x: Float, y: Float): Int? {
        // Mencari id parkir berdasarkan titik sentuhan (x, y)
        val floor = floors.getOrNull(floorId)
        floor?.let { selectedFloor ->
            selectedFloor.parkingList.forEach { parking ->
                val rect = RectF(parking.x ?: 0f, parking.y ?: 0f, (parking.x ?: 0f) + 200f, (parking.y ?: 0f) + 200f)
                if (rect.contains(x, y)) {
                    return parking.id
                }
            }
        }
        return null
    }

    private fun findParkingById(parkingId: Int): Parking? {
        // Mencari objek Parking berdasarkan id
        val floor = floors.getOrNull(floorId)
        floor?.let { selectedFloor ->
            return selectedFloor.parkingList.find { it.id == parkingId }
        }
        return null
    }
    fun getParkingByName(name: String): Parking? {
        for (floor in floors) {
            for (parking in floor.parkingList) {
                if (parking.name == name) {
                    return parking
                }
            }
        }
        return null
    }
}
