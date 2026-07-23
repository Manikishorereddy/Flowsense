const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const { expect } = require('chai');

const BASE_URL = process.env.TEST_BASE_URL || 'http://localhost:8080';

describe('FlowSense Web Application E2E Tests', function () {
    this.timeout(40000);
    let driver;

    before(async function () {
        const options = new chrome.Options();
        options.addArguments('--headless=new');
        options.addArguments('--no-sandbox');
        options.addArguments('--disable-dev-shm-usage');
        options.addArguments('--disable-gpu');
        options.addArguments('--window-size=1280,800');

        driver = await new Builder()
            .forBrowser('chrome')
            .setChromeOptions(options)
            .build();
    });

    after(async function () {
        if (driver) {
            await driver.quit();
        }
    });

    it('1. Splash Page - Should render brand title and subtitle', async function () {
        await driver.get(`${BASE_URL}/index.html`);
        const title = await driver.getTitle();
        expect(title).to.include('FlowSense AI');

        const brandText = await driver.findElement(By.className('brand-name')).getText();
        expect(brandText).to.include('FlowSense');

        const subtitleText = await driver.findElement(By.className('subtitle')).getText();
        expect(subtitleText).to.equal('Predict Crowds. Save Time.');
    });

    it('2. Role Selection Page - Should display User and Organization roles', async function () {
        await driver.get(`${BASE_URL}/role-selection.html`);
        const title = await driver.getTitle();
        expect(title).to.include('Choose Role');

        const roleCards = await driver.findElements(By.className('role-card'));
        expect(roleCards.length).to.equal(2);

        const cardHeadings = await Promise.all(roleCards.map(card => card.findElement(By.tagName('h3')).getText()));
        expect(cardHeadings).to.include('Organization');
        expect(cardHeadings).to.include('User');
    });

    it('3. User Login Page - Should contain required login inputs', async function () {
        await driver.get(`${BASE_URL}/login-user.html`);
        const title = await driver.getTitle();
        expect(title).to.include('User Login');

        const fullNameInput = await driver.findElement(By.id('fullName'));
        const emailInput = await driver.findElement(By.id('email'));
        const passwordInput = await driver.findElement(By.id('password'));
        const submitBtn = await driver.findElement(By.className('primary-btn'));

        expect(await fullNameInput.isDisplayed()).to.be.true;
        expect(await emailInput.isDisplayed()).to.be.true;
        expect(await passwordInput.isDisplayed()).to.be.true;
        expect(await submitBtn.isDisplayed()).to.be.true;
    });

    it('4. Organization Login Page - Should contain required login inputs', async function () {
        await driver.get(`${BASE_URL}/login-org.html`);
        const title = await driver.getTitle();
        expect(title).to.include('Organization Login');

        const emailInput = await driver.findElement(By.id('email'));
        const passwordInput = await driver.findElement(By.id('password'));
        const submitBtn = await driver.findElement(By.className('primary-btn'));

        expect(await emailInput.isDisplayed()).to.be.true;
        expect(await passwordInput.isDisplayed()).to.be.true;
        expect(await submitBtn.isDisplayed()).to.be.true;
    });

    it('5. User Signup Page - Should render form inputs properly', async function () {
        await driver.get(`${BASE_URL}/signup-user.html`);
        const title = await driver.getTitle();
        expect(title).to.include('Create Account');

        const emailInput = await driver.wait(until.elementLocated(By.id('email')), 5000);
        const submitBtn = await driver.wait(until.elementLocated(By.className('primary-btn')), 5000);
        expect(await emailInput.isDisplayed()).to.be.true;
        expect(await submitBtn.isDisplayed()).to.be.true;
    });

    it('6. Organization Signup Page - Should render form inputs properly', async function () {
        await driver.get(`${BASE_URL}/signup-org.html`);
        const title = await driver.getTitle();
        expect(title).to.include('Create Organization');

        const emailInput = await driver.wait(until.elementLocated(By.id('email')), 5000);
        const submitBtn = await driver.wait(until.elementLocated(By.className('primary-btn')), 5000);
        expect(await emailInput.isDisplayed()).to.be.true;
        expect(await submitBtn.isDisplayed()).to.be.true;
    });

    it('7. User Dashboard Page - Should load navigation and sections', async function () {
        await driver.get(`${BASE_URL}/user-home.html`);
        const title = await driver.getTitle();
        expect(title).to.include('Dashboard');

        const bodyText = await driver.findElement(By.tagName('body')).getText();
        expect(bodyText).to.not.be.empty;
    });

    it('8. Crowd Monitor Page - Should render monitoring container', async function () {
        await driver.get(`${BASE_URL}/monitor.html`);
        const title = await driver.getTitle();
        expect(title).to.include('Live Feeds');

        const body = await driver.findElement(By.tagName('body'));
        expect(await body.isDisplayed()).to.be.true;
    });
});
