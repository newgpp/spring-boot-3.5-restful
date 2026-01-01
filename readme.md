#### 创建docker网络

```shell
docker network create infra-net
```

#### mysql测试数据

```shell
docker run -d \
  --name mariadb \
  --network infra-net \
  --restart unless-stopped \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -v /data/mariadb:/var/lib/mysql \
  mariadb:10.11


```

```sql
CREATE DATABASE IF NOT EXISTS `test`;
USE `test`;

CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `ext_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '扩展信息Json' CHECK (json_valid(`ext_json`)),
  `create_time` datetime(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `t_user` (`username`, `password`, `age`, `ext_json`, `create_time`) VALUES
('zhangsan', 'pw123456', 22, '{"role": "admin", "loginCount": 5}', '2025-01-10 10:00:00.000'),
('lisi', 'abc888', 25, '{"dept": "Sales", "tags": ["active", "top"]}', '2025-01-12 11:30:15.500'),
('wangwu', 'pass999', 30, '{"city": "Beijing", "interests": ["coding", "gaming"]}', '2025-02-05 09:15:20.123'),
('zhaoliu', 'secret777', 19, '{"vip": true, "level": 3}', '2025-03-01 14:22:11.000'),
('chenqi', 'pwd000', 28, '{"remark": "New user from marketing"}', '2025-04-15 16:45:30.888'),
('maba', 'secure11', 35, '{"skills": ["Java", "SQL"], "years": 10}', '2025-05-20 08:00:00.111'),
('zhoujiu', 'hello2025', 21, '{"theme": "dark", "lang": "zh-CN"}', '2025-06-12 22:10:05.444'),
('wushi', 'test666', 40, '{"manager": true, "employees": 15}', '2025-07-01 10:05:59.999'),
('xiaoming', 'xm123', 24, '{"source": "mobile", "app_ver": "2.1.0"}', '2025-08-19 12:30:00.000'),
('xiaohong', 'xh456', 23, '{"gender": "female", "edu": "Bachelor"}', '2025-09-05 17:15:45.222'),
('david_w', 'dw_pass', 32, '{"country": "US", "timezone": "PST"}', '2025-10-10 03:00:00.666'),
('linda_k', 'lk_789', 27, '{"dept": "HR", "remote": false}', '2025-11-22 13:40:10.777'),
('ironman', 'stark1', 45, '{"suit": "Mark 85", "hero": true}', '2025-12-01 09:00:00.000'),
('spiderman', 'peterP', 18, '{"school": "Midtown High", "powers": ["web", "climb"]}', '2025-12-15 15:55:55.555'),
('batman', 'wayne1', 38, '{"city": "Gotham", "rich": true}', '2025-12-23 18:30:00.000');
```

#### mongo测试数据

```shell
docker run -d \
  --name mongodb \
  --network infra-net \
  --restart unless-stopped \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=123456 \
  -v /data/mongodb:/data/db \
  mongodb/mongodb-community-server:7.0-ubuntu2204

```

```javascript
use test;
db.t_user.insertMany([
  { "username": "zhangsan", "password": "pw123456", "age": 22, "ext_json": {"role": "admin", "loginCount": 5}, "create_time": ISODate("2025-01-10T10:00:00.000+08:00") },
  { "username": "lisi", "password": "abc888", "age": 25, "ext_json": {"dept": "Sales", "tags": ["active", "top"]}, "create_time": ISODate("2025-01-12T11:30:15.500+08:00") },
  { "username": "wangwu", "password": "pass999", "age": 30, "ext_json": {"city": "Beijing", "interests": ["coding", "gaming"]}, "create_time": ISODate("2025-02-05T09:15:20.123+08:00") },
  { "username": "zhaoliu", "password": "secret777", "age": 19, "ext_json": {"vip": true, "level": 3}, "create_time": ISODate("2025-03-01T14:22:11.000+08:00") },
  { "username": "chenqi", "password": "pwd000", "age": 28, "ext_json": {"remark": "New user from marketing"}, "create_time": ISODate("2025-04-15T16:45:30.888+08:00") },
  { "username": "maba", "password": "secure11", "age": 35, "ext_json": {"skills": ["Java", "SQL"], "years": 10}, "create_time": ISODate("2025-05-20T08:00:00.111+08:00") },
  { "username": "zhoujiu", "password": "hello2025", "age": 21, "ext_json": {"theme": "dark", "lang": "zh-CN"}, "create_time": ISODate("2025-06-12T22:10:05.444+08:00") },
  { "username": "wushi", "password": "test666", "age": 40, "ext_json": {"manager": true, "employees": 15}, "create_time": ISODate("2025-07-01T10:05:59.999+08:00") },
  { "username": "xiaoming", "password": "xm123", "age": 24, "ext_json": {"source": "mobile", "app_ver": "2.1.0"}, "create_time": ISODate("2025-08-19T12:30:00.000+08:00") },
  { "username": "xiaohong", "password": "xh456", "age": 23, "ext_json": {"gender": "female", "edu": "Bachelor"}, "create_time": ISODate("2025-09-05T17:15:45.222+08:00") },
  { "username": "david_w", "password": "dw_pass", "age": 32, "ext_json": {"country": "US", "timezone": "PST"}, "create_time": ISODate("2025-10-10T03:00:00.666+08:00") },
  { "username": "linda_k", "password": "lk_789", "age": 27, "ext_json": {"dept": "HR", "remote": false}, "create_time": ISODate("2025-11-22T13:40:10.777+08:00") },
  { "username": "ironman", "password": "stark1", "age": 45, "ext_json": {"suit": "Mark 85", "hero": true}, "create_time": ISODate("2025-12-01T09:00:00.000+08:00") },
  { "username": "spiderman", "password": "peterP", "age": 18, "ext_json": {"school": "Midtown High", "powers": ["web", "climb"]}, "create_time": ISODate("2025-12-15T15:55:55.555+08:00") },
  { "username": "batman", "password": "wayne1", "age": 38, "ext_json": {"city": "Gotham", "rich": true}, "create_time": ISODate("2025-12-23T18:30:00.000+08:00") }
]);
```

#### docker构建&启动

```shell
# 构建
docker build -t restful:1.0 .

# 启动
docker run -d \
  --name restful \
  --network infra-net \
  -p 10042:10042 \
  restful:1.0

```

#### 常用测试用例

[点击此处运行接口测试](./api-test.http)
