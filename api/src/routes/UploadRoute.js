const express = require('express');
const { put } = require('@vercel/blob');
const fs = require('fs');
const path = require('path');

const router = express.Router();

router.post('/upload', async (req, res) => {
  try {
    const imageName = req.body.imageName;

    if (!imageName) {
      return res.status(400).json({ error: 'O nome da imagem é obrigatório no corpo da requisição' });
    }

    // Altere o diretório de destino aqui
    const filePath = '/storage/emulated/0/Android/data/com.example.play2plat_tpcm/files/' + imageName;

    if (!fs.existsSync(filePath)) {
      return res.status(404).json({ error: 'Arquivo não encontrado' });
    }

    const fileContent = fs.readFileSync(filePath);

    const blob = await put(imageName, fileContent, { access: 'public' });

    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
