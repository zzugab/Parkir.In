const Sequelize = require('sequelize');
const sequelize = new Sequelize('parkir-in-test', 'yuda_studying', '(>UibCB~Di%S+x9U', {
  host: 'localhost',
  dialect: 'postgres'
});

module.exports = sequelize;