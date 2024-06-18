const express = require('express');
const bodyParser = require('body-parser');
const parkingListRoutes = require('./routes/parkingListRoutes');
const floorsRoutes = require('./routes/floorsRoutes');
const usersRoutes = require('./routes/usersRoutes'); // Import routes for users

const app = express();
const PORT = process.env.PORT || 3000;

app.use(bodyParser.json());

app.use('/floors/:floorId/parkingList', parkingListRoutes);
app.use('/floors', floorsRoutes);
app.use('/users', usersRoutes); // Use routes for users

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
