package com.samsthenerd.hexgloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexGloop {
    public static final String MOD_ID = "hexgloop";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final void logPrint(String message){
		LOGGER.info(message);
	}


    public static void onInitialize() {

    }
}
