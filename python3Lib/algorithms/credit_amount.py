#-*- coding:utf-8 -*-
# author:chen
# datetime:2018/12/12 下午10:56

import os
import numpy as np
import pandas as pd
import pymysql
import configparser
from sqlalchemy import create_engine
from algorithms.statistic_business_transaction import  *
from algorithms.statistic_firm_invoice import  *


## 提取数据

def extract_data(db_conn,applyIds_str,extract_type):
    # 数据库连接
    db = create_engine(
        'mysql+mysqlconnector://%s:%s@%s:%s/%s' % (db_conn['USERNAME'], db_conn['PASSWORD'],
                                                   db_conn['HOSTNAME'], db_conn['PORT'],db_conn['DATABASE'])
    )

    if extract_type == "months_output":
        form_table = "mid_firm_integrity_months_output_zjh_all"
    elif extract_type == "downstream_output":
        form_table = "mid_downstream_year_output_zjh"
    elif extract_type == "firm_info":
        form_table = "mid_firmInfos_zjh_all"

    extract_data = pd.read_sql(
        "select * from `%s`  %s" %(form_table,applyIds_str),
        con=db
    )
    return extract_data

def cal_credit_score(db_conn,model_coef,applyIds_str):
    ## 税务数据
    invoices_data = extract_data(db_conn, applyIds_str, "months_output")
    statistic_invoices = cal_statistic_invoices(invoices_data)
    # print(statistic_invoices.columns)
    ##下游企业数据
    downstream_data = extract_data(db_conn, applyIds_str, "downstream_output")
    statistic_downstream = Cal_Stability_downstreamFirms(downstream_data)
    # print(statistic_downstream.columns)

    #企业信息
    firm_data = extract_data(db_conn, applyIds_str, "firm_info")[["applyId","management_date","registered_capital","county_CAGR"]]

    credit_score_dfs = pd.merge(statistic_invoices,statistic_downstream,right_on= "applyId",left_on="applyId",how='inner')
    credit_score_dfs = pd.merge(credit_score_dfs, firm_data, right_on="applyId", left_on="applyId",how='inner')
    score_result = pd.DataFrame()

    for i in credit_score_dfs.applyId.unique():
        credit_score_df = credit_score_dfs[credit_score_dfs.applyId == i]
        score_dict = {}
        # 指标
        applyId = i
        estimate_income = round(credit_score_df["estimate_income"].values[0] / 10000, 2)
        rate_red_invoice_sum = credit_score_df["rate_red_invoice_sum"].values[0]
        rate_invalid_invoice_sum = credit_score_df["rate_invalid_invoice_sum"].values[0]
        rate_value_added_tax_sum = credit_score_df["rate_value_added_tax_sum"].values[0]
        CGAR = credit_score_df["CGAR"].values[0]
        stability_quarter = round(credit_score_df["stability_quarter"].values[0] / 10000 / estimate_income * 4, 2)
        rate_identical_transaction_sum = credit_score_df["rate_identical_transaction_sum"].values[0]

        registered_capital = round(credit_score_df["registered_capital"].values[0], 2)
        management_date = credit_score_df["management_date"].values[0]
        county_CAGR = credit_score_df["county_CAGR"].values[0]

        print("-------------")
        print(registered_capital,estimate_income, rate_red_invoice_sum, rate_invalid_invoice_sum, rate_value_added_tax_sum, CGAR,
              stability_quarter, rate_identical_transaction_sum,management_date,county_CAGR)

        #授信额度计算
        H_value = model_coef["CONSTANT"] + \
                  estimate_income * model_coef["ESTIMATE_INCOME"] + \
                  rate_red_invoice_sum * model_coef["REAT_RED_INVOICE_SUM"] +  \
                  rate_value_added_tax_sum * model_coef["REAT_VALUE_ADDED_TAX_SUM"] + \
                  stability_quarter * model_coef["STABILITY_QUARTER"]

        # COEF = {'CONSTANT': -2.061666, 'REGISTERED_CAPITAL': 0.0001478, 'ESTIMATE_INCOME': 0.000039,
        #         'REAT_RED_INVOICE_SUM': 0.011243,
        #         'REAT_INVALID_INVOICE_SUM': 0.0357409, 'REAT_VALUE_ADDED_TAX_SUM': 0.0022699, 'CGAR': 0.0292066,
        #         'STABILITY_QUARTER': 0.3191976,
        #         'REAT_IDENTICAL_TRA NSATION_SUM': 0.024414, 'MANAGEMENT_DATE': 0.006458, 'COUNTY_CAGR': 5.373981}

        # 处理
        P_result = np.e ** H_value / (1 + np.e ** H_value) * 1000

        score_dict["applyId"] = applyId
        score_dict["P_result"] = P_result

        score_result = pd.concat([score_result,pd.DataFrame(score_dict,index=[0])])

    score_result.index = range(0,len(score_result))
    return  score_result




if __name__ == '__main__':
    config = configparser.RawConfigParser()
    config.read(
        os.path.join(os.path.abspath(os.path.dirname(os.getcwd()) + os.path.sep + "."),
                     'resources/conf_db.cfg'))

    db_conn = eval(config.get('DATABASE', 'CONN'))
    #选择配置文件
    model_coef = eval(config.get('MODEL_COEFFICIENT', 'CREDIT'))

    rawdata = pd.read_excel("/Users/chen/Desktop/train_data.xlsx")
    trainData = tuple(rawdata.applyId.values)
    print(trainData)


    # trainData = ('1A5D6281941B46BD8CE12DB2C4B2F81F','025202CD8F1F41D0B052A0DCA1DB35B2','0407674ADBAA4986AE8429591C330BA9')

    if isinstance(trainData, tuple):
        applyIds_str = "where  applyId  IN %s " % (str(trainData))
    elif isinstance(trainData, str):
        applyIds_str = "where  applyId = '%s' " % (str(trainData))
    else:
        applyIds_str = " "

    score_result = cal_credit_score(db_conn,model_coef,applyIds_str)
    print("==========")

    print(score_result)

