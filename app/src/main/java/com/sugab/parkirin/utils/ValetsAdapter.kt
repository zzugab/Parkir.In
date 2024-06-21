package com.sugab.parkirin.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sugab.parkirin.R
import com.sugab.parkirin.data.valet.Valet

class ValetsAdapter(private var items: List<Valet?>) : RecyclerView.Adapter<ValetsAdapter.ValetViewHolder>() {

    inner class ValetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvParkingId: TextView = itemView.findViewById(R.id.tvParkingId)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvEmployeeName: TextView = itemView.findViewById(R.id.tvEmployeeName)
        private val tvPlat: TextView = itemView.findViewById(R.id.tvPlat)
        private val tvIsParked: TextView = itemView.findViewById(R.id.tvIsParked)

        fun bind(valet: Valet) {
            tvTitle.text = itemView.context.getString(R.string.valet_card)
            tvId.text = "Valet ID: ${valet.id}"
            tvParkingId.text = "Parking ID: ${valet.parkingId}"
            tvUserName.text = "Customer ID: ${valet.userId}"
            tvEmployeeName.text = "Employee ID: ${valet.employeeId}"
            tvPlat.text = "Plat: ${valet.plat}"
            tvIsParked.text = "Is Parked: ${valet.isParked}"

            // Set OnClickListener untuk card
            itemView.setOnClickListener {
                if (!valet.isParked) {
                    // Jika belum diparkir, ubah status isParked menjadi true
                    showParkedConfirmationDialog(itemView.context, valet)
                    // Update status karyawan menjadi isReady = true
                    notifyDataSetChanged()

                } else {
                    // Jika sudah diparkir, tampilkan dialog konfirmasi untuk hapus valet
                    showDeleteConfirmationDialog(itemView.context, valet)
                    notifyDataSetChanged()
                }
            }
        }

        private fun showParkedConfirmationDialog(context: Context, valet: Valet) {
            AlertDialog.Builder(context)
                .setTitle("Parked Confirmation")
                .setMessage("Mobil sudah terparkir ?")
                .setPositiveButton("Iya") { _, _ ->
                    // Update isParked menjadi true di Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("valet")
                        .document(valet.id)
                        .update("isParked", true)
                        .addOnSuccessListener {
                            // Update berhasil
                            Toast.makeText(context, "Update Status Berhasil", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            // Gagal update
                            val errorMessage = "Error updating valet status: ${exception.message}"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }

                    db.collection("valetEmployees")
                        .whereEqualTo("id", valet.employeeId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val isReady = true
                                // Update isReady menjadi true di Firestore
                                db.collection("valetEmployees")
                                    .document(valet.employeeId.toString())
                                    .update("isReady", isReady)
                            } else {
                                Log.e("Firestore", "Employee document not found for id: $valet.employeeId")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error fetching employee name for id: $valet.employeeId", exception)
                        }
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        private fun showDeleteConfirmationDialog(context: Context, valet: Valet) {
            AlertDialog.Builder(context)
                .setTitle("Hapus Valet Card")
                .setMessage("Anda yakin ingin menghapus Valet Card ini?")
                .setPositiveButton("Iya") { _, _ ->
                    // Hapus dokumen valet dari Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("valet")
                        .document(valet.id)
                        .delete()
                        .addOnSuccessListener {
                            // Dokumen berhasil dihapus
                            Toast.makeText(context, "Hapus Berhasil", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            val errorMessage = "Error deleting valet: ${exception.message}"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.valet_card, parent, false)
        return ValetViewHolder(view)
    }

    override fun onBindViewHolder(holder: ValetViewHolder, position: Int) {
        holder.bind(items[position]!!)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Valet?>) {
        items = newItems
        notifyDataSetChanged()
    }
}
