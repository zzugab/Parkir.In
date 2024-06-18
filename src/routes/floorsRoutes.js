const express = require('express');
const router = express.Router();
const firestore = require('../config/firebaseConfig');

// Create a new document in 'floors'
router.post('/', async (req, res) => {
  try {
    const data = req.body;
    const docRef = await firestore.collection('floors').add(data);
    res.status(201).send({ id: docRef.id });
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Read a document from floors
router.get('/:id', async (req, res) => {
  try {
    const docRef = firestore.collection('floors').doc(req.params.id);
    const doc = await docRef.get();
    if (!doc.exists) {
      res.status(404).send('Document not found');
    } else {
      res.status(200).send(doc.data());
    }
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Update a document in floors
router.put('/:id', async (req, res) => {
  try {
    const data = req.body;
    const docRef = firestore.collection('floors').doc(req.params.id);
    await docRef.update(data);
    res.status(200).send('Document updated');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Delete a document from 'floors'
router.delete('/:id', async (req, res) => {
  try {
    const docRef = firestore.collection('floors').doc(req.params.id);
    await docRef.delete();
    res.status(200).send('Document deleted');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// CRUD for subcollection 'parkingList'

// Import parkingList routes
const parkingListRoutes = require('./parkingListRoutes');
// Mount parkingList routes on /:floorId/parkingList
router.use('/:floorId/parkingList', parkingListRoutes);

module.exports = router;
