package com.github.imdabigboss.kitduels.common.util;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

public interface InventorySerialization {
    String[] playerInventoryToBase64(CommonPlayer player) throws IllegalStateException;
}
