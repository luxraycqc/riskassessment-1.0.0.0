### 文件说明
算法文件目录路径：
http://home1.aichain.net.cn:10080/app/ms/tree/master/python3Lib/algorithms
```
文件概况
1.db_conn.py  :数据库相关操作文件
2.model_main.py : 调用算法的主函数
3.statistic_business_transaction.py : 关于企业下游交易统计指标的计算
4. statistic_firm_invoice.py :关于企业的税务数据统计指标的计算
```

配置文件目录路径：
http://home1.aichain.net.cn:10080/app/ms/tree/master/python3Lib/resources
```
文件概况
1. conf_db.cfg ：包含数据库配置、信用评分与授信额度模型参数权重
```
### 调用方式：

执行路径：
`infosec@infosec-PowerEdge-R730:~/work/projects/ms/python3Lib/algorithms`

执行方式
```
1.导出JSon格式：
示例：python3  model_main.py "02CD30A6228D4EDAA86B7AB8355CE803" "EX_Json" "{'EX_PATH':'/Users/chen/Desktop/score_result.json'}"

备注：
第三个参数是数据导出的文件路径
```
```

2.导入mysql：
示例：
python3  model_main.py 
"02CD30A6228D4EDAA86B7AB8355CE803" "EX_DB" "{'HOSTNAME':'162.105.23.3','USERNAME':'pkutester','PASSWORD':'test1533','DATABASE':'chenkuo_credit','PORT':3306,'CHARSET':'utf8'}" score_result

备注：
第三个参数是数据库连接参数;
第四个参数score_result是要导入数据的表名

3.直接打印到屏幕：
示例：
python3  model_main.py 
"02CD30A6228D4EDAA86B7AB8355CE803" "EX_Print"

```


