const express = require('express');
const { put } = require('@vercel/blob');
const fs = require('fs');

const router = express.Router();

router.post('/upload', async (req, res) => {
  try {
    // Caminho para a imagem local
    const filePath = '../images/imagegame.png';

    // Lê o conteúdo do arquivo
    const fileContent = fs.readFileSync(filePath);

    // Armazena o arquivo na Vercel
    const blob = await put('image.png', fileContent, { access: 'public' });

    // Retorna a URL do arquivo armazenado
    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
