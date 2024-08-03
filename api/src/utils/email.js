const nodemailer = require('nodemailer');

const sendEmail = async (option) => {
    const transporter = nodemailer.createTransport({
        host: 'sandbox.smtp.mailtrap.io',
        port: 25,
        auth: {
            user: '3f21e39269d693',
            pass: '4fc5203d229eb7',
        }
    });

        /*
        const transporter = nodemailer.createTransport({
            host: process.env.EMAIL_HOST,
            port: process.env.EMAIL_PORT,
            auth: {
                user: process.env.EMAIL_USER,
                pass: process.env.EMAIL_PASSWORD,
            }
        })
        */

    const emailOptions = {
        from: 'ddkricplay2plat@gmail.com',
        to: option.email,
        subject: option.subject,
        text: option.message
    };

        try {
            await transporter.sendMail(emailOptions);
        } catch (error) {
            console.error('Error sending email:', error);
            throw error;
        }

};

module.exports = sendEmail;
