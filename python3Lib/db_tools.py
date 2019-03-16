#!/usr/bin/env python
# -*- coding: utf8 -*-
import os
import pymysql
import configparser
import datetime
from my_logger import logger
from app import create_app
config = configparser.RawConfigParser()
app = create_app()

'''调用存储过程'''
def call_proc(args,session):
    config.read(os.path.join(app.root_path, 'resources/conf_db.cfg'))
    conn = eval(config.get('DATABASE', 'CONN'))
    procs = eval(config.get(session,'PROCEDURES'))
    logger.info('Start to call procedure......')
    # 打开数据库连接
    db = pymysql.connect(host=conn['HOSTNAME'],
                         user=conn['USERNAME'], password=conn['PASSWORD'],
                         db=conn['DATABASE']
                         ,port=conn['PORT'])
    cursor = db.cursor()
    try:
        # 存储过程记录表app_procedure_record 4 column：reportid 状态0-1-2开始时间 结束时间
        # 在app_procedure_record里添加一条记录
        #sql=()
        now_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        start_time=datetime.datetime.strptime(now_time,'%Y-%m-%d %H:%M:%S')
        sql=("insert into app_procedure_record(report_id,status,start_time) \
                            values('%s',%d,'%s')")
        #print(sql)
        date=(args[0],0,start_time)
        cursor.execute(sql % date)
        db.commit()
        
        # 执行存储过程
        for pro in procs:
            cursor.callproc(pro,args)
            logger.info('Successfully called '+pro)
        # 向数据库提交
        db.commit()
        # 更新存储过程记录表 成功 将status 置为 1（成功）
        now_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        end_time=datetime.datetime.strptime(now_time,'%Y-%m-%d %H:%M:%S')
        sql=("update app_procedure_record set status = 1 ,end_time = '%s' \
        where report_id = '%s'")
        date=(end_time,args[0])
        cursor.execute(sql % date)
        db.commit()
    except Exception as e:
       # 发生错误时回滚
       db.rollback() 
       logger.info(e)
       # 更新存储过程记录表 成功 将status 置为 2（失败）
       now_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
       end_time=datetime.datetime.strptime(now_time,'%Y-%m-%d %H:%M:%S')
       sql=("update app_procedure_record set status = 2 ,end_time = '%s' \
       where report_id = '%s'")
       date=(end_time,args[0])
       cursor.execute(sql % date)
       db.commit()
    # 关闭数据库连接
    db.close()
    logger.info('Finish!')

# if __name__ == '__main__':
#     call_proc()