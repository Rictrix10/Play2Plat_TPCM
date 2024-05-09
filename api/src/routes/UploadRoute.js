const express = require('express');
const { put } = require('@vercel/blob');
const fs = require('fs');
const path = require('path');
const multer = require('multer');

const router = express.Router();
const upload = multer({ dest: 'uploads/' }); // Define o diretório onde os arquivos serão temporariamente salvos

router.post('/upload', upload.single('image'), async (req, res) => {
  try {
    const imageFile = req.file;

    if (!imageFile) {
      return res.status(400).json({ error: 'Nenhuma imagem foi enviada' });
    }

    const fileName = imageFile.originalname;
    const filePath = imageFile.path;

    const fileContent = fs.readFileSync(filePath);

    const blob = await put(fileName, fileContent, { access: 'public' });

    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
