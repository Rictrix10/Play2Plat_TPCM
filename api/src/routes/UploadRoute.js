const express = require('express');
const { put } = require('@vercel/blob');
const fs = require('fs');
const path = require('path');

const router = express.Router();

router.post('/upload', async (req, res) => {
  try {
    // Verifica se o corpo da requisição contém o parâmetro 'filePath'
    const filePath = req.body.filePath;

    if (!filePath) {
      // Se o parâmetro 'filePath' não estiver presente no corpo da requisição, retorna um erro
      return res.status(400).json({ error: 'O parâmetro "filePath" é obrigatório' });
    }

    // Verifica se o arquivo especificado existe
    if (!fs.existsSync(filePath)) {
      return res.status(404).json({ error: 'Arquivo não encontrado' });
    }

    // Lê o conteúdo do arquivo
    const fileContent = fs.readFileSync(filePath);

    // Extrai o nome do arquivo da parte final do caminho
    const fileName = path.basename(filePath);

    // Armazena o arquivo na Vercel
    const blob = await put(fileName, fileContent, { access: 'public' });

    // Retorna a URL do arquivo armazenado
    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
