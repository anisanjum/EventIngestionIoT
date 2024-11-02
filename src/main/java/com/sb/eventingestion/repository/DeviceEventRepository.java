package com.sb.eventingestion.repository;


import com.sb.eventingestion.entity.DeviceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceEventRepository extends JpaRepository<DeviceEvent, Long> {

}
