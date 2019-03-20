package cn.net.aichain.edge.ms.jpa.etl;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface EtlJobInfoDao extends JpaRepository<EtlJobInfo, String> {
    List<EtlJobInfo> findAll();
    List<EtlJobInfo> findAllByName(String name);

    @Transactional
    @Modifying
    @Query("UPDATE EtlJobInfo SET readStatus=1 WHERE id=:id")
    void updateReadStatusById(@Param("id") long id);

    @Transactional
    @Modifying
    @Query("UPDATE EtlJobInfo SET jobStatus=1 WHERE id=:id")
    void updateJobStatusById(@Param("id") long id);

    @Transactional
    @Modifying
    @Query("UPDATE EtlJobInfo SET content=:content WHERE id=:id")
    void updateMiddleContentById(@Param("content") String content, @Param("id") long id);

    @Transactional
    @Modifying
    @Query("UPDATE EtlJobInfo SET content=:content, jobStatus=1 WHERE id=:id")
    void updateContentById(@Param("content") String content, @Param("id") long id);
}
