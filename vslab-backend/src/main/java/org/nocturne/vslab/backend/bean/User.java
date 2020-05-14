package org.nocturne.vslab.backend.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private Integer id;

    private String name;

    /**
     * 注意该字段为密码加密后的密文
     */
    private String password;
}
