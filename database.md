Suggestion for fast podman postgres data base spin up

## Network

```bash
podman network create home-banking-net
```

## Postgres

```bash
podman run -d \
  --name home-banking-db \
  --network home-banking-net \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=example \
  -e POSTGRES_DB=home-banking-db \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:16
```

## Adminer

```bash
podman run -d \
  --name adminer \
  --network home-banking-net \
  -p 8081:8080 \
  adminer
```
