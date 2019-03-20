# 风控系统
## 部署说明

## 概述

## 环境

### 服务器

| 服务器名称      | 用途       | IP    |  域名 |
| --------   | --------   | -----:   | :----: |
| infosec-PowerEdge-R730   | 门户 、微服务、应用系统、DevOps       |   162.105.23.211    |   home1.aichain.ent.cn  |
| infosec-PowerEdge-R730-2   | 大数据、人工智能        |   162.105.23.132    |   home2.aichain.ent.cn  |
--------------------- 

```
ping home1.aichain.net.cn
```

```
infosec@infosec-PowerEdge-R730:~/work/projects/ms$ ping home1.aichain.net.cn
PING home1.aichain.net.cn (162.105.23.211) 56(84) bytes of data.
64 bytes from infosec-PowerEdge-R730 (162.105.23.211): icmp_seq=1 ttl=64 time=0.038 ms
64 bytes from infosec-PowerEdge-R730 (162.105.23.211): icmp_seq=2 ttl=64 time=0.027 ms
--- home1.aichain.net.cn ping statistics ---
2 packets transmitted, 2 received, 0% packet loss, time 1001ms
rtt min/avg/max/mdev = 0.027/0.032/0.038/0.007 ms

```

微服务代码与文档

```
http://home1.aichain.net.cn:10080/app/ms
```

登录服务器：

```
ssh infosec@home1.aichain.net.cn
```

常用的screen命令 (C-a是Ctrl+a)
>新建和切换屏幕，便于关闭boot进程

|命令  | 作用 |
|--------|:------:|
|C-a c|创建一个新的运行shell的窗口并切换到该窗口|
|C-a n	|切换到下一个窗口|
|C-a p	|切换到前一个窗口|

## 一些自定义别名
```
scls // 显示所有窗口
scms // 进入微服务窗口
scsl // 进入微服务slim窗口
cdms  // 进入~/work/projects/ms目录
gitp // git pull 拉取最新代吗
cdri  // 进入~/work/projects/ms/riskassessmentslim目录
runms // mvn spring-boot:run 启动微服务
```

## 部署微服务（Spring Boot）
```
scms  // 恢复微服务所在的会话
Ctrl-c  // 终止服务
cdri // 进入~/work/projects/ms/riskassessmentslim目录
runms // 启动微服务
Ctrl-a-d  // 离开会话（关闭当前窗口，不会关闭服务）
```

## 部署Web客户端（Nodejs）
```
npm install
npm start
```

## 大数据套件（cloudera CDH）
```
http://home1.aichain.net.cn:7180

    Username: cloudera
    Password: cloudera

```

```
docker run --hostname=home1.aichain.net.cn --name cdh2 --privileged=true -t -i  -p 2181:2181 -p 60010:60010  -p 60000:60000  -p 9092:9092  -p 8888:8888 -p 7180:7180 CONTAINDER_NAME/ID

docker run --name cdh2 --privileged=true -t -i  -p 2181:2181 -p 60010:60010  -p 60000:60000  -p 9092:9092  -p 8888:8888 -p 7180:7180 CONTAINDER_NAME/ID

/usr/bin/docker-quickstart

/home/cloudera/cloudera-manager --force --express


```

# 数据库说明
## 辰阔提供的数据库(源端)：
使用账户：
cktester/test1533

>权限：仅有读取与执行权限
### 对接业务系统： 
地址： jdbc:mysql://162.105.23.3:3306/chenkuo_os

`字符集:utf8mb4`
`校验规则：utf8mb4_general_ci`

>备注： 存放关于辰阔业务系统相关数据，共有57张表

### 对接税务系统： 
地址： jdbc:mysql://162.105.23.3:3306/chenkuo_taxs

`字符集:utf8mb4`
`校验规则：utf8mb4_general_ci`
>备注： 存放从企业税控机采集的税务数据，共有两张表

## 目的端数据库：
地址：jdbc:mysql://162.105.23.3:3306/chenkuo_credit

使用账户：
pkutester/test1533

>权限 :拥有该库所有权限

`字符集:utf8mb4`
`校验规则：utf8mb4_general_ci`
