BASE_URL ?= http://localhost:8080
CACHE_MODE ?= MANUAL

.PHONY: help
help:
	@echo "Команды для запуска k6 тестов:"
	@echo "  make post      - Тест создания продуктов"
	@echo "  make get       - Тест получения продукта по ID"
	@echo "  make put       - Тест обновления продукта"
	@echo "  make delete    - Тест удаления продукта"
	@echo "  make all       - Запустить все тесты"
	@echo ""
	@echo "Настройки:"
	@echo "  BASE_URL       - URL сервера (по умолчанию: http://localhost:8080)"
	@echo "  CACHE_MODE     - Режим кэширования (по умолчанию: MANUAL)"
	@echo ""
	@echo "Примеры:"
	@echo "  make get CACHE_MODE=NONE_CACHE"
	@echo "  make post BASE_URL=http://prod-server:8080"

# Тест создания продуктов
.PHONY: post
post:
	@echo "Запуск теста создания продуктов..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=$(CACHE_MODE) k6 run test-post.js

# Тест получения продукта по ID
.PHONY: get
get:
	@echo "Запуск теста получения продукта по ID..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=$(CACHE_MODE) k6 run test-get.js

.PHONY: get-none-cache
get-none-cache:
	@echo "Запуск теста получения продукта по ID (mode=NONE_CACHE)..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=NONE_CACHE k6 run test-get.js

.PHONY: get-manual-cache
get-manual-cache:
	@echo "Запуск теста получения продукта по ID (mode=MANUAL)..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=MANUAL k6 run test-get.js

.PHONY: get-spring-cache
get-spring-cache:
	@echo "Запуск теста получения продукта по ID (mode=SPRING)..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=SPRING k6 run test-get.js

# Тест обновления продукта
.PHONY: put
put:
	@echo "Запуск теста обновления продукта..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=$(CACHE_MODE) k6 run test-put.js

# Тест удаления продукта
.PHONY: delete
delete:
	@echo "Запуск теста удаления продукта..."
	@BASE_URL=$(BASE_URL) CACHE_MODE=$(CACHE_MODE) k6 run test-delete.js

# Запуск всех тестов последовательно
.PHONY: all
all: post get put delete
	@echo "Все тесты завершены!"