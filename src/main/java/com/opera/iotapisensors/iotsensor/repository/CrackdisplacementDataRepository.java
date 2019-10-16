package com.opera.iotapisensors.iotsensor.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.opera.iotapisensors.iotsensor.entities.IotCrackSensorData;

@Repository
public interface CrackdisplacementDataRepository extends JpaRepository<IotCrackSensorData, Long> {

  @Query("select crk from IotCrackSensorData crk where crk.measuredAt between :startDate and :endDate order by crk.measuredAt")
  List<IotCrackSensorData> getCrackdisplacementData(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("select crk from IotCrackSensorData crk where crk.measuredAt between :startDate and :endDate and crk.deviceId = :deviceId AND crk.historic = true order by crk.measuredAt")
  List<IotCrackSensorData> getCrackdisplacementDataByDeviceId(
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
      @Param("deviceId") String deviceId);

  @Query(
      value = "SELECT MEASUREDAT_UTC FROM IOT_CRK WHERE HISTORIC = TRUE ORDER BY MEASUREDAT_UTC DESC FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  LocalDateTime getDataOfLatestEntry();

  @Query(
      value = "SELECT MEASUREDAT_UTC FROM IOT_CRK c WHERE c.DEVICEID = :deviceId AND HISTORIC = TRUE ORDER BY MEASUREDAT_UTC DESC FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  LocalDateTime getDataOfLatestEntryByDevice(@Param("deviceId") String deviceId);
  
  @Query("SELECT crk from IotCrackSensorData crk WHERE crk.deviceId = :deviceId AND crk.measuredAt = :measuredAt AND historic = true")
  List<IotCrackSensorData> getDataByDeviceIdAndMeasuredAt(@Param("deviceId") String deviceId,
      @Param("measuredAt") LocalDateTime date);

  @Query(
      value = "select * from IOT_CRK c where c.L = (select min(c.L) from IOT_CRK c where c.MEASUREDAT_UTC between :startDate and :endDate and c.DEVICEID = :deviceId) and c.MEASUREDAT_UTC between :startDate and :endDate and c.DEVICEID = :deviceId and c.historic = true order by c.MEASUREDAT_UTC desc FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  IotCrackSensorData getLowValue(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate, @Param("deviceId") String deviceId);

  @Query(
      value = "select * from IOT_CRK c where c.L = (select max(c.L) from IOT_CRK c where c.MEASUREDAT_UTC between :startDate and :endDate and c.DEVICEID = :deviceId) and c.MEASUREDAT_UTC between :startDate and :endDate and c.DEVICEID = :deviceId and c.historic = true order by c.MEASUREDAT_UTC desc FETCH FIRST 1 ROWS ONLY;",
      nativeQuery = true)
  IotCrackSensorData getHighValue(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate, @Param("deviceId") String deviceId);
}
