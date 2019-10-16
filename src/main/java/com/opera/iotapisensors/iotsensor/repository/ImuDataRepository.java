package com.opera.iotapisensors.iotsensor.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.opera.iotapisensors.iotsensor.entities.ImuData;

@Repository
public interface ImuDataRepository extends JpaRepository<ImuData, Long> {

  @Query("select imu from ImuData imu where imu.measuredAt between :startDate and :endDate and imu.deviceId = :deviceId AND imu.historic = true order by imu.measuredAt")
  List<ImuData> getImuDataByDeviceIdWithTimeSpan(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
  
  @Query("SELECT imu from ImuData imu WHERE imu.deviceId = :deviceId AND imu.measuredAt = :measuredAt AND historic = true")
  List<ImuData> getDataByDeviceIdAndMeasuredAt(@Param("deviceId") String deviceId,
      @Param("measuredAt") LocalDateTime date);
  
  @Query(
      value = "SELECT MEASUREDAT_UTC FROM IOT_IMU a WHERE a.DEVICEID = :deviceId AND HISTORIC = TRUE ORDER BY MEASUREDAT_UTC DESC FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  LocalDateTime getDateOfLatestEntryByDevice(@Param("deviceId") String deviceId);
}
