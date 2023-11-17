# How to run this project with docker

Docker runs MySQL database and spring rest API with predefined data of goods!

Rest API:
* port - 8080

MySql Database:
* port: 3306

### Clone project
```shell
git clone https://github.com/Snoke19/order_management.git
```
### Go to the project folder and run docker-compose

```shell
cd ./order_management

docker compose up
```

### Run tests with Maven

```shell
cd ./order_management

./mvnw clear test
```

# Endpoints

## Manager

### Add a new good:

```shell
curl -X POST --location "http://localhost:8080/api/v1/good" \
    -H "Content-Type: application/json" \
    -d "{
          \"description\": \"IPhone 11\",
          \"name\": \"IPhone 11 - best phone!\",
          \"price\": 40000,
          \"quantity\": 5
        }"
```

### Get all available goods:

```shell
curl -X GET --location "http://localhost:8080/api/v1/goods" \
    -H "Content-Type: application/json"
```
```json
[
  {
    "name": "iPhone 12",
    "description": "Flagship smartphone from Apple",
    "price": 1000,
    "quantity": 50,
    "id": 1
  }
}
```

## Client

### Create a new order and you can add many good for buying:

```shell
curl -X POST --location "http://localhost:8080/api/v1/order" \
    -H "Content-Type: application/json" \
    -d "{
          \"infoGoodOrders\": [{
            \"idGood\": 7,
            \"quantityBuy\": 2
          }]
        }"
```

### Get all orders:

```shell
curl -X GET --location "http://localhost:8080/api/v1/orders" \
    -H "Content-Type: application/json"
```

### Pay for order

```shell
curl -X POST --location "http://localhost:8080/api/v1/order/pay" \
    -H "Content-Type: application/json" \
    -d "{
          \"orderId\": 1
        }"
```
