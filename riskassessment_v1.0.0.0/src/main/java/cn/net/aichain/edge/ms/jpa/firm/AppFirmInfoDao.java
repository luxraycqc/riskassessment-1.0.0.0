package cn.net.aichain.edge.ms.jpa.firm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface AppFirmInfoDao extends JpaRepository<AppFirmInfo, String> {

    List<AppFirmInfo> findAll();

    AppFirmInfo findByApplyId(String applyId);
}
