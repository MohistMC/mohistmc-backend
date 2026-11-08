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

The development settings are in `.env.dev`, read by the `dev` profile. Fill in `GITHUB_USERNAME` and `GITHUB_TOKEN`
to synchronize the builds from GitHub: a classic token without any scope is enough, the repositories are public.

## Running the project

Run the postgres database (will use credentials from `.env.dev`) and S3Mock, which stands in for the builds' bucket:
```bash
$ docker compose --env-file .env.dev up -d
```

Then, start the app with the `dev` profile, from your IDE or with Gradle:
```bash
$ ./gradlew bootRun --args='--spring.profiles.active=dev'
```
