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
            return await prisma.company.findMany();
    }
};

module.exports = CompanyModel;
