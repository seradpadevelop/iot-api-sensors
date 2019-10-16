package com.opera.iotapisensors.iotsensor.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.opera.iotapisensors.iotsensor.entities.IotInclinometerData;

@Repository
public interface InclinometerDataRepository extends JpaRepository<IotInclinometerData, Long> {

  @Query("SELECT inc from IotInclinometerData inc WHERE inc.deviceId = :deviceId AND historic = true ORDER BY measuredAt")
  List<IotInclinometerData> getInclinometerDataByDeviceId(@Param("deviceId") String deviceId);

  @Query("SELECT MAX(inc.measuredAt) from IotInclinometerData inc WHERE inc.deviceId = :deviceId AND historic = true")
  LocalDateTime getDateOfLatestEntryByDevice(@Param("deviceId") String deviceId);

  @Query("SELECT AVG(inc.rollValue) from IotInclinometerData inc WHERE inc.deviceId = :deviceId AND inc.measuredAt BETWEEN :startDate AND :endDate AND historic = true")
  Float getAvgRollValueBetweenMeasuredAtByDeviceId(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  @Query("SELECT AVG(inc.pitchValue) from IotInclinometerData inc WHERE inc.deviceId = :deviceId AND inc.measuredAt BETWEEN :startDate AND :endDate AND historic = true")
  Float getAvgPitchValueBetweenMeasuredAtByDeviceId(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  @Query("SELECT inc from IotInclinometerData inc WHERE inc.deviceId = :deviceId AND inc.measuredAt BETWEEN :startDate AND :endDate AND historic = true ORDER BY measuredAt")
  List<IotInclinometerData> getInclinometerDataWithTimeSpan(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
  
  @Query("SELECT inc from IotInclinometerData inc WHERE inc.deviceId = :deviceId AND inc.measuredAt = :measuredAt AND historic = true")
  List<IotInclinometerData> getDataByDeviceIdAndMeasuredAt(@Param("deviceId") String deviceId,
      @Param("measuredAt") LocalDateTime date);

  @Query(
      value = "Select roll_mavg from iot_inklinometer where deviceId = :deviceId and measuredAt between :startDate and :endDate AND historic = true ORDER BY measuredAt DESC FETCH FIRST 1 ROWS ONLY",
      nativeQuery = true)
  Float getLatestRollMavgBetweenMeasuredAtByDeviceId(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  @Query(
      value = "Select pitch_mavg from iot_inklinometer where deviceId = :deviceId and measuredAt between :startDate and :endDate AND historic = true ORDER BY measuredAt DESC FETCH FIRST 1 ROWS ONLY",
      nativeQuery = true)
  Float getLatestPitchMavgBetweenMeasuredAtByDeviceId(@Param("deviceId") String deviceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
