<div align="center">
  <h1 align="center">Tomuss Grade API</h1>

### Backend for MohistMC's website
![NodeJS](https://img.shields.io/badge/node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)
![Typescript](https://img.shields.io/badge/typescript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![NestJS](https://img.shields.io/badge/nest-black?style=for-the-badge&logo=nestjs&logoColor=white)
![MikroORM](https://img.shields.io/badge/mikroorm-FF6F61?style=for-the-badge&logo=typeorm&logoColor=white)
</div>

## About

This is the backend for MohistMC's website.     

## Project setup

```bash
$ docker compose up -d
```

```bash
$ pnpm install
```

## Compile and run the project

```bash
# dev mode
$ pnpm run start:dev
```

## Run tests

```bash
# unit tests
$ pnpm run test

# e2e tests
$ pnpm run test:e2e

# test coverage
$ pnpm run test:cov
```

## Run in production mode

Set the `NODE_ENV` environment variable to `production` and run the following command:
```bash
$ docker compose up -d
```

## License

Nest is [GPL-3.0 licensed](LICENSE).
