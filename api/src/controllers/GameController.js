const GameModel = require('../models/GameModel');
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const GameController = {
    createGame: async (req, res) => {
        try {
            const { name, description, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId, averageStars = 0, isDeleted = false } = req.body;
            const newGame = await GameModel.createGame(name, description, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId, averageStars, isDeleted );
            res.status(201).json(newGame);
        } catch (error) {
            console.error('Erro ao criar jogo:', error);
            res.status(500).json({ error: 'Erro ao criar jogo' });
        }
    },
        getGames: async (req, res) => {
            try {
                const games = await GameModel.getGames();

                // Itera sobre cada jogo e calcula a média de estrelas para cada um
                const gamesWithAverage = await Promise.all(games.map(async (game) => {
                    const avaliations = await prisma.avaliation.findMany({
                        where: {
                            gameId: game.id
                        }
                    });
                    const average = calculateAverage(avaliations);
                    return {
                        ...game,
                        average: average
                    };
                }));

                res.json(gamesWithAverage);
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
        const platforms = game.platforms;

        let sequenceName = null; // Inicializa a variável sequenceName como null

        // Verifica se a propriedade sequence é nula antes de acessar sua propriedade name
        if (game.sequence) {
            sequenceName = game.sequence.name;
        }

        const avaliations = await prisma.avaliation.findMany({
            where: {
                gameId: gameId
            }
        });

        const average = calculateAverage(avaliations);

        res.json({
            id: game.id,
            name: game.name,
            description: game.description,
            isFree: game.isFree,
            releaseDate: game.releaseDate,
            pegiInfo: game.pegiInfo,
            coverImage: game.coverImage,
            sequence: sequenceName, // Usa sequenceName em vez de game.sequence.name
            company: game.company.name,
            averageStars: game.averageStars,
            isDeleted: game.isDeleted,
            genres: genres,
            platforms: platforms,
            avaliations: avaliations,
            average: average
        });
    } catch (error) {
        console.error('Erro ao buscar jogo por ID:', error);
        res.status(500).json({ error: 'Erro ao buscar jogo por ID' });
    }
},

    updateGame: async (req, res) => {
        try {
            const gameId = parseInt(req.params.id);
            const { name, description, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId, averageStars, isDeleted } = req.body;

            const updatedGame = await GameModel.updateGame(gameId, {
                name,
                description,
                isFree,
                releaseDate,
                pegiInfo,
                coverImage,
                sequenceId,
                companyId,
                averageStars,
                isDeleted
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


  getGamesByDescendingId: async (req, res) => {
          try {
              const games = await GameModel.getGamesByDescendingId();
              res.json(games);
          } catch (error) {
              console.error('Erro ao buscar jogos por ID decrescente:', error);
              res.status(500).json({ error: 'Erro ao buscar jogos por ID decrescente' });
          }
      },

    getGamesBySameCompanyId: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId, 10);
            if (isNaN(gameId)) {
                return res.status(400).json({ error: 'ID inválido' });
            }

            const games = await GameModel.getGamesBySameCompanyId(gameId);
            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos pela mesma empresa:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos pela mesma empresa' });
        }
    },

    getGamesBySameSequenceId: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId, 10);
            if (isNaN(gameId)) {
                return res.status(400).json({ error: 'ID inválido' });
            }

            const games = await GameModel.getGamesBySameSequenceId(gameId);
            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos pela mesma sequência:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos pela mesma sequência' });
        }
    },

