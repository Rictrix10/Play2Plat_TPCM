-- CreateTable
CREATE TABLE "user" (
    "id" SERIAL NOT NULL,
    "email" TEXT NOT NULL,
    "username" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "avatar" TEXT,
    "userTypeId" INTEGER NOT NULL,

    CONSTRAINT "user_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "userType" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "userType_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "platform" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "platform_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "userPlatform" (
    "id" SERIAL NOT NULL,
    "userId" INTEGER NOT NULL,
    "platformId" INTEGER NOT NULL,

    CONSTRAINT "userPlatform_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "company" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "company_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "sequence" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "sequence_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "genre" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "genre_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "game" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "isFree" BOOLEAN NOT NULL,
    "releaseDate" TIMESTAMP(3),
    "pegiInfo" INTEGER NOT NULL,
    "coverImage" TEXT,
    "sequenceId" INTEGER NOT NULL,
    "companyId" INTEGER NOT NULL,

    CONSTRAINT "game_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "platformGame" (
    "id" SERIAL NOT NULL,
    "gameId" INTEGER NOT NULL,
    "platformId" INTEGER NOT NULL,

    CONSTRAINT "platformGame_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "userGame" (
    "id" SERIAL NOT NULL,
    "state" TEXT NOT NULL,
    "userId" INTEGER NOT NULL,
    "gameId" INTEGER NOT NULL,

    CONSTRAINT "userGame_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "gameGenre" (
    "id" SERIAL NOT NULL,
    "gameId" INTEGER NOT NULL,
    "genreId" INTEGER NOT NULL,

    CONSTRAINT "gameGenre_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "dlc" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "gameId" INTEGER NOT NULL,

    CONSTRAINT "dlc_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "avaliation" (
    "id" SERIAL NOT NULL,
    "stars" DOUBLE PRECISION NOT NULL,
    "userId" INTEGER NOT NULL,
    "gameId" INTEGER NOT NULL,

    CONSTRAINT "avaliation_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "userGameComment" (
    "id" SERIAL NOT NULL,
    "comments" TEXT NOT NULL,
    "image" TEXT,
    "userId" INTEGER NOT NULL,
    "gameId" INTEGER NOT NULL,
    "latitude" DOUBLE PRECISION NOT NULL,
    "longitude" DOUBLE PRECISION NOT NULL,

    CONSTRAINT "userGameComment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "userGameFavorite" (
    "id" SERIAL NOT NULL,
    "userId" INTEGER NOT NULL,
    "gameId" INTEGER NOT NULL,

    CONSTRAINT "userGameFavorite_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "_platformTouser" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL
);

-- CreateTable
CREATE TABLE "_gameTogenre" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL
);

-- CreateTable
CREATE TABLE "_gameTouser" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL
);

-- CreateTable
CREATE TABLE "_gameToplatform" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL
);

-- CreateIndex
CREATE UNIQUE INDEX "user_email_key" ON "user"("email");

-- CreateIndex
CREATE UNIQUE INDEX "user_username_key" ON "user"("username");

-- CreateIndex
CREATE UNIQUE INDEX "userPlatform_userId_platformId_key" ON "userPlatform"("userId", "platformId");

-- CreateIndex
CREATE UNIQUE INDEX "game_sequenceId_companyId_key" ON "game"("sequenceId", "companyId");

-- CreateIndex
CREATE UNIQUE INDEX "platformGame_platformId_gameId_key" ON "platformGame"("platformId", "gameId");

-- CreateIndex
CREATE UNIQUE INDEX "userGame_userId_gameId_key" ON "userGame"("userId", "gameId");

-- CreateIndex
CREATE UNIQUE INDEX "gameGenre_gameId_genreId_key" ON "gameGenre"("gameId", "genreId");

-- CreateIndex
CREATE UNIQUE INDEX "avaliation_userId_gameId_key" ON "avaliation"("userId", "gameId");

-- CreateIndex
CREATE UNIQUE INDEX "userGameComment_userId_gameId_key" ON "userGameComment"("userId", "gameId");

