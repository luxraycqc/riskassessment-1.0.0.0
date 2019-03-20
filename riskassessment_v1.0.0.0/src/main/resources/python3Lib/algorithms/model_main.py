#-*- coding:utf-8 -*-
# author:chen
# datetime:2018/12/12 下午10:56

import os
import time
import sys
import json


file_path = os.path.abspath(os.path.dirname(os.getcwd())) #项目路径导入环境变量
sys.path.append(file_path)
# sys.path.append(file_path + "/algorithms")
#
# for p in sys.path:
#     print(p)

import numpy as np
import pandas as pd
pd.set_option('display.max_columns', None)
import configparser

from db_conn import fatchData_formCredit, export_data
from statistic_business_transaction import  *
from statistic_firm_invoice import  *


def cal_credit_score(db_conn,model_coef,amount_coef,applyIds_str):
    """
        函数说明：计算信用评分和授信额度
        输入：
            1. 税务数据
            2.下游企业数据
            3.企业信息
            4.
        输出：
        dataframe形式的数据
            包括：apply_Id（申请ID）、estimate_income 年预估值 、credit_score 信用评分 、amount_credit 授信额度

     """
    #企业信息
    firm_data = fatchData_formCredit(db_conn, applyIds_str, "firm_info")[["apply_id","registered_year","apply_credit_limit",
                                                                          "registered_capital","county_cagr","apply_date"]]

    ## 税务数据
    invoices_data = fatchData_formCredit(db_conn, applyIds_str, "months_output")
    statistic_invoices,pass_type = cal_statistic_invoices(invoices_data,firm_data["apply_credit_limit"].values[0])

    if pass_type>0:
        dict_statistic_invoices = statistic_invoices.set_index('apply_id').to_dict('records')[0]
        #导出数据库：改
        #statistic_invoices.to_excel("/Users/chen/Desktop/md_firm_integrity_months_output_cjq_all_20181219.xlsx",index=False)
        # print(statistic_invoices.columns) 删

        ##下游企业数据
        downstream_data = fatchData_formCredit(db_conn, applyIds_str, "downstream_output")
        statistic_downstream = Cal_Stability_downstreamFirms(downstream_data,str(firm_data["apply_date"].values[0].year))
        dict_statistic_downstream = statistic_downstream.set_index('apply_id').to_dict('records')[0]

        # 导出数据库：改
        #statistic_downstream.to_excel("/Users/chen/Desktop/md_firm_integrity_transaction_statistics_cjq_all_20181219.xlsx",index=False)

        # # HHi
        # mid_downstream_HHI = Extract_Data(db_conn, applyIds_str,"HHI")
        # print(statistic_downstream.columns)

        #数据拼接：
        credit_score_dfs = pd.merge(statistic_invoices,statistic_downstream,right_on= "apply_id",left_on="apply_id",how='inner')
        credit_score_dfs = pd.merge(credit_score_dfs, firm_data, right_on="apply_id", left_on="apply_id",how='inner')

        #score_result = pd.DataFrame()

        for i in credit_score_dfs.apply_id.unique():
            credit_score_df = credit_score_dfs[credit_score_dfs.apply_id == i]
            score_dict = {}
            # 指标
            apply_id = i
            max_invoicedNot_months = credit_score_df["max_invoicedNot_months"].values[0]
            rate_red_invoice_sum = round(credit_score_df["rate_red_invoice_sum"].values[0], 2)
            rate_invalid_invoice_sum = round(credit_score_df["rate_invalid_invoice_sum"].values[0], 2)
            # print("credit_score_df[rate_invalid_invoice_sum]:%s"%credit_score_df["rate_invalid_invoice_sum"].values)
            rate_value_added_tax_sum = round(credit_score_df["rate_value_added_tax_sum"].values[0], 2)

            estimate_income =  round(credit_score_df["estimate_income"].values[0] / 10000, 2)
            # print("estimate_income:%s"%estimate_income)
            if estimate_income == "Null" or np.isnan(estimate_income):
                estimate_income = credit_score_df["registered_capital"].values[0]
            # print("estimate_income:%s" % estimate_income)
            CGAR = credit_score_df["CGAR"].values[0]

            if CGAR == "Null":
                CGAR = 0.23 ### 平均 需改动

            # print("CGAR%s"%CGAR)
            stability_quarter = credit_score_df["stability_quarter"].values[0]
            if stability_quarter == "Null":
                stability_quarter = 0.430517152 ### 平均 需改动

            registered_capital = float(credit_score_df["registered_capital"].values[0])
            # print("registered_year:%s" %registered_capital)
            rate_identical_transaction_sum = round(credit_score_df["rate_identical_transaction_sum"].values[0], 2)
            registered_year = credit_score_df["registered_year"].values[0]
            # print("registered_year:%s"%type(registered_year))
            county_cagr = credit_score_df["county_cagr"].values[0]
            HHI_value = credit_score_df["HHI_value"].values[0]
            # print("HHI_value:%s"%type(HHI_value))

            # print(i,max_invoicedNot_months,rate_red_invoice_sum,rate_invalid_invoice_sum,rate_value_added_tax_sum,estimate_income,estimate_income,CGAR,stability_quarter,registered_capital,rate_identical_transaction_sum,management_date,county_CAGR,HHI_value)
            # print("registered_capital,estimate_income, rate_red_invoice_sum, rate_invalid_invoice_sum, rate_value_added_tax_sum:%s,%s,%s,%s,%s"%(registered_capital,estimate_income, rate_red_invoice_sum, rate_invalid_invoice_sum, rate_value_added_tax_sum))
            # print("-------------")
            # print("CGAR,stability_quarter, rate_identical_transaction_sum,management_date,county_CAGR:%s,%s,%s,%s,%s"%(CGAR,stability_quarter, rate_identical_transaction_sum,management_date,county_CAGR))
            #
            # print("================")
            # print(max_invoicedNot_months,HHI_value)

            ##信用评分
            H_value = model_coef["CONSTANT"] + max_invoicedNot_months * model_coef["MAX_INVOICEDNOT_MONTHS"] \
                      + registered_capital * model_coef["REGISTERED_CAPITAL"] \
                      + estimate_income * model_coef["ESTIMATE_INCOME"] + \
                      rate_red_invoice_sum * model_coef["REAT_RED_INVOICE_SUM"] + \
                      rate_invalid_invoice_sum * model_coef["REAT_INVALID_INVOICE_SUM"] + \
                      rate_value_added_tax_sum * model_coef["REAT_VALUE_ADDED_TAX_SUM"] + \
                      CGAR * model_coef["CGAR"] + stability_quarter * model_coef["STABILITY_QUARTER"]\
                      + rate_identical_transaction_sum * model_coef["REAT_IDENTICAL_TRANSATION_SUM"]+ \
                      registered_year * model_coef["REGISTERED_YEAR"]+ county_cagr * model_coef["COUNTY_CAGR"] \
                      + HHI_value * model_coef["HHI"]

            # print(max_invoicedNot_months ,
            #       registered_capital,
            #       estimate_income ,
            #       rate_red_invoice_sum,
            #       rate_invalid_invoice_sum ,
            #       rate_value_added_tax_sum ,
            #       CGAR, stability_quarter,
            #       rate_identical_transaction_sum ,
            #       management_date , county_CAGR ,
            #       HHI_value )

            # print("======" * 10)
            # print(max_invoicedNot_months * model_coef["MAX_INVOICEDNOT_MONTHS"] , registered_capital * model_coef["REGISTERED_CAPITAL"] ,
            #           estimate_income * model_coef["ESTIMATE_INCOME"] , rate_red_invoice_sum * model_coef["REAT_RED_INVOICE_SUM"] ,
            #           rate_invalid_invoice_sum * model_coef["REAT_INVALID_INVOICE_SUM"] , rate_value_added_tax_sum * model_coef["REAT_VALUE_ADDED_TAX_SUM"] ,
            #           CGAR * model_coef["CGAR"] , stability_quarter * model_coef["STABILITY_QUARTER"] , rate_identical_transaction_sum * model_coef["REAT_IDENTICAL_TRANSATION_SUM"],
            #           management_date * model_coef["MANAGEMENT_DATE"],county_CAGR * model_coef["COUNTY_CAGR"] , HHI_value * model_coef["HHI"])
            # print("======"*10)
            # print("H_value:%s"%H_value)
            # 处理
            credit_score = np.e ** H_value / (1 + np.e ** H_value) * 1000
            # print("credit_score:%s"%credit_score)
            # print("======"*10)
            ## 授信额度
            amount_value = amount_coef["CONSTANT"] + max_invoicedNot_months * amount_coef["MAX_INVOICEDNOT_MONTHS"] + rate_red_invoice_sum * amount_coef["REAT_RED_INVOICE_SUM"] + \
                           rate_invalid_invoice_sum * amount_coef["REAT_INVALID_INVOICE_SUM"] + rate_value_added_tax_sum * amount_coef["REAT_VALUE_ADDED_TAX_SUM"] + \
                           estimate_income * amount_coef["ESTIMATE_INCOME"] + CGAR * amount_coef["CGAR"] + stability_quarter * amount_coef["STABILITY_QUARTER"]

            # print(amount_coef["CONSTANT"],max_invoicedNot_months,
            #       rate_red_invoice_sum,
            #       rate_invalid_invoice_sum,
            #       rate_value_added_tax_sum,
            #       estimate_income,
            #       CGAR, stability_quarter)
            amount_credit = int(amount_value/10) * 10

            if credit_score > 500:
                if amount_credit < 50:
                    amount_credit = 50
                elif amount_credit > 100:
                    amount_credit = 100
            else:
                if amount_credit < 30:
                    amount_credit = 30
                elif amount_credit > 95:
                    amount_credit = 95
            # print("credit_value:%s"%amount_value)
            score_dict["apply_id"] = apply_id
            score_dict["estimate_income"] = str(estimate_income)
            score_dict["credit_score"] = round(credit_score)
            score_dict["amount_credit"] = amount_credit
            score_dict["label"] = pass_type
            if pass_type == 1:
                score_dict["remark"] = "最近12个月税务数据总和小于贷款金额"
            else:
                score_dict["remark"] = "正常计算"

            # score_result = pd.concat([score_result,pd.DataFrame(score_dict,index=[0])])
        #score_result.index = range(0,len(score_result))
        return  dict_statistic_invoices,dict_statistic_downstream,score_dict
    else:
        score_dict = {}
        score_dict["apply_id"] = firm_data["apply_id"].values[0]
        score_dict["estimate_income"] = "null"
        score_dict["credit_score"] = -1
        score_dict["amount_credit"] = -1
        score_dict["label"] = pass_type
        score_dict["remark"] = "税务数据不满足至少18个月的前提"

        return  score_dict


