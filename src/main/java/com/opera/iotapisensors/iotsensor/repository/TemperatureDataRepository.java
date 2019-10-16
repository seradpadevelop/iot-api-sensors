package com.opera.iotapisensors.iotsensor.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.opera.iotapisensors.iotsensor.entities.TemperatureData;

@Repository
public interface TemperatureDataRepository extends JpaRepository<TemperatureData, Long> {

  @Query(
      value = "SELECT * FROM iot_temperature WHERE deviceId = :deviceId AND historic = true AND measuredAt_utc BETWEEN :startDate AND :endDate ORDER BY measuredAt_utc;",
      nativeQuery = true)
  List<TemperatureData> getTemperatureDataByDeviceIdWithTimeSpan(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
  
  @Query(
      value = "SELECT MEASUREDAT_UTC FROM IOT_TEMPERATURE a WHERE a.DEVICEID = :deviceId AND HISTORIC = TRUE ORDER BY MEASUREDAT_UTC DESC FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  LocalDateTime getDateOfLatestEntryByDevice(@Param("deviceId") String deviceId);
  
  @Query("SELECT t from TemperatureData t WHERE t.deviceId = :deviceId AND t.measuredAt = :measuredAt AND historic = true")
  List<TemperatureData> getDataByDeviceIdAndMeasuredAt(@Param("deviceId") String deviceId,
      @Param("measuredAt") LocalDateTime date);
}
