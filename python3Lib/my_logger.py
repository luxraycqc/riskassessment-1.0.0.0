import logging
import datetime
logger = logging.getLogger(__name__)
logger.setLevel(level=logging.INFO)
handler = logging.FileHandler("logs/call_proc-{}.log".format(datetime.datetime.now().strftime('%Y-%m-%d')))
handler.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s - %(filename)s- %(funcName)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
console = logging.StreamHandler()
console.setLevel(logging.INFO)
logger.addHandler(handler)
logger.addHandler(console)