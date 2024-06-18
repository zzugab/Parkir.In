const express = require('express');
const router = express.Router();
const db = require('../config/firebaseConfig');
const admin = require('firebase-admin');

// Endpoint untuk membuat reservasi tempat parkir
router.post('/reserveParkingPlace/:parkingPlaceId', async (req, res) => {
  try {
    const parkingPlaceId = req.params.parkingPlaceId;
    const reservationData = req.body;

    // Cek apakah tempat parkir tersedia
    const parkingPlaceRef = db.collection('parking_places').doc(parkingPlaceId);
    const parkingPlaceDoc = await parkingPlaceRef.get();

    if (!parkingPlaceDoc.exists) {
      res.status(404).json({ error: 'Tempat parkir tidak ditemukan' });
      return;
    }

    const parkingPlaceData = parkingPlaceDoc.data();
    const totalSlots = parkingPlaceData.total_slots;
    const availableSlots = parkingPlaceData.available_slots;

    if (availableSlots <= 0) {
      res.status(400).json({ error: 'Tempat parkir penuh' });
      return;
    }

    // Buat reservasi
    const reservationsRef = parkingPlaceRef.collection('reservations');
    const newReservationRef = await reservationsRef.add(reservationData);

    // Kurangi slot yang tersedia
    await parkingPlaceRef.update({
      available_slots: admin.firestore.FieldValue.increment(-1),
      reserved_slots: admin.firestore.FieldValue.increment(1)
    });

    res.status(201).json({ message: 'Reservasi berhasil dibuat', id: newReservationRef.id });
  } catch (error) {
    console.error('Error membuat reservasi: ', error);
    res.status(500).json({ error: 'Gagal membuat reservasi', details: error.message });
  }
});

// Endpoint untuk membatalkan reservasi tempat parkir
router.delete('/cancelReservation/:parkingPlaceId/:reservationId', async (req, res) => {
  try {
    const parkingPlaceId = req.params.parkingPlaceId;
    const reservationId = req.params.reservationId;

    // Hapus reservasi dari Firestore
    const reservationRef = db.collection('parking_places').doc(parkingPlaceId)
                          .collection('reservations').doc(reservationId);
    const reservationDoc = await reservationRef.get();

    if (!reservationDoc.exists) {
      res.status(404).json({ error: 'Reservasi tidak ditemukan' });
      return;
    }

    await reservationRef.delete();

    // Tambahkan kembali slot yang tersedia
    const parkingPlaceRef = db.collection('parking_places').doc(parkingPlaceId);
    await parkingPlaceRef.update({
      available_slots: admin.firestore.FieldValue.increment(1),
      reserved_slots: admin.firestore.FieldValue.increment(-1)
    });

    res.status(200).json({ message: 'Reservasi berhasil dibatalkan' });
  } catch (error) {
    console.error('Error membatalkan reservasi: ', error);
    res.status(500).json({ error: 'Gagal membatalkan reservasi', details: error.message });
  }
});

module.exports = router;
