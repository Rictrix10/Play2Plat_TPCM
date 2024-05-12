const express = require('express');
const { put } = require('@vercel/blob');
const multer = require('multer');
const path = require('path');

const router = express.Router();

// Configurando o armazenamento com multer
const storage = multer.memoryStorage();
const upload = multer({ storage: storage });

router.post('/upload', upload.single('image'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'Nenhuma imagem enviada' });
    }

    const imageContent = req.file.buffer;
    const imageName = req.file.originalname;

    const blob = await put(imageName, imageContent, { access: 'public' });

    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Ocorreu um erro no servidor' });
  }
});

module.exports = router;
