exports.errorHandler = (res, error) => {
    res.status(400).json({ error: error.message });
  };
  