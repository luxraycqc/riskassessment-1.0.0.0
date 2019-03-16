# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
import matplotlib.pyplot as plt
import numpy as np  
from sklearn import metrics 
from sklearn.metrics import auc
import csv

#读取数据
with open('A.csv', 'r') as csvfile:
    reader1 = csv.DictReader(csvfile)
    y = [row['result'] for row in reader1]
    
with open('A.csv', 'r') as csvfile:
    reader2 = csv.DictReader(csvfile)
    scores = [row['p'] for row in reader2]
    
y = np.array(y, dtype = np.float64)
scores = np.array(scores, dtype = np.float64)

#计算fpr，tpr，thresholds及auc
fpr, tpr, thresholds = metrics.roc_curve(y, scores, pos_label=1)
auc = metrics.auc(fpr, tpr) 

#声明数组
len_thres = len(thresholds) #阈值类数
len_samps = len(y)          #样本个数

tp = np.zeros(len_thres, dtype=np.int64)
fn = np.zeros(len_thres, dtype=np.int64)
fp = np.zeros(len_thres, dtype=np.int64)
tn = np.zeros(len_thres, dtype=np.int64)

precision = np.zeros(len_thres) # tp / (tp + fp)
lift = np.zeros(len_thres) # [tp / (tp + fp)] / [(tp + fn) / (tp + fp + tn + fn)]
depth = np.zeros(len_thres) #(tp + fp) / (tp + fn + fp + tn)
fpr1 = np.zeros(len_thres)

#计算tp，fn,fp,tn,depth,precision,lift
for i in range(len_thres):
    tmp_tp = 0
    tmp_fn = 0
    tmp_fp = 0
    tmp_tn = 0
    for j in range(len_samps):
        if y[j] == 1 and scores[j] >= thresholds[i]:
            tmp_tp += 1
        elif y[j] == 1 and scores[j] < thresholds[i]:
            tmp_fn += 1
        elif y[j] == 0 and scores[j] >= thresholds[i]:
            tmp_fp += 1
        else: tmp_tn += 1
        
    tp[i] = tmp_tp
    fn[i] = tmp_fn
    fp[i] = tmp_fp
    tn[i] = tmp_tn
    
    depth[i] = (tp[i] + fp[i]) / (tp[i] + fp[i] + fn[i] + tn[i])
    precision[i] = tp[i] / (tp[i] + fp[i])
    lift[i] = precision[i] / ((tp[i] + fn[i]) / (tp[i] + fn[i] + tn[i] + fp[i]))
print ("AUC = ", auc)
#画图
plt.plot(tpr,precision,linewidth=2,label="P-R")
plt.xlabel("Recall / TPR")
plt.ylabel("Precision / PV_plus")
plt.ylim(0.45,.7)
plt.xlim(0,1.05)
plt.legend(loc=1)#图例的位置plt.show()
    
plt.plot(depth,precision,linewidth=2,label="Gains")
plt.xlabel("Depth")
plt.ylabel("Precision / PV_plus")
plt.ylim(0.45,0.7)
plt.xlim(0,1.05)
plt.legend(loc=1)#图例的位置plt.show() 

plt.plot(depth,lift,linewidth=2,label="Lift")
plt.xlabel("Depth")
plt.ylabel("Lift")
plt.ylim(0.95,1.35)
plt.xlim(0,1.05)
plt.legend(loc=1)#图例的位置plt.show()  

plt.plot(fpr,tpr,linewidth=2,label="ROC")
plt.xlabel("false presitive rate / FPR")
plt.ylabel("true presitive rate / TPR")
plt.ylim(0,1.05)
plt.xlim(0,1.05)
plt.legend(loc=4)#图例的位置plt.show()    






