# How to run this project with docker

Docker run mysql database and spring rest api with predefined data of goods!

Rest Api:
* port - 8080

MySql Database:
* port: 3306

### Clone project
```shell
git clone https://github.com/Snoke19/order_management.git
```
### Go to project folder and run docker compose

```shell
cd ./order_management

docker compose up
```

### Run tests with maven

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

### Get all goods:

```shell
curl -X GET --location "http://localhost:8080/api/v1/goods" \
    -H "Content-Type: application/json"
```

## Client

### Creat a new order:

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
