package com.epam.resourceservice.repository;

import com.epam.resourceservice.model.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author www.epam.com
 */
@Repository
public interface ResourceRepository extends CrudRepository<Resource, Integer> {
}
