package com.simplefanc.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chenfan
 * @date 2022/10/24 22:40
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hello implements Serializable {
    private static final long serialVersionUID = -7970005053591210082L;

    private String message;
    private String description;
}
