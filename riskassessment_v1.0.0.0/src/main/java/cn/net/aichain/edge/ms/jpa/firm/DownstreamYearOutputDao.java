package cn.net.aichain.edge.ms.jpa.firm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownstreamYearOutputDao extends JpaRepository<DownstreamYearOutput,Long> {
    List<DownstreamYearOutput> findAll();

    List<DownstreamYearOutput> findByApplyId(String applyId);

}
