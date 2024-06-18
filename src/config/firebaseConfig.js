const admin = require('firebase-admin');
const serviceAccount = require('../parkir-in-firebase-adminsdk.json');

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://elite-buttress-422612.firebaseio.com' // Replace with your database URL
  });
}

const db = admin.firestore();

module.exports = db;