-- CreateIndex
CREATE UNIQUE INDEX "userGameFavorite_userId_gameId_key" ON "userGameFavorite"("userId", "gameId");

-- CreateIndex
CREATE UNIQUE INDEX "_platformTouser_AB_unique" ON "_platformTouser"("A", "B");

-- CreateIndex
CREATE INDEX "_platformTouser_B_index" ON "_platformTouser"("B");

-- CreateIndex
CREATE UNIQUE INDEX "_gameTogenre_AB_unique" ON "_gameTogenre"("A", "B");

-- CreateIndex
CREATE INDEX "_gameTogenre_B_index" ON "_gameTogenre"("B");

-- CreateIndex
CREATE UNIQUE INDEX "_gameTouser_AB_unique" ON "_gameTouser"("A", "B");

-- CreateIndex
CREATE INDEX "_gameTouser_B_index" ON "_gameTouser"("B");

-- CreateIndex
CREATE UNIQUE INDEX "_gameToplatform_AB_unique" ON "_gameToplatform"("A", "B");

-- CreateIndex
CREATE INDEX "_gameToplatform_B_index" ON "_gameToplatform"("B");

-- AddForeignKey
ALTER TABLE "user" ADD CONSTRAINT "user_userTypeId_fkey" FOREIGN KEY ("userTypeId") REFERENCES "userType"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userPlatform" ADD CONSTRAINT "userPlatform_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userPlatform" ADD CONSTRAINT "userPlatform_platformId_fkey" FOREIGN KEY ("platformId") REFERENCES "platform"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "game" ADD CONSTRAINT "game_sequenceId_fkey" FOREIGN KEY ("sequenceId") REFERENCES "sequence"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "game" ADD CONSTRAINT "game_companyId_fkey" FOREIGN KEY ("companyId") REFERENCES "company"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "platformGame" ADD CONSTRAINT "platformGame_platformId_fkey" FOREIGN KEY ("platformId") REFERENCES "platform"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "platformGame" ADD CONSTRAINT "platformGame_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userGame" ADD CONSTRAINT "userGame_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userGame" ADD CONSTRAINT "userGame_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "gameGenre" ADD CONSTRAINT "gameGenre_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "gameGenre" ADD CONSTRAINT "gameGenre_genreId_fkey" FOREIGN KEY ("genreId") REFERENCES "genre"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "dlc" ADD CONSTRAINT "dlc_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "avaliation" ADD CONSTRAINT "avaliation_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "avaliation" ADD CONSTRAINT "avaliation_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userGameComment" ADD CONSTRAINT "userGameComment_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userGameComment" ADD CONSTRAINT "userGameComment_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userGameFavorite" ADD CONSTRAINT "userGameFavorite_userId_fkey" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "userGameFavorite" ADD CONSTRAINT "userGameFavorite_gameId_fkey" FOREIGN KEY ("gameId") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_platformTouser" ADD CONSTRAINT "_platformTouser_A_fkey" FOREIGN KEY ("A") REFERENCES "platform"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_platformTouser" ADD CONSTRAINT "_platformTouser_B_fkey" FOREIGN KEY ("B") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_gameTogenre" ADD CONSTRAINT "_gameTogenre_A_fkey" FOREIGN KEY ("A") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_gameTogenre" ADD CONSTRAINT "_gameTogenre_B_fkey" FOREIGN KEY ("B") REFERENCES "genre"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_gameTouser" ADD CONSTRAINT "_gameTouser_A_fkey" FOREIGN KEY ("A") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_gameTouser" ADD CONSTRAINT "_gameTouser_B_fkey" FOREIGN KEY ("B") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_gameToplatform" ADD CONSTRAINT "_gameToplatform_A_fkey" FOREIGN KEY ("A") REFERENCES "game"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_gameToplatform" ADD CONSTRAINT "_gameToplatform_B_fkey" FOREIGN KEY ("B") REFERENCES "platform"("id") ON DELETE CASCADE ON UPDATE CASCADE;
