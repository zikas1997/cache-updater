package ru.zikas1997.cacheupdater.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqMessage {
    String[] nameCaches;
}
