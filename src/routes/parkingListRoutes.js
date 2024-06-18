const express = require('express');
const router = express.Router();
const firestore = require('../config/firebaseConfig');

// Create a new document in 'parkingList' subcollection
router.post('/', async (req, res) => {
  try {
    const floorId = req.params.floorId;
    const data = req.body;
    const docRef = await firestore.collection('floors').doc(floorId).collection('parkingList').add(data);
    res.status(201).send({ id: docRef.id });
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Read a document from 'parkingList' subcollection
router.get('/:parkingId', async (req, res) => {
  try {
    const floorId = req.params.floorId;
    const parkingId = req.params.parkingId;
    const docRef = firestore.collection('floors').doc(floorId).collection('parkingList').doc(parkingId);
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

// Update a document in 'parkingList' subcollection
router.put('/:parkingId', async (req, res) => {
  try {
    const floorId = req.params.floorId;
    const parkingId = req.params.parkingId;
    const data = req.body;
    const docRef = firestore.collection('floors').doc(floorId).collection('parkingList').doc(parkingId);
    await docRef.update(data);
    res.status(200).send('Document updated');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Update a specific field in 'parkingList' subcollection item
router.put('/:parkingId/field', async (req, res) => {
  try {
    const floorId = req.params.floorId;
    const parkingId = req.params.parkingId;
    const fieldToUpdate = req.body.field;
    const newValue = req.body.value;

    const docRef = firestore.collection('floors').doc(floorId).collection('parkingList').doc(parkingId);

    const updateData = {};
    updateData[fieldToUpdate] = newValue;

    await docRef.update(updateData);

    res.status(200).send('Field updated');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

// Delete a document from parkingList subcollection
router.delete('/:parkingId', async (req, res) => {
  try {
    const floorId = req.params.floorId;
    const parkingId = req.params.parkingId;
    const docRef = firestore.collection('floors').doc(floorId).collection('parkingList').doc(parkingId);
    await docRef.delete();
    res.status(200).send('Document deleted');
  } catch (error) {
    res.status(400).send(error.message);
  }
});

module.exports = router;
