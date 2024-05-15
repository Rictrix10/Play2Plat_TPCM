const express = require('express');
const multer = require('multer');
const { put } = require('@vercel/blob');

const router = express.Router();
const upload = multer();

router.post('/upload', upload.single('file'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'Nenhum arquivo enviado' });
    }

    const fileBuffer = req.file.buffer;
    const fileName = req.file.originalname;

    const blob = await put(fileName, fileBuffer, { access: 'public' });

    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
