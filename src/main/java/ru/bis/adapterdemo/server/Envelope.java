package ru.bis.adapterdemo.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlRootElement(name = "Envelope")
public class Envelope {
    String name;
    Integer code;
    String addInfo;
}
