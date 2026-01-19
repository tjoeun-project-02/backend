package com.oakey.flavor.repository;

import com.oakey.flavor.domain.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlavorRepository extends JpaRepository<Flavor, Integer> {

    List<Flavor> findByWsId(Integer wsId);

    List<Flavor> findByKeywordId(Integer keywordId);
}
