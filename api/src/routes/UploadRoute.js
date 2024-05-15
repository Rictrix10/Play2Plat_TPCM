const express = require('express');
const { put } = require('@vercel/blob');

const router = express.Router();

router.post('/upload', async (req, res) => {
  try {
    if (!req.files || Object.keys(req.files).length === 0) {
      return res.status(400).json({ error: 'Nenhum arquivo enviado' });
    }

    const uploadedFile = req.files.image;

    const fileBuffer = uploadedFile.data;
    const fileName = uploadedFile.name;

    const blob = await put(fileName, fileBuffer, { access: 'public' });

    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
