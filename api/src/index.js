const express = require('express');
const cors = require('cors');
const userRoute = require('./routes/UserRoute');
const userTypeRoute = require('./routes/UserTypeRoute');
const genreRoute = require('./routes/GenreRoute');
const platformRoute = require('./routes/PlatformRoute');
const companyRoute = require('./routes/CompanyRoute');
const sequenceRoute = require('./routes/SequenceRoute');
const gameRoute = require('./routes/GameRoute');
const dlcRoute = require('./routes/DLCRoute');
const avaliationRoute = require('./routes/AvaliationRoute');
const usergamecommentsRoute = require('./routes/UserGameCommentsRoute');
const usergameRoute = require('./routes/UserGameRoute');
const usergameFavoriteRoute = require('./routes/UserGameFavoriteRoute');
const gameGenreRoute = require('./routes/GameGenreRoute');
const platformGameRoute = require('./routes/PlatformGameRoute');
const userPlatformRoute = require('./routes/UserPlatformRoute');

const app = express();

app.use(cors());
app.use(express.json());

const apiRouter = express.Router();

app.use('/api', apiRouter);

apiRouter.use(userRoute);
apiRouter.use(userTypeRoute);
apiRouter.use(genreRoute);
apiRouter.use(platformRoute);
apiRouter.use(companyRoute);
apiRouter.use(sequenceRoute);
apiRouter.use(gameRoute);
apiRouter.use(dlcRoute);
apiRouter.use(avaliationRoute);
apiRouter.use(usergamecommentsRoute);
apiRouter.use(usergameRoute);
apiRouter.use(usergameFavoriteRoute);
apiRouter.use(gameGenreRoute);
apiRouter.use(platformGameRoute);
apiRouter.use(userPlatformRoute);

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});
