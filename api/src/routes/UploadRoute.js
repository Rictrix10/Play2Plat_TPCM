const express = require('express');
const { put } = require('@vercel/blob');
const fs = require('fs');
const path = require('path');

const router = express.Router();

router.post('/upload', async (req, res) => {
  try {
    // Verifica se o corpo da requisição contém o nome da imagem
    const imageName = req.body.imageName;

    if (!imageName) {
      // Se o nome da imagem não estiver presente no corpo da requisição, retorna um erro
      return res.status(400).json({ error: 'O nome da imagem é obrigatório no corpo da requisição' });
    }

    // Constrói o caminho completo da imagem usando o nome fornecido no corpo da requisição
    const filePath = path.join(__dirname, '../../images/', imageName);

    // Verifica se o arquivo especificado existe
    if (!fs.existsSync(filePath)) {
      return res.status(404).json({ error: 'Arquivo não encontrado' });
    }

    // Lê o conteúdo do arquivo
    const fileContent = fs.readFileSync(filePath);

    // Armazena o arquivo na Vercel
    const blob = await put(imageName, fileContent, { access: 'public' });

    // Retorna a URL do arquivo armazenado
    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Erro ao fazer upload da imagem' });
  }
});

module.exports = router;
