#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Dec  6 10:22:53 2018

@author: vincent
"""

from flask_script import Manager
from myapp import create_app
from flask_script import Command
manager = Manager(create_app)

@manager.command
def hello():
    print("hello")

@manager.option('-n','--name',help='Your name')
def helloname(name):
    print("hello",name)
  
class Hi(Command):
    def run(self):
        print("hello world")
        
        


manager.add_command('hi',Hi())
if __name__=="__main__":
    manager.run()