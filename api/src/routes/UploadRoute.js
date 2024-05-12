const express = require('express');
const { put } = require('@vercel/blob');
const fs = require('fs');
const path = require('path');

const router = express.Router();

router.post('/upload', async (req, res) => {
  try {
    const imageContent = req.body.imageContent;
    const imageName = req.body.imageName;

    /*
    if (!imageContent || !imageName) {
      return res.status(400).json({ error: 'O conteúdo e o nome da imagem são obrigatórios no corpo da requisição' });
    }
    */

    const blob = await put(imageName, imageContent, { access: 'public' });

    res.json({ url: blob.url });
  } catch (error) {
    console.error(error);
  }
});

module.exports = router;
