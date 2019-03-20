package cn.net.aichain.edge.ms.module.firm;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.foundation.os.OSUtil;
import cn.net.aichain.edge.ms.jpa.firm.DownstreamYearOutputDao;
import cn.net.aichain.edge.ms.jpa.firm.FirmMonthsOutputDao;
import cn.net.aichain.edge.ms.message.WebMessage;
import cn.net.aichain.edge.ms.module.etl.dataloader.CreditScore;

import cn.net.aichain.edge.ms.module.etl.dataloader.DataLoader;
import com.alibaba.druid.pool.DruidDataSource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/creditScore")
@Api(tags = "3.信用评分接口")
public class CreditScoreRest {
    @Autowired
    DownstreamYearOutputDao downstreamYearOutputDao;
    @Autowired
    FirmMonthsOutputDao firmMonthsOutputDao;
    @Autowired
    DataLoader dataLoader;

    // 传入企业的申请id 获得结果
    @RequestMapping(value = "/getScoreResult",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId",value = "申请id",defaultValue ="0973D35C50DB4972858E97AF5D79CD6B" ,required = true)
    })
    public WebMessage getScoreResult(final String applyId) throws Exception{
        final WebMessage msg = new WebMessage();
        Props props = new Props("config/etl.properties");
        String destUrl = props.getStr("destdb.main.mysql.jdbcUrl");
        String destUsername = props.getStr("destdb.main.mysql.username");
        String destPassword = props.getStr("destdb.main.mysql.password");
        DruidDataSource destDs = new DruidDataSource();
        destDs.setUrl(destUrl);
        destDs.setUsername(destUsername);
        destDs.setPassword(destPassword);
        Db destDb = DbUtil.use(destDs);
        // 进行计算
        Entity result=CreditScore.calculateCreditScore(applyId, destDb);

        if (result==null){
            msg.msg.put("msg","0");
            msg.msg.put("code","缺少该企业数据");
        }
        else {
            msg.msg.put("msg","1");
            msg.msg.put("data",result);
        }
        destDs.close();
        return msg;
    }

    @RequestMapping(value = "/getScoreTest",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "today",value = "指定日期",defaultValue ="20181108" ,required = true)
    })
    public WebMessage getScoreTest(final String today) throws Exception{
        final WebMessage msg = new WebMessage();
        dataLoader.loadFirmInfo(today);
        return msg;
    }
}
