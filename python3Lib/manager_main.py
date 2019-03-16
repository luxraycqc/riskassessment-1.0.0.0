#!/usr/bin/env python
# -*- coding: utf8 -*-

from flask_script import Manager
from app import create_app
from manager_etl import DBMANAGER

manager = Manager(create_app)
manager.add_option('-c', '--config', dest='config', required=False)
manager.add_command('db', DBMANAGER)

if __name__ == '__main__':
    manager.run()