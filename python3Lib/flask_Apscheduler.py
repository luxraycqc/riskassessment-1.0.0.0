#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Dec  5 12:32:21 2018

@author: vincent
"""
# flask+APScheduler
# 定时任务
from flask import Flask
from flask_apscheduler import APScheduler
# 设置任务
class Config(object):
    JOBS=[{'id':'job1',
           'func':"__main__:print_hello",
           'trigger':'interval',
           'seconds':5,}]
def print_hello():
    print("hello world")

app=Flask(__name__)
app.config.from_object(Config())

@app.route('/')
def hello_world():
    return 'hello'

if __name__ == '__main__':
    scheduler = APScheduler()  # 实例化APScheduler
    scheduler.init_app(app)  # 把任务列表放进flask
    scheduler.start()  # 启动任务列表
    app.run()  # 启动flask
