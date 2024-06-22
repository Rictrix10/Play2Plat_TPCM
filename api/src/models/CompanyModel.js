const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const CompanyModel = {
    createCompany: async (name) => {
        return await prisma.company.create({
            data: {
                name
            }
        });
    },
    getCompanies: async () => {
        return await prisma.company.findMany({
            orderBy: { name: 'asc' }
        });
    },
    getRandomCompanyName: async () => {
        // Get companies with at least one associated game
        const companies = await prisma.company.findMany({
            select: {
                id: true,
                name: true
            },
            where: {
                games: {
                    some: {} // Only companies with at least one associated game
                }
            }
        });

        // Filter companies with at least 7 games
        const companiesWithGamesCount = await Promise.all(companies.map(async company => {
            const gamesCount = await prisma.game.count({
                where: {
                    companyId: company.id
                }
            });
            return {
                ...company,
                gamesCount
            };
        }));

        const filteredCompanies = companiesWithGamesCount.filter(company => company.gamesCount >= 7);

        if (filteredCompanies.length === 0) {
            return null;
        }

        const randomIndex = Math.floor(Math.random() * filteredCompanies.length);
        return filteredCompanies[randomIndex].name;
    }
};

module.exports = CompanyModel;
