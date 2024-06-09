const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { validateRegisterInput, validateLoginInput } = require('../utils/validator');

const users = []; // Simpan pengguna di array sementara

exports.register = (req, res) => {
  const { username, email, password } = req.body;

  const { errors, valid } = validateRegisterInput(username, email, password);

  if (!valid) {
    return res.status(400).json(errors);
  }

  // Cek apakah email sudah terdaftar
  const userExists = users.find(user => user.email === email);
  if (userExists) {
    return res.status(400).json({ email: 'Email already exists' });
  }

  // Hash password
  const hashedPassword = bcrypt.hashSync(password, 10);

  // Simpan pengguna baru
  const newUser = { id: users.length + 1, username, email, password: hashedPassword };
  users.push(newUser);

  res.status(201).json({ message: 'User registered successfully' });
};

exports.login = (req, res) => {
  const { email, password } = req.body;

  const { errors, valid } = validateLoginInput(email, password);

  if (!valid) {
    return res.status(400).json(errors);
  }

  // Cek apakah pengguna terdaftar
  const user = users.find(user => user.email === email);
  if (!user) {
    return res.status(400).json({ email: 'User not found' });
  }

  // Cek password
  const isPasswordCorrect = bcrypt.compareSync(password, user.password);
  if (!isPasswordCorrect) {
    return res.status(400).json({ password: 'Incorrect password' });
  }

  // Buat token JWT
  const token = jwt.sign({ id: user.id, email: user.email }, 'your_jwt_secret', { expiresIn: '1h' });

  res.json({ token });
};
