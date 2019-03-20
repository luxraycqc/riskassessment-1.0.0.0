# -*- coding: utf-8 -*-
"""
author: ChenJiquan
date : 20181204

functions: 存放关于企业的上下游企业相关指标计算的函数
"""
from pexpect import searcher_re


def Cal_Stability_downstreamFirms(df,apply_date):

    """
    函数说明：计算下游企业交易稳定性、HHI等相关指标
    输入：传入dataframe格式的税务数据（例如若干企业）
    输出：dataframe形式的数据，》
        包括：applyId（申请ID）、
            num_all_firms（数目）、num_identical_firms、num_last_year_firms、num_recent_year_firms	
            rate_abnormal_collect（占比）、rate_identical_transaction	
            sum_abnormal_collect（金额）、sum_all_firms_transaction、sum_avr_firms_transaction、sum_identical_transaction
    
    """
    import  pandas as pd
    import  numpy as np
    import time

    downstream_firms_DataDf = pd.DataFrame()

    for apply_id in df["apply_id"].unique():
        fun_data = df[df["apply_id"] == apply_id][["apply_id", "years", "downstream_firm_buyer_ids", "transaction_amount"]]
        downstream_firms_Datadict = {}  # 存数据

        identical_firms_transaction_sum = 0
        abnormal_frims_transaction_sum = 0
        all_firms_transaction_sum = 0
        all_firms_transaction_sum_list = []  # 存储两年内的交易金额

        downstream_firms_Datadict["apply_id"] = apply_id
        recent_years = fun_data.years.sort_values(ascending=False).unique()[0:2]  # 最近两年
        recent_year_data = set(
            fun_data[fun_data["years"] == recent_years[0]]["downstream_firm_buyer_ids"].values)  # 最近一年
        downstream_firms_Datadict["num_recent_year_firms"] = len(recent_year_data)

        if len(recent_years) > 1:
            last_year_data = set(fun_data[fun_data["years"] == recent_years[1]][
                                     "downstream_firm_buyer_ids"].values) # 去年
            identical_firms = recent_year_data & last_year_data  # 交集

            # 企业数
            all_firms_num = len(recent_year_data | last_year_data)
            identical_firms_num = len(recent_year_data & last_year_data)

            downstream_firms_Datadict["num_last_year_firms"] = len(last_year_data)
            downstream_firms_Datadict["num_all_firms"] = all_firms_num
            downstream_firms_Datadict["num_identical_firms"] = identical_firms_num

            # 金额累计
            for each_firm_data in fun_data.values:
                if each_firm_data[2] in identical_firms:  # each_firm_data[1] 纳税人识别号
                    identical_firms_transaction_sum += each_firm_data[3]  # 两年
                    # 总交易量
                all_firms_transaction_sum += each_firm_data[3]
                all_firms_transaction_sum_list.append(each_firm_data[3])

            # 重合企业金额、异常采集金额 、 总金额
            downstream_firms_Datadict["sum_identical_transaction"] = round(identical_firms_transaction_sum, 2)
            downstream_firms_Datadict["sum_abnormal_collect"] = round(abnormal_frims_transaction_sum, 2)
            downstream_firms_Datadict["sum_all_firms_transaction"] = round(all_firms_transaction_sum, 2)
            # 平均金额
            downstream_firms_Datadict["sum_avr_firms_transaction"] = round(all_firms_transaction_sum / all_firms_num, 2)
            # 中位数
            downstream_firms_Datadict["sum_median_firms_transaction"] = np.median(all_firms_transaction_sum_list)

            # 重合企业交易金额占比
            downstream_firms_Datadict["rate_identical_transaction_sum"] = round(
                identical_firms_transaction_sum / all_firms_transaction_sum, 4) * 100
            # ---- 相同交易企业数占比、
            downstream_firms_Datadict["rate_identical_transaction_num"] = round(identical_firms_num / all_firms_num,
                                                                                4) * 100
            # 发票采集异常交易金额占比
            downstream_firms_Datadict["rate_abnormal_collect_sum"] = round(
                abnormal_frims_transaction_sum / all_firms_transaction_sum, 4) * 100

            #HHI 赫分达尔
            # current_year = str(2016)   #########改为申请的时间
            # data_years = fun_data["years"].unique()
            # max_year = max(data_years)
            # if(current_year in data_years):
            #     year_HHI = int(max_year) - 1 #去年
            #     print("year")
            # else:
            #     year_HHI = max_year
            data_forHHI = fun_data[fun_data["years"] == apply_date]
            #总交易
            total_transaction_amount = np.sum(data_forHHI["transaction_amount"])

            data_HHI = np.sum(
                data_forHHI["transaction_amount"].apply(lambda x: (x/total_transaction_amount)**2)\
                    .sort_values(ascending=False)[:10]
            )
            downstream_firms_Datadict["HHI_value"] = data_HHI

        else:
            for each_firm_data in fun_data.values:
                # if each_firm_data[2] in abnormal_data:
                #     abnormal_frims_transaction_sum += each_firm_data[3]
                all_firms_transaction_sum += each_firm_data[3]
                all_firms_transaction_sum_list.append(each_firm_data[3])

            all_firms_num = len(recent_year_data)

            downstream_firms_Datadict["num_last_year_firms"] = -1  # -1表缺失
            downstream_firms_Datadict["num_all_firms"] = all_firms_num  # 企业数
            downstream_firms_Datadict["num_identical_firms"] = 0

            downstream_firms_Datadict["sum_identical_transaction"] = -1
            downstream_firms_Datadict["sum_abnormal_collect"] = round(abnormal_frims_transaction_sum, 2)
            downstream_firms_Datadict["sum_all_firms_transaction"] = round(all_firms_transaction_sum, 2)  # 总金额

            downstream_firms_Datadict["rate_identical_transaction_num"] = -1
            downstream_firms_Datadict["rate_identical_transaction_sum"] = -1
            downstream_firms_Datadict["rate_abnormal_collect_sum"] = -1

            # 平均金额
            downstream_firms_Datadict["sum_avr_firms_transaction"] = round(all_firms_transaction_sum / all_firms_num, 2)
            # 中位数
            downstream_firms_Datadict["sum_median_firms_transaction"] = np.median(all_firms_transaction_sum_list)

        # # 数据偏度与峰度
        # pd_all_firms_transaction_sum_list = pd.Series(all_firms_transaction_sum_list)
        # list_skew = pd_all_firms_transaction_sum_list.skew()
        # list_kurt = pd_all_firms_transaction_sum_list.kurt()
        #
        # if (pd.isna(list_skew)):
        #     downstream_firms_Datadict["sum_firms_transaction_skew"] = "Null"
        # else:
        #     downstream_firms_Datadict["sum_firms_transaction_skew"] = list_skew
        # if (pd.isna(list_kurt)):
        #     downstream_firms_Datadict["sum_firms_transaction_kurt"] = "Null"
        # else:
        #     downstream_firms_Datadict["sum_firms_transaction_skew"] = list_kurt

        downstream_firms_DataDf = pd.concat(
            [downstream_firms_DataDf, pd.DataFrame(downstream_firms_Datadict, index=[0])])
        # downstream_firms_DataDf.index = range(1, len(downstream_firms_DataDf) + 1)

        stability_bins = [
            -1, 33, 54, 85, 101
        ]

        level_stability = [
            '偏低度稳定', '中度稳定', '偏高度稳定', '高度稳定'
        ]


        # 稳定性
        downstream_firms_DataDf['level_stability'] = pd.cut(
            downstream_firms_DataDf.rate_identical_transaction_sum,
            stability_bins,
            right=False,
            labels=level_stability
        )

    return downstream_firms_DataDf


# if __name__ == '__main__':
#     from algorithms.db_conn import Extract_Data, export_data
#     import configparser
#     import os
#     import pandas as pd
#
#     pd.set_option('display.max_columns', None)
#
#     config = configparser.RawConfigParser()
#     config.read(
#         os.path.join(os.path.abspath(os.path.dirname(os.getcwd()) + os.path.sep + "."),
#                      'resources/conf_db.cfg'))
#     db_conn = eval(config.get('DATABASE', 'CONN_CREDIT'))
#     applyIds_str = '02CD30A6228D4EDAA86B7AB8355CE803'
#     downstream_data = Extract_Data(db_conn, applyIds_str, "downstream_output")
#
#     statistic_downstream = Cal_Stability_downstreamFirms(downstream_data)
#     # print(statistic_downstream)