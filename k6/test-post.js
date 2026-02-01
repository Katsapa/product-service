import http from 'k6/http';
import { sleep } from 'k6';
import { BASE_URL, CACHE_MODE } from './config.js';
import { randomString, randomFloat } from './k6_utils.js';

export const options = {
    stages: [
        { duration: '5s', target: 200 },
        { duration: '15s', target: 200 },
        { duration: '5s', target: 0 },
    ],
};

export default function() {
    const productData = {
        name: `Product ${randomString(8)}`,
        price: randomFloat(10, 999.99),
        description: `Test product - ${Date.now()}`
    };

    const url = `${BASE_URL}/api/products?cacheMode=${CACHE_MODE}`;

    const response = http.post(url, JSON.stringify(productData), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (response.status !== 201) {
        console.log(`POST failed: ${response.status}`);
    }

    sleep(0.5);
}