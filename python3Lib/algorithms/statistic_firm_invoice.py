# -*- coding: utf-8 -*-
"""
author: ChenJiquan
date : 20181204

functions:  包含企业的相关税务数据统计量的函数

"""
import numpy as np
def Judge_taxData_level(tmp_data,apply_credit_limit):
    flag = False
    pass_type = 0
    months_data = tmp_data.iloc[:, 0:12].values[tmp_data.iloc[:, 0:12].values > 0]
    months_len = len(months_data)

    if months_len >= 18:
        flag = True
        if (np.sum(months_data[-13:-1])//10000) < float(apply_credit_limit):
            pass_type = 1
        else:
            pass_type = 2

    return flag,pass_type

def Cal_CGAR(data_x):
    """
    函数说明：计算企业复合增长率
    输入：传入一个数组，例如（某企业3年的统计数据）
    输出：返回保留两位小数的数值
    """
    import numpy as np
    from functools import reduce

    #print("data_x:%s"%data_x)
    cal_list = []
    AGR = 0
    if len(data_x) > 1:
        for i in range(0, len(data_x) - 1):
            AGR = ((data_x[i + 1] - data_x[i]) / data_x[i])[0]
            cal_list.append(AGR)
        #print("cal_list:%s" % cal_list)
        if len(cal_list) > 1:
            cal_list = [j + 1 for j in cal_list]
            CGAR_list = reduce(lambda x, y: x * y, cal_list)
            return round(np.power(CGAR_list, 1 / len(cal_list)) - 1, 2)
        else:
            return round(AGR, 2)

    else:
        return "Null"
    
def Cal_AAGR(data_x):
    """
    函数说明：计算平均年增长率
    输入：传入一个数组，例如（某企业3年的统计数据）
    输出：返回保留两位小数的数值
    """
    import numpy as np

    cal_list = []
    AGR = 0
    if len(data_x) > 1:
        for i in range(0, len(data_x) - 1):
            AGR = ((data_x[i + 1] - data_x[i]) / data_x[i])[0]  # 拿到值
            cal_list.append(AGR)

        #print("cal_list AGR:%s" % cal_list)
        if len(cal_list) > 1:
            return np.mean(cal_list) # 返回平均
        else:
            return round(AGR, 2)
    else:
        return "Null"
    

def Cal_Stability_Quarter(quarter_all):
    """
    函数说明：计算企业稳定性（所有季度的预估值求标准差）
    输入：传入包含12个月数据的dataframe
    输出：返回稳定性指标
    """
    
    import numpy as np

    quarterData_list = []
    for q in range(0, len(quarter_all)):
        for i in range(0, 12, 3):
            quarter_data = quarter_all.iloc[q, :][i:i + 3]
            if len(quarter_data[quarter_data > 0]) == 3:
                quarterData_list.append(quarter_data[quarter_data > 0].sum())
            else:
                continue
    # 列表长度
    if len(quarterData_list) > 1:
        stability_quarter = np.std(quarterData_list) / np.mean(quarterData_list)
    elif len(quarterData_list) == 1:
        stability_quarter = 1
    else:
        stability_quarter = "Null"

    return stability_quarter


def Cal_Invoices_Annual(df_data): 
    """
    函数说明：计算企业相关税务统计信息，用于计算建模指标。详情参考矩阵说明
    输入：传入包含税务月统计发票数据（例如来自表：mid_firm_integrity_grant_months_output_cjq_all）的dataframe
    输出：返回Dataframe ，包含矩阵信息
    """

    import pandas as pd
    import numpy as np

    tmp = len(df_data["year"].unique()) #年数
    array_month_data = np.zeros([tmp, 33])  #存储
    """ 矩阵说明：
        0-11 存放 月份信息- 有效发票金额
        12判断是否完整年(没有则不算统计指标)  13是否大于6个月(1/-1) 、
        14 年总红冲金额   15、年总作废金额  16、年总有效金额（不含红冲）    
        17 真实收入（有效+红冲）    18 增值税额  19 总税额 20.年化收入（有效金额通过） 
        21 红冲占比（发票总金额）  22.作废占比   （发票总金额） 23.增税占比（总税额）
        24 有效发票总数	25无效发票总数	 26红冲发票总数	27发票总数  28 无效票占比 29红冲票占比 
        30 年份 31 实开票月数   32 最大未开票月份数
    """
    
    count = 0  #行

    for i in  df_data["year"].unique():  
        array_month_data[count,30] = i
#         year_data_all = df_data[df_data.year == i][["month","valid_invoice_amt","red_invoice_amt","invalid_invoice_amt"]].values
        year_data_all = df_data[df_data.year == i][["month","valid_invoice_amt","red_invoice_amt","invalid_invoice_amt",
                                                    "business_income","added_value_tax_amt","total_tax_amt","valid_invoice_num",
                                                    "invalid_invoice_num","valid_red_invoice_num","total_invoice_num"]].fillna(0).values

        #月份收入赋值
        red_invoice_sum = 0 #红冲金额合计
        valid_invoice_sum = 0  #有效金额合计
        invalid_invoice_sum = 0  #作废金额合计
        real_invoice_sum = 0 #真实收入
        value_added_tax_sum = 0  #增值税额合计
        total_tax_sum = 0  #总税额合计
        
        valid_invoice_count = 0 #有效票数
        invalid_invoice_count = 0 #无效票数
        red_invoice_count = 0 #红冲票数
        
        total_invoice_count=0 # 发票数 real_invoice_count

        for j in year_data_all:
            array_month_data[count, int(j[0]) - 1] = j[1]  # 存每个月的数
            red_invoice_sum += abs(j[2])
            valid_invoice_sum += j[1]
            invalid_invoice_sum += j[3]
            real_invoice_sum += j[4]
            value_added_tax_sum += j[5]
            total_tax_sum += j[6]

            valid_invoice_count += j[7]
            invalid_invoice_count += j[8]
            red_invoice_count += j[9]

            total_invoice_count += j[10]  # 有效

        #14红冲
        array_month_data[count,14] = red_invoice_sum
        #15作废
        array_month_data[count,15] = invalid_invoice_sum
        #16有效
        array_month_data[count,16] = valid_invoice_sum
        
        total_invoice_sum = np.sum(array_month_data[count,14:17])
        
        
        #17 发票总金额（真实）
        array_month_data[count,17] = real_invoice_sum
            
         #18 增值税额  19 总税额 
        array_month_data[count,18] = value_added_tax_sum
        array_month_data[count,19] =total_tax_sum

        #20.年化收入（通过真实金额） 
        real_months_amt = array_month_data[count][0:12]
        months_amt_index = np.argwhere(real_months_amt)
        invoiced_data = np.sort(months_amt_index)
        max_not_invoiced = 0  # 最大无开票信息月份
        #print(invoiced_data)
        # print("invoiced_data:%s"%invoiced_data)
        if len(invoiced_data) > 1:
            mid_invoiced = invoiced_data[0][0]
            for i in invoiced_data[1:]:
                if i[0] - mid_invoiced > max_not_invoiced:
                    max_not_invoiced = i[0] - mid_invoiced
                mid_invoiced = i[0]
        else:
            max_not_invoiced = max_not_invoiced

        if max_not_invoiced > 0:
            array_month_data[count, 32] = max_not_invoiced - 1
        else:
            array_month_data[count, 32] = max_not_invoiced

        #print("max_not_invoiced:%s" % max_not_invoiced)

        # print("months_amt_index:%s"%months_amt_index)
        if len(months_amt_index) > 0:
            real_months_len = (months_amt_index[-1] - months_amt_index[0])[0] + 1  # 实际月份

            if real_months_len == 12:
                array_month_data[count, 12] = 1
                array_month_data[count, 13] = 1
                array_month_data[count, 20] = real_months_amt.sum()
            elif real_months_len > 6:
                array_month_data[count, 13] = 1
                array_month_data[count, 20] = real_months_amt.sum() / real_months_len * 12

            else:
                array_month_data[count, 20] = real_months_amt.sum()

            # print(real_months_amt.sum(), real_months_len)
            # print(array_month_data[count, 20])
            array_month_data[count,31] = real_months_len
            # print("real_months_len:%s"%real_months_len)

        #21红冲占比（红冲／有效总）
        if array_month_data[count,16] != 0:
            array_month_data[count,21] = round(abs(array_month_data[count,14])/array_month_data[count,16],4) * 100 #百分制占比
        else:
            array_month_data[count,21] = 0   

        #22作废金额占比（作废／总发票）
        if total_invoice_sum != 0:
            array_month_data[count,22] = round(array_month_data[count,15]/total_invoice_sum ,4) * 100 #百分制占比
        else:
            array_month_data[count,22] = 0
            
        #23.增税占比（增／总税额）
        if array_month_data[count,19] != 0:
            array_month_data[count,23] = round(array_month_data[count,18]/array_month_data[count,19] ,4) * 100 #百分制占比
        else:
            array_month_data[count,23] = 0  

       # 24 有效发票总数	 25无效发票总数	 26有效红冲发票总数	 27 发票总数 
        array_month_data[count,24] = valid_invoice_count
        array_month_data[count,25] = invalid_invoice_count
        array_month_data[count,26] = red_invoice_count
        array_month_data[count,27] = total_invoice_count

        #28 无效票占比
        if array_month_data[count,27] != 0:
            array_month_data[count,28] = round(array_month_data[count,25]/array_month_data[count,27],4) * 100 #百分制占比
        else:
            array_month_data[count,28] = 0  
        # 29红冲票占比
        if array_month_data[count,24] != 0:
            array_month_data[count,29] = round(array_month_data[count,26]/array_month_data[count,24],4) * 100 #百分制占比
        else:
            array_month_data[count,29] = 0  
        
        count += 1 
    return pd.DataFrame(array_month_data)


def cal_statistic_invoices(df,apply_credit_limit):
    """
    函数说明：计算相关税务统计值，用于建模(调用了本模块中的其他函数)。
            包括：applyId、estimate_income、
            avr_red_invoice_rate、avr_invalid_invoice_rate、avr_value_added_tax_rate、
            avr_invalid_invoice_poll_rate、avr_red_invoice__poll_rate	
            AAGR、CGAR、stability_quarter、Label
            
    输入：传入包含企业相关税务统计信息的dataframe
    输出：返回Dataframe，包含建模所需的若干值
    """
    import pandas as pd
    import numpy as np
    statistic_invoices = pd.DataFrame()  # 存储建模 - 统计值

    # 提取对应年份
    df[["year", "month"]] = df["months"].str.split("-", 2, True)
    for i in df["apply_id"].unique():
        dict_invoices = {}  # 存数据

        tmp_data = Cal_Invoices_Annual(df[df["apply_id"] == i])
        integrity_data = tmp_data[tmp_data.iloc[:, 12:13].values > 0]
        integrity_year_len = len(integrity_data) #年完整数

        ### 判断是否需要计算信用评分和授信额度
        Judge_type,pass_type = Judge_taxData_level(tmp_data,apply_credit_limit)
        if(Judge_type):
            dict_invoices["apply_id"] = i
            dict_invoices["max_invoicedNot_months"] = np.max(tmp_data.iloc[:, 32:33].values)

            # 求平均   21红冲占比、22作废占比 、23增税占比
            dict_invoices["rate_red_invoice_sum"] = np.mean(tmp_data.iloc[:, 21:22].values)
            dict_invoices["rate_invalid_invoice_sum"] = np.mean(tmp_data.iloc[:, 22:23].values)
            # print("tmp_data.iloc[:,22:23].values:%s" % tmp_data.iloc[:, 22:23].values)
            dict_invoices["rate_value_added_tax_sum"] = np.mean(tmp_data.iloc[:, 23:24].values)

            # 28 平均  无效票数占比 29红冲票数占比
            dict_invoices["rate_invalid_invoice_poll"] = np.mean(tmp_data.iloc[:, 28:29].values)
            dict_invoices["rate_red_invoice_poll"] = np.mean(tmp_data.iloc[:, 29:30].values)

            # 规则：若无完整年则不计算AAGR、CGAR \季度稳定性  并将其设为 Null
            # return tmp_data

            # print("integrity_year_len%s"%integrity_year_len)
            if integrity_year_len > 0:  # 完整年
                dict_invoices["Label"] = str(integrity_year_len) + "年完整数据"
                if integrity_year_len == 1:
                    dict_invoices["estimate_income"] = round(integrity_data[20].values[0], 2)  # 年化收入
                else:
                    dict_invoices["estimate_income"] = np.mean(integrity_data.iloc[:, 20:21].values)
                # print(integrity_data.iloc[:, 20:21].values)
                dict_invoices["AAGR"] = Cal_AAGR(integrity_data.iloc[:, 20:21].values)
                dict_invoices["CGAR"] = Cal_CGAR(integrity_data.iloc[:, 20:21].values)
            else:
                dict_invoices["Label"] = "无年完整数据"
                integrity_data = tmp_data[tmp_data.iloc[:,13:14].values > 0]
                if len(integrity_data) > 0:  # 超过6个月
                    dict_invoices["estimate_income"] = np.mean(integrity_data.iloc[:, 20:21].values)
                    dict_invoices["AAGR"] = Cal_AAGR(integrity_data.iloc[:, 20:21].values)
                    dict_invoices["CGAR"] = Cal_CGAR(integrity_data.iloc[:, 20:21].values)

                else:  # 无多于6个月的年度，但是两年有连续的多于6个月的记录
                    integrity_data = pd.DataFrame(tmp_data).iloc[-2:, :]
                    if len(integrity_data.iloc[:, 0:12].values[integrity_data.iloc[:, 0:12].values > 0]) > 6:
                        # print("++++,%s"%(integrity_data.iloc[:,20:21]))
                        # print("======"*12)
                        dict_invoices["estimate_income"] = np.sum(integrity_data.iloc[:,20:21]) /np.sum(integrity_data.iloc[:,31:32]) * 12

                    else:
                        dict_invoices["estimate_income"] = np.sum(tmp_data.iloc[:,20:21].values)
                        # print("++++,%s" % (integrity_data.iloc[:, 20:21]))
                        # print("======" * 12)
                    dict_invoices["AAGR"] = "Null"
                    dict_invoices["CGAR"] = "Null"


            # integrity_data = tmp_data[tmp_data.iloc[:, 20:21].values > 0] #取年收入大于零
            # #print("integrity_data:%s"%integrity_data)
            # dict_invoices["AAGR"] = Cal_AAGR(integrity_data.iloc[:, 20:21].values)
            # dict_invoices["CGAR"] = Cal_CGAR(integrity_data.iloc[:, 20:21].values)
            # 求季度稳定性
            quarter_all = tmp_data.iloc[:, 0:12]
            dict_invoices["stability_quarter"] = Cal_Stability_Quarter(quarter_all)
            # print("CGAR:%s" % dict_invoices["CGAR"])
            # print("AAGR:%s" % dict_invoices["AAGR"])

            statistic_invoices = pd.concat([statistic_invoices, pd.DataFrame(dict_invoices, index=[0])])
            statistic_invoices.index = range(1, len(statistic_invoices) + 1)

        else:
            return 0,pass_type
    return statistic_invoices,pass_type