# Points App

An REST API to debit, add, and retrieve user reward point balance.

## Requirements

The app requires the following depedencies to be installed:

- Java 11
- [Maven](https://maven.apache.org/)

## Running the App

### MacOS/Linux

```
./mvnw spring-boot:run
```

### Windows

```
mvnw spring-boot:run
```

## Running Unit Tests

```
mvn verify
```

## API Routes

### Get User Points Balance

```
GET /api/users/{userId}/points/balance
```

### Spend User Points Balance

```
POST /api/users/{userId}/points/spend
```

### Get User Transactions

```
GET /api/users/{userId}/transactions
```
### Add a User Transaction

```
POST /api/users/{userId}/transactions
```


