#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Dec  6 10:23:29 2018

@author: vincent
"""

#!/usr/bin/env python
# -*- coding: utf8 -*-
import os

from flask import Flask


def create_app(config=None):
    app = Flask(__name__)


    @app.route('/')
    def index():
        return 'Hello, %(name)s!' % {'name': app.config['HELLO']}
    @app.route('/test')
    def test():
        return 'hello' 
    return app