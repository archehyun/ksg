package com.ksg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ¹ÚÃ¢Çö
 *
 */

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class KeyWordInfo {
	
	public String key_type;

	public String key_name;
}
