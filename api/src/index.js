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

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});
