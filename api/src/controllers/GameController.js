const GameModel = require('../models/GameModel');

const GameController = {
    createGame: async (req, res) => {
        try {
            const { name, description, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId  } = req.body;
            const newGame = await GameModel.createGame(name, description, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId );
            res.status(201).json(newGame);
        } catch (error) {
            console.error('Erro ao criar jogo:', error);
            res.status(500).json({ error: 'Erro ao criar jogo' });
        }
    },
        getGames: async (req, res) => {
            try {
                const games = await GameModel.getGames();
                res.json(games);
            } catch (error) {
                console.error('Erro ao buscar jogos:', error);
                res.status(500).json({ error: 'Erro ao buscar jogos' });
            }
        },
   getGameById: async (req, res) => {
       try {
           const gameId = parseInt(req.params.id, 10); // Converter o ID para número inteiro
           if (isNaN(gameId)) {
               return res.status(400).json({ error: 'ID inválido' });
           }
           const game = await GameModel.getGameById(gameId);
           if (!game) {
               return res.status(404).json({ error: 'Jogo não encontrado' });
           }

           const genres = game.genres;
           const platforms = game.platforms

           res.json({
               id: game.id,
               name: game.name,
               description: game.description,
               isFree: game.isFree,
               releaseDate: game.releaseDate,
               pegiInfo: game.pegiInfo,
               coverImage: game.coverImage,
               sequence: game.sequence.name,
               company: game.company.name,
               genres: genres,
               platforms: platforms
           });
       } catch (error) {
           console.error('Erro ao buscar jogo por ID:', error);
           res.status(500).json({ error: 'Erro ao buscar jogo por ID' });
       }
   },





    updateGame: async (req, res) => {
        try {
            const gameId = parseInt(req.params.id);
            const { name, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId } = req.body;

            const updatedGame = await GameModel.updateGame(gameId, {
                name,
                isFree,
                releaseDate,
                pegiInfo,
                coverImage,
                sequenceId,
                companyId
            });

            res.json(updatedGame);
        } catch (error) {
            console.error('Erro ao atualizar jogo:', error);
            res.status(500).json({ error: 'Erro ao atualizar jogo' });
        }
    },

    deleteGame: async (req, res) => {
        try {
            const gameId = parseInt(req.params.id);

            await GameModel.deleteGame(gameId);

            res.sendStatus(204);
        } catch (error) {
            console.error('Erro ao excluir jogo:', error);
            res.status(500).json({ error: 'Erro ao excluir jogo' });
        }
    },


        getGamesByParams: async (req, res) => {
            try {
                const { name, isFree } = req.query;
                console.log('Parâmetros de consulta recebidos:', { name, isFree });
                const parsedIsFree = isFree === undefined ? undefined : isFree.toLowerCase() === 'true';
                console.log('Parâmetro isFree convertido:', parsedIsFree);
                const games = await GameModel.getGamesByParams(name, parsedIsFree);
                res.json(games);
            } catch (error) {
                console.error('Erro ao buscar jogos por parâmetros:', error);
                res.status(500).json({ error: 'Erro ao buscar jogos por parâmetros' });
            }
        },

    getGamesByCompany: async (req, res) => {
        try {
            const { companyName } = req.params;

            if (!companyName) {
                return res.status(400).json({ error: 'Informe o nome da empresa' });
            }

            const games = await GameModel.getGamesByCompany(null, companyName);

            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos por empresa:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos por empresa' });
        }
    },

    getGamesBySequence: async (req, res) => {
        try {
            const { sequenceName } = req.params;

            if (!sequenceName) {
                return res.status(400).json({ error: 'Informe o nome da sequencia' });
            }

            const games = await GameModel.getGamesBySequence(null, sequenceName);

            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos por sequencia:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos por sequencia' });
        }
    },

        getGamesByPartialName: async (req, res) => {
            try {
                const { name } = req.params;
                if (!name) {
                    return res.status(400).json({ error: 'O parâmetro name é obrigatório' });
                }

                const games = await GameModel.getGamesByPartialName(name);
                res.json(games);
            } catch (error) {
                console.error('Erro ao buscar jogos por nome parcial:', error);
                res.status(500).json({ error: 'Erro ao buscar jogos por nome parcial' });
            }
        },

        getGamesByFreeStatus: async (req, res) => {
            try {
                const { isFree } = req.params;
                const parsedIsFree = isFree.toLowerCase() === 'true'; // Converte a string para booleano

                const games = await GameModel.getGamesByParams(null, parsedIsFree);
                const simplifiedGames = games.map(game => ({
                    id: game.id,
                    name: game.name,
                    coverImage: game.coverImage
                }));

                res.json(simplifiedGames);
            } catch (error) {
                console.error('Erro ao buscar jogos por status gratuito:', error);
                res.status(500).json({ error: 'Erro ao buscar jogos por status gratuito' });
            }
        },


  };

module.exports = GameController;
