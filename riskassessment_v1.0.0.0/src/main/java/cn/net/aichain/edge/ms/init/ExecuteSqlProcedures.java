package cn.net.aichain.edge.ms.init;

import cn.hutool.log.Log;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import cn.hutool.log.LogFactory;
import cn.net.aichain.edge.ms.module.etl.dataloader.SqlProcedureIniter;

@Component
public class ExecuteSqlProcedures implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Log log = LogFactory.get();
        log.info("==========start initing project===========");
        SqlProcedureIniter.createTables();
        log.info("==========init project completed===========");
    }
}
