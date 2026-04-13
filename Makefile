.PHONY: up down build logs restart db-shell backend-shell

# Start all services (detached)
up:
	docker compose up -d

# Stop and remove containers
down:
	docker compose down

# Build all images
build:
	docker compose build

# Follow logs (Ctrl+C to stop)
logs:
	docker compose logs -f

# Restart all services
restart:
	docker compose restart

# Open a MySQL shell
db-shell:
	docker compose exec mysql mysql -u $${DB_USER:-happypaws} -p$${DB_PASSWORD:-happypaws} $${DB_NAME:-clinic}

# Open a shell in the backend container
backend-shell:
	docker compose exec backend sh
