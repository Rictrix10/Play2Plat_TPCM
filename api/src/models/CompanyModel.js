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
        const companies = await prisma.company.findMany({
            select: {
                id: true,
                name: true
            },
            where: {
                games: {
                    some: {} // Only companies with at least one associated game
                },
                _count: {
                    games: {
                       gte: 7
                    }
                }
            }
        });
        if (companies.length === 0) {
            return null;
        }
        const randomIndex = Math.floor(Math.random() * companies.length);
        return companies[randomIndex].name;
    }
};

module.exports = CompanyModel;
