package com.oakey.whisky.repository;

import com.oakey.whisky.domain.Whisky;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WhiskyRepository extends JpaRepository<Whisky, Integer> {

    Optional<Whisky> findByWsName(String wsName);

    boolean existsByWsName(String wsName);

    @Query(value = """
        SELECT *
        FROM (
            SELECT w.WS_ID AS wsId,
                   (
                       GREATEST(
                           UTL_MATCH.JARO_WINKLER_SIMILARITY(LOWER(w.WS_NAME), LOWER(:qName)),
                           UTL_MATCH.JARO_WINKLER_SIMILARITY(LOWER(NVL(w.WS_DISTILLERY, '')), LOWER(NVL(:qDist, ''))),
                           UTL_MATCH.JARO_WINKLER_SIMILARITY(LOWER(NVL(w.WS_NAME_KO, '')), LOWER(NVL(:qNameKo, '')))
                       )
                       + CASE
                           WHEN :qAge IS NOT NULL AND w.WS_AGE = :qAge THEN 5
                           ELSE 0
                         END
                   ) AS score
            FROM TB_WHISKY w
            WHERE (
                LOWER(w.WS_NAME) LIKE LOWER(:prefix) || '%'
                OR LOWER(NVL(w.WS_DISTILLERY, '')) LIKE LOWER(:prefix) || '%'
                OR LOWER(NVL(w.WS_NAME_KO, '')) LIKE LOWER(:prefix) || '%'
            )
        )
        ORDER BY score DESC
        FETCH FIRST 3 ROWS ONLY
        """, nativeQuery = true)
    List<WhiskySimilarityRow> findTop3ByJaroWinkler(
        @Param("qName") String qName,
        @Param("qDist") String qDist,
        @Param("qNameKo") String qNameKo,
        @Param("qAge") Integer qAge,
        @Param("prefix") String prefix
    );
}