#
def operation_exModes(cal_result,*parameters,**operation_para):
    """
         函数说明：将算法结果导出

         导出方式：
            1.json格式 到文件
            2.myslq数据库
      """
    operation_type = parameters[2] #操作类型
    generate_time = time.strftime('%Y/%m/%d %H:%M:%S', time.localtime(time.time()))
    #直接打印
    if operation_type == "EX_Print":
        if isinstance(cal_result, tuple):
            score_result_json = cal_result[2]
            dict_statistic_invoices_json = cal_result[0]
            dict_statistic_downstream_json = cal_result[1]
        else:
            score_result_json = cal_result
            dict_statistic_invoices_json = "null"
            dict_statistic_downstream_json = "null"
        json_score_result = json.dumps({
                                    'score_result': score_result_json,
                                    'dict_statistic_invoices':dict_statistic_invoices_json,
                                    'dict_statistic_downstream':dict_statistic_downstream_json,
                                    'generate_time': generate_time}
                                    ,ensure_ascii=False)
        print(json_score_result)
    else:
        operation_para = eval(parameters[3])  # 操作参数
        #导出json格式
        if operation_type == "EX_Json":
            #to_json
            json_score_result = json.dumps({
                'score_result': cal_result[2],
                'dict_statistic_invoices': cal_result[0],
                'dict_statistic_downstream': cal_result[1],
                'generate_time': generate_time},
                ensure_ascii=False)
            print(json_score_result)

            with open(operation_para["EX_PATH"],'a',encoding="utf8") as f:
                f.write(json_score_result + "\n")
        #导出到数据库
        if operation_type == "EX_DB":
            para_supplement = parameters[4] #表名
            if isinstance(cal_result, tuple):
                cal_result[2]["generate_time"] = generate_time
                score_result_DF = pd.DataFrame(cal_result[2], index=[0])
            else:
                cal_result["generate_time"] = generate_time
                score_result_DF = pd.DataFrame(cal_result, index=[0])
            try:
                export_data(operation_para,score_result_DF,para_supplement)

            except Exception as e:
                print(e.with_traceback()) ### 异常报错 --- 待补充
    return "execute success"




