const express = require('express');
const router = express.Router();
const firestore = require('../config/firebaseConfig');

// Create a new user
router.post('/', async (req, res) => {
  try {
    const { uid, displayName, email, role } = req.body; // Destructure the request body
    const docRef = firestore.collection('users').doc(uid); // Use UID as the document ID
    await docRef.set({ displayName, email, role, uid });
    res.status(201).send({ id: uid });
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Read a user by ID
router.get('/:uid', async (req, res) => {
  try {
    const uid = req.params.uid;
    const docRef = firestore.collection('users').doc(uid);
    const doc = await docRef.get();
    if (!doc.exists) {
      res.status(404).send('Data user tidak ditemukan');
    } else {
      res.status(200).send(doc.data());
    }
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Update a user by ID
router.put('/:uid', async (req, res) => {
  try {
    const uid = req.params.uid;
    const data = req.body;
    const docRef = firestore.collection('users').doc(uid);
    await docRef.update(data);
    res.status(200).send('Data user berhasil diupdate!');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Delete a user by ID
router.delete('/:uid', async (req, res) => {
  try {
    const uid = req.params.uid;
    const docRef = firestore.collection('users').doc(uid);
    await docRef.delete();
    res.status(200).send('Data users berhasil dihapus!');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

module.exports = router;
