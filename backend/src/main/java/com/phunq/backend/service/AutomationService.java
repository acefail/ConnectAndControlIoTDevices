package com.phunq.backend.service;

import com.phunq.backend.adafruit.AdafruitService;
import com.phunq.backend.entity.Feed;
import com.phunq.backend.entity.FeedGroup;
import com.phunq.backend.service.entity.FeedGroupService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author phunq3107
 * @since 2/26/2022
 */
@Service
@Slf4j
public class AutomationService {

  @Value("${adafruit.threshold.sound}")
  public Integer THRESHOLD_SOUND;

  private final AdafruitService adafruitService;
  private final FeedGroupService feedGroupService;

  public AutomationService(AdafruitService adafruitService,
      FeedGroupService feedGroupService) {
    this.adafruitService = adafruitService;
    this.feedGroupService = feedGroupService;
  }

  public void doAutomationTask() throws IOException {
    List<FeedGroup> groups = feedGroupService.findAll();
    for (FeedGroup group : groups) {
      if (!group.getEnableAutomation()) {
        continue;
      }
      handleTaskRelatedToTemperatureSensor(group);
      handleTaskRelatedToSoundSensor(group);
    }
  }

  public void handleTaskRelatedToTemperatureSensor(FeedGroup group) throws IOException {
    Feed temperatureSensor = group.getTemperatureSensor();
    if (temperatureSensor == null) {
      return;
    }
    int currentTemperature = temperatureSensor.getCurrentValueAsInt();
    if (currentTemperature < group.getLowerTemperatureThreshold()
        && group.getLight().getCurrentValueAsInt() == 0) {
      log.warn(String.format(
          "Automation task [Group %s]: Current temperature (%d) < %d => turn on light",
          group.getName(),
          currentTemperature,
          group.getLowerTemperatureThreshold()
      ));
      if (group.getLight() != null) {
        adafruitService.addFeedValue(group.getLight().getKey(), "1");
      }

    } else if (currentTemperature > group.getUpperTemperatureThreshold()
        && group.getLight().getCurrentValueAsInt() != 0) {
      log.warn(String.format(
          "Automation task [Group %s]: Current temperature (%d) > %d => turn off light",
          group.getName(),
          currentTemperature,
          group.getUpperTemperatureThreshold()
      ));
      if (group.getLight() != null) {
        adafruitService.addFeedValue(group.getLight().getKey(), "0");
      }
    }
  }

  public void handleTaskRelatedToSoundSensor(FeedGroup group) throws IOException {
    Feed soundSensor = group.getSoundSensor();
    if (soundSensor == null) {
      return;
    }
    int currentSound = soundSensor.getCurrentValueAsInt();
    if (currentSound > THRESHOLD_SOUND) {
      log.warn(String.format("Automation task [Group %s]: Have hatched egg", group.getName()));
    }
  }
}