if __name__ == '__main__':

    config = configparser.RawConfigParser()
    config.read(
        os.path.join(os.path.abspath(os.path.dirname(os.getcwd()) + os.path.sep + "."),
                     'resources/conf_db.cfg'))

    db_conn = eval(config.get('DATABASE', 'CONN_DBCREDIT'))
    model_coef = eval(config.get('MODEL_COEFFICIENT', 'SCORE_COEF'))
    amount_coef = eval(config.get('MODEL_COEFFICIENT', 'AMOUNT_COEF'))

    parameters = sys.argv
    # print(len(parameters))
    APPLYID = parameters[1]  # 申请ID
    # if isinstance(trainData, tuple):
    #     applyIds_str = "where  applyId  IN %s " % (str(trainData))
    # elif isinstance(trainData, str):
    #     applyIds_str = "where  applyId = '%s' " % (str(trainData))
    # else:
    #     applyIds_str = " "

    # APPLYID = "06A420A1B5B94DCF82338A8224B2AC39"
    # print(type(APPLYID))
    cal_result = cal_credit_score(db_conn,model_coef,amount_coef,APPLYID)

    execute_type =  operation_exModes(cal_result, *parameters)
    print(execute_type)
    # score_result.to_excel("/Users/chen/Desktop/preScore_result_20181220.xlsx",index=False)
    #score_result = {'applyId': '02CD30A6228D4EDAA86B7AB8355CE803', 'estimate_income': '474.21', 'credit_score': 379.0, 'amount_credit': 50}

