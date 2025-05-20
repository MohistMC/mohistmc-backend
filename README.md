<div align="center">
  <h1 align="center">MohistMC REST API</h1>

### Backend for MohistMC's website
![Java](https://img.shields.io/badge/java-007ACC?style=for-the-badge&logo=java&logoColor=white)
![SpringBoot](https://img.shields.io/badge/springboot-43853D?style=for-the-badge&logo=springboot&logoColor=white)
</div>

## About

This is the rest api for MohistMC's website.     

## Project setup

Clone the repository and open it in your favorite IDE (IntelliJ IDEA is recommended).       

Copy the `.env.dist` file to `.env` and fill in the values.

## Running the project

Run the postgres database (will use credentials from `.env` file):
```bash
$ docker compose --env-file .env.dev up -d
```

Then, start the app using your IDE.
