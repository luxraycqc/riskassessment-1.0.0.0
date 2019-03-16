#!/usr/bin/env python
# -*- coding: utf8 -*-
import os

from flask import Flask


def create_app(config=None):
    app = Flask(__name__)
    if config is None:
        config = os.path.join(app.root_path, 'resources/conf_dev.cfg')
    app.config.from_pyfile(config)

    @app.route('/')
    def index():
        return 'Hello, %(name)s!' % {'name': app.config['HELLO']}
    @app.route('/test')
    def test():
        return 'hello' 
    return app