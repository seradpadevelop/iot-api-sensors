package com.opera.iotapisensors.iotsensor.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.opera.iotapisensors.iotsensor.entities.AccelerometerData;

@Repository
public interface AccelerometerDataRepository extends JpaRepository<AccelerometerData, Long> {

  @Query(
      value = "select * from iot_accelerometer a where a.value1 = (select min(a.value1) from iot_accelerometer a where a.measuredAt_utc between :startDate and :endDate and a.deviceid = :deviceId)\n"
          + "and a.measuredAt_utc between :startDate and :endDate and a.deviceid = :deviceId order by a.measuredAt_utc desc FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  AccelerometerData getLowValue(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate, @Param("deviceId") String deviceId);

  @Query(
      value = "select * from iot_accelerometer a where a.value1 = (select max(a.value1) from iot_accelerometer a where a.measuredAt_utc between :startDate and :endDate and a.deviceId = :deviceId) \n"
          + "and a.measuredAt_utc between :startDate and :endDate and a.deviceId = :deviceId order by a.measuredAt_utc desc FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  AccelerometerData getHighValue(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate, @Param("deviceId") String deviceId);

  @Query("SELECT a from AccelerometerData a WHERE a.deviceId = :deviceId AND a.measuredAt BETWEEN :startDate AND :endDate AND HISTORIC = TRUE ORDER BY measuredAt")
  List<AccelerometerData> getAccelerometerDataByDeviceIdWithTimeSpan(
      @Param("deviceId") String deviceId, @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
  
  @Query("SELECT a from AccelerometerData a WHERE a.deviceId = :deviceId AND a.measuredAt = :measuredAt AND historic = true")
  List<AccelerometerData> getDataByDeviceIdAndMeasuredAt(@Param("deviceId") String deviceId,
      @Param("measuredAt") LocalDateTime date);

  @Query(
      value = "SELECT MEASUREDAT_UTC FROM IOT_ACCELEROMETER a WHERE a.DEVICEID = :deviceId AND HISTORIC = TRUE ORDER BY MEASUREDAT_UTC DESC FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  LocalDateTime getDateOfLatestEntryByDevice(@Param("deviceId") String deviceId);
}