getFilteredGames: async (req, res) => {
    try {
        const { genres, platforms, companies, sequences, free, isAscending, orderType } = req.body;


        const filters = {
            isDeleted: false,
        };


                if (sequences && sequences.length > 0) {
                    const sequencesRecords = await prisma.sequence.findMany({
                        where: { name: { in: sequences } },
                        select: { id: true },
                    });
                    if (sequencesRecords.length === 0) {
                        return res.status(404).json({ error: 'Sequences not found' });
                    }
                    const sequenceIds = sequencesRecords.map(sequence => sequence.id);
                    filters.sequenceId = { in: sequenceIds };
                }

                if (companies && companies.length > 0) {
                    const companiesRecords = await prisma.company.findMany({
                        where: { name: { in: companies } },
                        select: { id: true },
                    });
                    if (companiesRecords.length === 0) {
                        return res.status(404).json({ error: 'Companies not found' });
                    }
                    const companyIds = companiesRecords.map(company => company.id);
                    filters.companyId = { in: companyIds };
                }

        let genreGameIds = [];
        if (genres && genres.length > 0) {
            const genreGames = await prisma.gameGenre.findMany({
                where: { genre: { name: { in: genres } } },
                select: { gameId: true },
            });
            genreGameIds = genreGames.map(gg => gg.gameId);
        }

        let platformGameIds = [];
        if (platforms && platforms.length > 0) {
            const platformGames = await prisma.platformGame.findMany({
                where: { platform: { name: { in: platforms } } },
                select: { gameId: true },
            });
            platformGameIds = platformGames.map(pg => pg.gameId);
        }

        /*
        if (typeof free !== 'undefined') {
            filters.isFree = free;
        }
        */

        if (free === null) {
            // Se free for null, não aplicamos nenhum filtro isFree
        } else {
            // Se free não for null, aplicamos o filtro isFree
            filters.isFree = free;
        }

        const allGames = await prisma.game.findMany();

        let filteredGames = allGames;

        const orderBy = {};
        switch (orderType) {
            case 'alphabetical':
                orderBy.name = isAscending ? 'asc' : 'desc';
                break;
            case 'recent':
                orderBy.id = isAscending ? 'asc' : 'desc';
                break;
            case 'mostFavorited':
                orderBy.userGameFavorites = {
                     _count: isAscending ? 'asc' : 'desc',
                };
                break;
            case 'averageStars':
                    orderBy.averageStars = isAscending ? 'asc' : 'desc';
                    break;
            default:
                orderBy.id = isAscending ? 'asc' : 'desc';
                break;
        }

    const games = await prisma.game.findMany({
        where: {
            sequenceId: filters.sequenceId,
            companyId: filters.companyId,
            isFree: filters.isFree,
            isDeleted: filters.isDeleted,
                AND: [
                    genreGameIds.length > 0 ? { id: { in: genreGameIds } } : undefined,
                    platformGameIds.length > 0 ? { id: { in: platformGameIds } } : undefined,
                ].filter(Boolean),
        },
        orderBy: orderBy,
        include: {
            company: true,
            sequence: true,
            genres: true,
            platforms: true,
            avaliations: true
        },
    });


        res.json(games);
    } catch (error) {
        console.error('Erro ao buscar jogos filtrados:', error);
        res.status(500).json({ error: 'Erro ao buscar jogos filtrados' });
    }
},

        softDeleteGame: async (req, res) => {
            try {
                const gameId = parseInt(req.params.id);
                const game = await GameModel.softDeleteGame(gameId);

                res.json(game);
            } catch (error) {
                console.error('Erro ao excluir jogo:', error);
                res.status(500).json({ error: 'Erro ao excluir jogo' });
            }
        },

  };

  function calculateAverage(avaliations) {
      if (avaliations && avaliations.length > 0) {
          // Calcula a soma total das estrelas das avaliações
          const totalStars = avaliations.reduce((sum, av) => sum + av.stars, 0);

          // Calcula o número total de avaliações para o jogo
          const totalAvaliations = avaliations.length;

          // Calcula a média das estrelas
          return totalStars / totalAvaliations;
      } else {
          return 0; // Se não houver avaliações, retorna 0
      }
  }

  async function getAverage(gameId) {
      try {
          const response = await fetch("https://play2-plat-tpcm.vercel.app/api/avaliation/average/${gameId}");
          if (response.status === 404) {
              // Se não houver avaliações para o jogo, retorna 0
              return 0;
          }
          const data = await response.json();
          return data.average;
      } catch (error) {
          console.error('Erro ao buscar média de estrelas:', error);
          throw new Error('Erro ao buscar média de estrelas');
      }
  }


module.exports = GameController;
