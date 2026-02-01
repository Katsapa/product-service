import http from 'k6/http';
import { sleep } from 'k6';
import { BASE_URL, CACHE_MODE } from './config.js';
import { randomString, randomFloat } from './k6_utils.js';

export const options = {
    stages: [
        { duration: '5s', target: 50 },
        { duration: '15s', target: 50 },
        { duration: '5s', target: 0 },
    ],
};

export default function() {
    // Создаем продукт для удаления
    const productData = {
        name: `Product to delete ${randomString(8)}`,
        price: randomFloat(10, 999.99),
        description: `For deletion - ${Date.now()}`
    };

    const createUrl = `${BASE_URL}/api/products?cacheMode=${CACHE_MODE}`;

    const createResponse = http.post(createUrl, JSON.stringify(productData), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (createResponse.status === 201) {
        const createdProduct = JSON.parse(createResponse.body);
        const productId = createdProduct.id;

        // Удаляем продукт
        const deleteUrl = `${BASE_URL}/api/products/${productId}?cacheMode=${CACHE_MODE}`;

        const deleteResponse = http.del(deleteUrl, null, {
            headers: { 'Content-Type': 'application/json' },
        });

        if (deleteResponse.status !== 204) {
            console.log(`DELETE failed: ${deleteResponse.status} for ID: ${productId}`);
        }
    } else {
        console.log(`Product creation failed: ${createResponse.status}`);
    }

    sleep(1.0);
}
