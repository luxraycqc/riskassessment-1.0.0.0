#-*- coding:utf-8 -*-

import os
import sys

file_path = os.path.abspath(os.path.dirname(os.getcwd()))
sys.path.append(file_path)

import pandas as pd
import configparser

from db_conn import fetchData_formCredit
from statistic_firm_invoice import *
pd.set_option('display.max_columns', None)

def cal_firm_profile_params(db_conn, applyIds_str):
    """
        函数说明：获得计算企业画像所需参数
    """

    # 企业信息
    firm_data = fetchData_formCredit(db_conn, applyIds_str, "firm_info")[["apply_id", "business_date", "apply_date", "registered_year", "registered_capital"]]
    # 税务数据
    invoices_data = fetchData_formCredit(db_conn, applyIds_str, "months_output")
    statistic_invoices = cal_statistic_invoices(invoices_data)

    # 数据拼接
    credit_score_dfs = pd.merge(firm_data, statistic_invoices, right_on="apply_id", left_on="apply_id", how='inner')

    for i in credit_score_dfs.apply_id.unique():
        credit_score_df = credit_score_dfs[credit_score_dfs.apply_id == i]
        firm_profile_params = {}

        apply_id = i
        registered_capital = credit_score_df["registered_capital"].values[0]
        if registered_capital == None:
            registered_capital = "500.000000"
        registered_year = credit_score_df["registered_year"].values[0]
        estimate_income = credit_score_df["estimate_income"].values[0]
        AAGR = credit_score_df["AAGR"].values[0]
        if AAGR == "Null":
            AAGR = 0.0
        CGAR = credit_score_df["CGAR"].values[0]
        if CGAR == "Null":
            CGAR = 0.23
        stability_quarter = credit_score_df["stability_quarter"].values[0]
        if stability_quarter == "Null":
            stability_quarter = 0.430517152

        firm_profile_params["apply_id"] = apply_id
        firm_profile_params["registered_capital"] = registered_capital
        firm_profile_params["registered_year"] = registered_year
        firm_profile_params["estimate_income"] = estimate_income
        firm_profile_params["AAGR"] = AAGR
        firm_profile_params["CGAR"] = CGAR
        firm_profile_params["stability_quarter"] = stability_quarter

        print(firm_profile_params)

if __name__ == '__main__':

    config = configparser.RawConfigParser()
    config.read(os.path.join(os.path.abspath(os.path.dirname(os.getcwd()) + os.path.sep + "."), 'resources/conf_db.cfg'))
    db_conn = eval(config.get('DATABASE', 'CONN_DBCREDIT'))
    model_coef = eval(config.get('MODEL_COEFFICIENT', 'SCORE_COEF'))
    amount_coef = eval(config.get('MODEL_COEFFICIENT', 'AMOUNT_COEF'))
    parameters = sys.argv
    cal_firm_profile_params(db_conn, parameters[1])

